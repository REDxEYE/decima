package com.shade.decima.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatSearchWithHistoryIcon;
import com.shade.decima.model.app.Project;
import com.shade.decima.model.packfile.Packfile;
import com.shade.decima.model.packfile.PackfileBase;
import com.shade.decima.ui.Application;
import com.shade.decima.ui.editor.FileEditorInputLazy;
import com.shade.platform.model.runtime.ProgressMonitor;
import com.shade.platform.ui.controls.ColoredListCellRenderer;
import com.shade.platform.ui.controls.TextAttributes;
import com.shade.platform.ui.dialogs.ProgressDialog;
import com.shade.platform.ui.util.UIUtils;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindFilesDialog extends JDialog {
    public enum Strategy {
        FIND_MATCHING("Find matching\u2026", "Enter part of a file name or path", UIManager.getIcon("Action.containsIcon")),
        FIND_REFERENCED_BY("Find referenced by\u2026", "Enter full path to a file to find files that reference it", UIManager.getIcon("Action.exportIcon")),
        FIND_REFERENCES_TO("Find references to\u2026", "Enter full path to a file to find files that are referenced by it", UIManager.getIcon("Action.importIcon"));

        private final String label;
        private final String placeholder;
        private final Icon icon;

        Strategy(@NotNull String label, @NotNull String placeholder, @NotNull Icon icon) {
            this.label = label;
            this.placeholder = placeholder;
            this.icon = icon;
        }
    }

    private static final WeakHashMap<Project, WeakReference<FileInfoIndex>> CACHE = new WeakHashMap<>();
    private static final WeakHashMap<Project, Deque<HistoryRecord>> HISTORY = new WeakHashMap<>();
    private static final int HISTORY_LIMIT = 10;

    private final Project project;

    private final JComboBox<Strategy> strategyCombo;
    private final JTextField inputField;
    private final JTable resultsTable;

    public static void show(@NotNull JFrame frame, @NotNull Project project, @NotNull Strategy strategy, @Nullable String query) {
        FileInfoIndex index = null;

        if (CACHE.containsKey(project)) {
            final WeakReference<FileInfoIndex> ref = CACHE.get(project);
            if (ref != null) {
                index = ref.get();
            }
        }

        if (index == null) {
            try {
                index = ProgressDialog
                    .showProgressDialog(frame, "Build file info index", monitor -> buildFileInfoIndex(monitor, project))
                    .orElse(null);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            if (index != null) {
                CACHE.put(project, new WeakReference<>(index));
            }
        }

        if (index == null) {
            return;
        }

        new FindFilesDialog(frame, project, strategy, index, query).setVisible(true);
    }

    private FindFilesDialog(@NotNull JFrame frame, @NotNull Project project, @NotNull Strategy initialStrategy, @NotNull FileInfoIndex index, @Nullable String query) {
        super(frame, "Find Files in '%s'".formatted(project.getContainer().getName()), true);
        this.project = project;

        resultsTable = new JTable(new FilterableTableModel(index));
        resultsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setFocusable(false);
        resultsTable.getColumnModel().getColumn(0).setMaxWidth(100);
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() % 2 == 0) {
                    final int row = resultsTable.rowAtPoint(e.getPoint());

                    if (row >= 0) {
                        final FilterableTableModel model = (FilterableTableModel) resultsTable.getModel();
                        final FileInfo info = model.getValueAt(row);

                        openSelectedFile(project, info);

                        if (!e.isControlDown()) {
                            dispose();
                        }
                    }
                }
            }
        });

        inputField = new JTextField();
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 1, 0, UIManager.getColor("Separator.shadow")),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshResults();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshResults();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshResults();
            }
        });

        strategyCombo = new JComboBox<>(Strategy.values());
        strategyCombo.setSelectedItem(initialStrategy);
        strategyCombo.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, UIManager.getColor("Separator.shadow")));
        strategyCombo.setRenderer(new ColoredListCellRenderer<>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends Strategy> list, @NotNull Strategy value, int index, boolean selected, boolean focused) {
                setLeadingIcon(value.icon);
                append(value.label, TextAttributes.REGULAR_ATTRIBUTES);
            }
        });
        strategyCombo.addItemListener(e -> {
            final Strategy strategy = strategyCombo.getItemAt(strategyCombo.getSelectedIndex());
            inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, strategy.placeholder);
            refreshResults();
        });

        final JToolBar toolbar = new JToolBar();
        toolbar.add(strategyCombo);
        toolbar.add(new SearchHistoryAction());

        inputField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, initialStrategy.placeholder);
        inputField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_COMPONENT, toolbar);

        UIUtils.delegateAction(inputField, resultsTable, "selectPreviousRow", JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        UIUtils.delegateAction(inputField, resultsTable, "selectNextRow", JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        UIUtils.delegateAction(inputField, resultsTable, "scrollUpChangeSelection", JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        UIUtils.delegateAction(inputField, resultsTable, "scrollDownChangeSelection", JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        final JScrollPane tablePane = new JScrollPane(resultsTable);
        tablePane.setBorder(BorderFactory.createEmptyBorder());

        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(inputField, BorderLayout.NORTH);
        panel.add(tablePane, BorderLayout.CENTER);
        setContentPane(panel);

        pack();
        inputField.requestFocusInWindow();

        setSize(650, 350);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        final JRootPane rootPane = getRootPane();

        UIUtils.putAction(rootPane, JComponent.WHEN_IN_FOCUSED_WINDOW, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        UIUtils.putAction(rootPane, JComponent.WHEN_IN_FOCUSED_WINDOW, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final FilterableTableModel model = (FilterableTableModel) resultsTable.getModel();
                if (model.getRowCount() > 0) {
                    final FileInfo info = model.getValueAt(resultsTable.getSelectedRow());
                    openSelectedFile(project, info);
                    dispose();
                }
            }
        });

        UIUtils.putAction(rootPane, JComponent.WHEN_IN_FOCUSED_WINDOW, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_DOWN_MASK), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final FilterableTableModel model = (FilterableTableModel) resultsTable.getModel();
                if (model.getRowCount() > 0) {
                    final FileInfo info = model.getValueAt(resultsTable.getSelectedRow());
                    openSelectedFile(project, info);
                }
            }
        });

        if (query != null) {
            inputField.setText(query);
            inputField.selectAll();
        }
    }

    private void refreshResults() {
        ((FilterableTableModel) resultsTable.getModel()).refresh(
            PackfileBase.getNormalizedPath(inputField.getText(), false),
            strategyCombo.getItemAt(strategyCombo.getSelectedIndex())
        );
        resultsTable.changeSelection(0, 0, false, false);
    }

    private void openSelectedFile(@NotNull Project project, @NotNull FileInfo info) {
        Application.getEditorManager().openEditor(
            new FileEditorInputLazy(project.getContainer(), info.packfile(), info.path()),
            true
        );

        final Deque<HistoryRecord> history = HISTORY.computeIfAbsent(project, x -> new ArrayDeque<>());
        final HistoryRecord record = new HistoryRecord(inputField.getText(), strategyCombo.getItemAt(strategyCombo.getSelectedIndex()));
        history.remove(record);
        history.offerFirst(record);
        if (history.size() > HISTORY_LIMIT) {
            history.removeLast();
        }
    }

    @NotNull
    private static FileInfoIndex buildFileInfoIndex(@NotNull ProgressMonitor monitor, @NotNull Project project) throws IOException {
        try (var task = monitor.begin("Build file info index", 3)) {
            final List<FileInfo> info = new ArrayList<>();
            final Map<Packfile, Set<Long>> seen = new HashMap<>();

            try (var ignored = task.split(1).begin("Add named entries")) {
                final Map<Long, List<Packfile>> packfiles = new HashMap<>();

                for (Packfile packfile : project.getPackfileManager().getPackfiles()) {
                    for (PackfileBase.FileEntry fileEntry : packfile.getFileEntries()) {
                        packfiles.computeIfAbsent(fileEntry.hash(), x -> new ArrayList<>()).add(packfile);
                    }
                }

                try (Stream<String> files = project.listAllFiles()) {
                    files.forEach(path -> {
                        final long hash = PackfileBase.getPathHash(path);
                        for (Packfile packfile : packfiles.getOrDefault(hash, Collections.emptyList())) {
                            info.add(new FileInfo(packfile, path, hash));
                            seen.computeIfAbsent(packfile, x -> new HashSet<>())
                                .add(hash);
                        }
                    });
                }
            }

            try (var ignored = task.split(1).begin("Add unnamed entries")) {
                for (Packfile packfile : project.getPackfileManager().getPackfiles()) {
                    final Set<Long> files = seen.get(packfile);
                    if (files == null) {
                        continue;
                    }
                    for (PackfileBase.FileEntry entry : packfile.getFileEntries()) {
                        final long hash = entry.hash();
                        if (files.contains(hash)) {
                            continue;
                        }
                        info.add(new FileInfo(packfile, "<unnamed>/%8x".formatted(hash), hash));
                        seen.computeIfAbsent(packfile, x -> new HashSet<>())
                            .add(hash);
                    }
                }
            }

            try (var ignored = task.split(1).begin("Compute file links")) {
                return new FileInfoIndex(info.toArray(FileInfo[]::new), project.listFileLinks());
            }
        }
    }

    private static class FilterableTableModel extends AbstractTableModel {
        private static final FileInfo[] NO_RESULTS = new FileInfo[0];
        private static final int MAX_RESULTS = 1000;

        private final FileInfoIndex index;
        private FileInfo[] results;

        public FilterableTableModel(@NotNull FileInfoIndex index) {
            this.index = index;
            this.results = NO_RESULTS;
        }

        @Override
        public int getRowCount() {
            return results.length;
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return switch (column) {
                case 0 -> "Packfile";
                case 1 -> "Path";
                default -> "";
            };
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final FileInfo info = results[rowIndex];

            return switch (columnIndex) {
                case 0 -> info.packfile.getName();
                case 1 -> info.path;
                default -> null;
            };
        }

        @NotNull
        public FileInfo getValueAt(int rowIndex) {
            return results[rowIndex];
        }

        public void refresh(@NotNull String query, @NotNull Strategy strategy) {
            final int size = results.length;

            results = NO_RESULTS;
            fireTableRowsDeleted(0, size);

            if (query.isEmpty()) {
                return;
            }

            final long hash = PackfileBase.getPathHash(PackfileBase.getNormalizedPath(query, false));

            final FileInfo[] output = switch (strategy) {
                case FIND_MATCHING -> Arrays.stream(index.files)
                    .filter(file -> file.hash == hash || file.path.contains(query))
                    .limit(MAX_RESULTS)
                    .toArray(FileInfo[]::new);
                case FIND_REFERENCED_BY -> Objects.requireNonNullElse(index.referencedBy.get(hash), NO_RESULTS);
                case FIND_REFERENCES_TO -> Objects.requireNonNullElse(index.referencesTo.get(hash), NO_RESULTS);
            };

            if (output.length == 0) {
                return;
            }

            Arrays.sort(output, Comparator.comparing(FileInfo::packfile).thenComparing(FileInfo::path));

            results = output;
            fireTableRowsInserted(0, results.length);
        }
    }

    private class SearchHistoryAction extends AbstractAction {
        public SearchHistoryAction() {
            putValue(SMALL_ICON, new FlatSearchWithHistoryIcon(true));
            putValue(SHORT_DESCRIPTION, "Search History");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final Deque<HistoryRecord> history = HISTORY.get(project);
            final JPopupMenu menu = new JPopupMenu();

            if (history != null && !history.isEmpty()) {
                for (HistoryRecord record : history) {
                    menu.add(new AbstractAction(record.query, record.strategy.icon) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            strategyCombo.setSelectedItem(record.strategy);
                            inputField.setText(record.query);
                            inputField.selectAll();
                        }
                    });
                }
            } else {
                menu.add(new AbstractAction("<Empty>") {
                    {
                        setEnabled(false);
                    }

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // do nothing
                    }
                });
            }

            menu.show(inputField, strategyCombo.getWidth(), inputField.getHeight());
        }
    }

    private record HistoryRecord(@NotNull String query, @NotNull Strategy strategy) {}

    private record FileInfo(@NotNull Packfile packfile, @NotNull String path, long hash) {}

    private static final class FileInfoIndex {
        private final FileInfo[] files;
        private final Map<Long, FileInfo[]> referencesTo;
        private final Map<Long, FileInfo[]> referencedBy;

        private FileInfoIndex(@NotNull FileInfo[] files, @NotNull Map<Long, long[]> links) {
            final Map<Long, List<FileInfo>> hashes = Arrays.stream(files).collect(Collectors.groupingBy(FileInfo::hash));
            final Map<Long, List<FileInfo>> referencesTo = new HashMap<>();
            final Map<Long, List<FileInfo>> referencedBy = new HashMap<>();

            for (Map.Entry<Long, long[]> entry : links.entrySet()) {
                final Long file = entry.getKey();

                for (long reference : entry.getValue()) {
                    referencesTo
                        .computeIfAbsent(reference, x -> new ArrayList<>())
                        .addAll(Objects.requireNonNullElseGet(hashes.get(file), List::of));

                    referencedBy
                        .computeIfAbsent(file, x -> new ArrayList<>())
                        .addAll(Objects.requireNonNullElseGet(hashes.get(reference), List::of));
                }
            }

            this.files = files;
            this.referencesTo = referencesTo.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toArray(FileInfo[]::new)
            ));
            this.referencedBy = referencedBy.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toArray(FileInfo[]::new)
            ));
        }
    }
}
