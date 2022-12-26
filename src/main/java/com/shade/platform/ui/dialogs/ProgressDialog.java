package com.shade.platform.ui.dialogs;

import com.shade.platform.model.data.DataKey;
import com.shade.platform.model.runtime.ProgressMonitor;
import com.shade.util.NotNull;
import com.shade.util.Nullable;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class ProgressDialog extends BaseDialog {
    private static final DataKey<ProgressMonitor.IndeterminateTask> TASK_KEY = new DataKey<>("task", ProgressMonitor.IndeterminateTask.class);
    private static final int INDETERMINATE = -1;

    private final JPanel taskPanel;
    private final ProgressMonitorListener listener;
    private final SwingWorker<Object, Exception> executor;

    private ProgressDialog(@NotNull String title, @NotNull Worker<?, ?> worker) {
        super(title, List.of(BUTTON_CANCEL));
        this.taskPanel = new JPanel();
        this.taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.PAGE_AXIS));

        this.listener = new ProgressMonitorListener() {
            @Override
            public void taskBegin(@NotNull ProgressMonitor.IndeterminateTask task, int ticks) {
                taskPanel.add(new TaskComponent(task, ticks));
                taskPanel.revalidate();
                taskPanel.repaint();
            }

            @Override
            public void taskEnd(@NotNull ProgressMonitor.IndeterminateTask task) {
                taskPanel.remove(findTaskComponent(task));
                taskPanel.revalidate();
                taskPanel.repaint();
            }

            @Override
            public void taskWorked(@NotNull ProgressMonitor.Task task, int ticks) {
                findTaskComponent(task).worked(ticks);
            }

            @NotNull
            private TaskComponent findTaskComponent(@NotNull ProgressMonitor.IndeterminateTask task) {
                final int count = taskPanel.getComponentCount();

                for (int i = 0; i < count; i++) {
                    final TaskComponent component = (TaskComponent) taskPanel.getComponent(i);
                    final ProgressMonitor.IndeterminateTask other = TASK_KEY.get(component);

                    if (other == task) {
                        return component;
                    }
                }

                throw new IllegalArgumentException("Can't find component for the given task");
            }
        };

        this.executor = new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                return worker.doInBackground(new MyProgressMonitor(listener));
            }

            @Override
            protected void done() {
                close();
            }
        };

    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T, E extends Exception> Optional<T> showProgressDialog(@Nullable Window owner, @NotNull String title, @NotNull Worker<T, E> worker) throws E {
        final ProgressDialog dialog = new ProgressDialog(title, worker);
        final SwingWorker<Object, Exception> executor = dialog.executor;

        if (dialog.showDialog(owner) == BUTTON_CANCEL) {
            executor.cancel(true);
        }

        try {
            return Optional.ofNullable((T) executor.get());
        } catch (ExecutionException e) {
            throw (E) e.getCause();
        } catch (CancellationException | InterruptedException e) {
            return Optional.empty();
        }
    }

    @NotNull
    @Override
    protected JComponent createContentsPane() {
        final JScrollPane pane = new JScrollPane(taskPanel);
        pane.setPreferredSize(new Dimension(420, 200));
        return pane;
    }

    @NotNull
    @Override
    protected JDialog createDialog(@Nullable Window owner) {
        final JDialog dialog = super.createDialog(owner);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                executor.execute();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (executor.isDone()) {
                    dialog.dispose();
                }
            }
        });

        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        return dialog;
    }

    @Nullable
    @Override
    protected ButtonDescriptor getDefaultButton() {
        return BUTTON_CANCEL;
    }

    public interface Worker<T, E extends Exception> {
        T doInBackground(@NotNull ProgressMonitor monitor) throws E;
    }

    private static class TaskComponent extends JComponent {
        private final ProgressMonitor.IndeterminateTask task;
        private final int total;

        private final JLabel label;
        private final JProgressBar progressBar;

        private int worked = 0;

        public TaskComponent(@NotNull ProgressMonitor.IndeterminateTask task, int total) {
            this.task = task;
            this.total = total;

            setLayout(new MigLayout("ins panel", "[grow,fill]", "[][]"));
            add(label = new JLabel(), "wrap");
            add(progressBar = new JProgressBar());

            if (total != INDETERMINATE) {
                label.setText("%s (0/%d)".formatted(task.title(), total));
                progressBar.setMaximum(total);
            } else {
                label.setText(task.title());
                progressBar.setIndeterminate(true);
            }

            putClientProperty(TASK_KEY, task);
        }

        @Override
        public Dimension getMaximumSize() {
            final Dimension size = getPreferredSize();
            return new Dimension(Short.MAX_VALUE, size.height);
        }

        public void worked(int ticks) {
            if (worked + ticks > total) {
                throw new IllegalArgumentException("Too many work to do");
            }

            worked += ticks;

            progressBar.setValue(worked);
            label.setText("%s (%d/%d)".formatted(task.title(), worked, total));
        }
    }

    private static class MyProgressMonitor implements ProgressMonitor {
        protected final ProgressMonitorListener listener;

        public MyProgressMonitor(@NotNull ProgressMonitorListener listener) {
            this.listener = listener;
        }

        @NotNull
        @Override
        public IndeterminateTask begin(@NotNull String title) {
            return new MyProgressMonitorTask<>(this, title, INDETERMINATE);
        }

        @NotNull
        @Override
        public Task begin(@NotNull String title, int total) {
            return new MyProgressMonitorTask<>(this, title, total);
        }
    }

    private static class MySubProgressMonitor extends MyProgressMonitor {
        private final MyProgressMonitorTask<?> task;
        private final int provided;

        public MySubProgressMonitor(@NotNull ProgressMonitorListener listener, @NotNull MyProgressMonitorTask<?> task, int provided) {
            super(listener);
            this.task = task;
            this.provided = provided;
        }

        @NotNull
        @Override
        public IndeterminateTask begin(@NotNull String title) {
            return new MySubProgressMonitorTask(this, title, INDETERMINATE);
        }

        @NotNull
        @Override
        public Task begin(@NotNull String title, int total) {
            return new MySubProgressMonitorTask(this, title, total);
        }
    }

    private static class MyProgressMonitorTask<T extends MyProgressMonitor> implements ProgressMonitor.Task {
        protected final T monitor;
        private final String title;

        private MyProgressMonitorTask(@NotNull T monitor, @NotNull String title, int total) {
            this.monitor = monitor;
            this.title = title;
            this.monitor.listener.taskBegin(this, total);
        }

        @NotNull
        @Override
        public ProgressMonitor split(int ticks) {
            return new MySubProgressMonitor(monitor.listener, this, ticks);
        }

        @Override
        public void worked(int ticks) {
            monitor.listener.taskWorked(this, ticks);
        }

        @Override
        public void close() {
            monitor.listener.taskEnd(this);
        }

        @NotNull
        @Override
        public String title() {
            return title;
        }
    }

    private static class MySubProgressMonitorTask extends MyProgressMonitorTask<MySubProgressMonitor> {
        private MySubProgressMonitorTask(@NotNull MySubProgressMonitor monitor, @NotNull String title, int total) {
            super(monitor, title, total);
        }

        @Override
        public void close() {
            monitor.task.worked(monitor.provided);
            super.close();
        }
    }

    private interface ProgressMonitorListener {
        void taskBegin(@NotNull ProgressMonitor.IndeterminateTask task, int ticks);

        void taskEnd(@NotNull ProgressMonitor.IndeterminateTask task);

        void taskWorked(@NotNull ProgressMonitor.Task task, int ticks);
    }
}
