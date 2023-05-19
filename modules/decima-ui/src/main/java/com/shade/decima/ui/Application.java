package com.shade.decima.ui;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.shade.decima.BuildConfig;
import com.shade.decima.cli.ApplicationCLI;
import com.shade.decima.model.app.ProjectChangeListener;
import com.shade.decima.model.app.ProjectContainer;
import com.shade.decima.model.app.ProjectManager;
import com.shade.decima.ui.editor.NodeEditorInputLazy;
import com.shade.decima.ui.editor.ProjectEditorInput;
import com.shade.decima.ui.menu.menus.HelpMenu;
import com.shade.decima.ui.navigator.NavigatorTree;
import com.shade.decima.ui.navigator.NavigatorTreeModel;
import com.shade.decima.ui.navigator.NavigatorView;
import com.shade.decima.ui.navigator.impl.NavigatorProjectNode;
import com.shade.decima.ui.navigator.menu.ProjectCloseItem;
import com.shade.platform.model.ExtensionRegistry;
import com.shade.platform.model.Lazy;
import com.shade.platform.model.Service;
import com.shade.platform.model.data.DataContext;
import com.shade.platform.model.persistence.PersistenceManager;
import com.shade.platform.model.runtime.VoidProgressMonitor;
import com.shade.platform.ui.ElementFactory;
import com.shade.platform.ui.PlatformMenuConstants;
import com.shade.platform.ui.app.ApplicationManager;
import com.shade.platform.ui.controls.HintManager;
import com.shade.platform.ui.editors.Editor;
import com.shade.platform.ui.editors.EditorChangeListener;
import com.shade.platform.ui.editors.EditorInput;
import com.shade.platform.ui.editors.EditorManager;
import com.shade.platform.ui.editors.lazy.LazyEditorInput;
import com.shade.platform.ui.editors.lazy.UnloadableEditorInput;
import com.shade.platform.ui.menus.MenuManager;
import com.shade.platform.ui.views.ViewManager;
import com.shade.util.NotNull;
import com.shade.util.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class Application implements com.shade.platform.ui.app.Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Preferences preferences;
    private final PersistenceManager persistenceManager;
    private final Map<Class<?>, Lazy<Object>> services;

    private JFrame frame;
    private ApplicationPane pane;

    public Application() {
        this.preferences = Preferences.userRoot().node("decima-explorer");
        this.persistenceManager = new PersistenceManager(Path.of("config/workspace.json"));
        this.services = ExtensionRegistry.getExtensions(Object.class, Service.class).stream()
            .collect(Collectors.toMap(
                service -> service.metadata().value(),
                Function.identity()
            ));
    }

    @NotNull
    public static Application getInstance() {
        return (Application) ApplicationManager.getApplication();
    }

    @Override
    public void start(@NotNull String[] args) {
        if (args.length > 0) {
            ApplicationCLI.execute(args);
        }

        beforeUI();
        pane = new ApplicationPane();
        frame = new JFrame();
        postUI();

        frame.setContentPane(pane);
        frame.setTitle(getApplicationTitle());
        frame.setIconImages(FlatSVGUtils.createWindowIconImages("/icons/application.svg"));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        getMenuManager().installMenuBar(frame.getRootPane(), PlatformMenuConstants.APP_MENU_ID, key -> {
            final KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

            for (Component cur = manager.getPermanentFocusOwner(); cur instanceof JComponent c; cur = cur.getParent()) {
                final DataContext context = (DataContext) c.getClientProperty(MenuManager.CONTEXT_KEY);

                if (context != null) {
                    final Object data = context.getData(key);

                    if (data != null) {
                        return data;
                    }
                }
            }

            return null;
        });

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                persistenceManager.persist();
            } catch (IOException e) {
                log.warn("Periodical state persistence failed", e);
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public <T> T getService(@NotNull Class<T> cls) {
        final Lazy<Object> service = services.get(cls);

        if (service != null) {
            return cls.cast(service.get());
        } else {
            return null;
        }
    }

    @NotNull
    @Override
    public JFrame getFrame() {
        return getInstance().frame;
    }

    @NotNull
    public static Preferences getPreferences() {
        return getInstance().preferences;
    }

    @NotNull
    public static MenuManager getMenuManager() {
        return getInstance().getService(MenuManager.class);
    }

    @NotNull
    public static ProjectManager getProjectManager() {
        return getInstance().getService(ProjectManager.class);
    }

    @NotNull
    public static EditorManager getEditorManager() {
        return getInstance().getService(EditorManager.class);
    }

    @NotNull
    public static ViewManager getViewManager() {
        return getInstance().pane;
    }

    @NotNull
    public static ElementFactory getElementFactory(@NotNull String id) {
        return ExtensionRegistry.getExtensions(ElementFactory.class, ElementFactory.Registration.class).stream()
            .filter(factory -> factory.metadata().value().equals(id))
            .findFirst().orElseThrow(() -> new NoSuchElementException("Can't find element factory '" + id + "'"))
            .get();
    }

    @NotNull
    public static NavigatorTree getNavigator() {
        return Objects.requireNonNull(Application.getViewManager().<NavigatorView>findView(NavigatorView.ID)).getTree();
    }

    @NotNull
    private static String getApplicationTitle() {
        final Editor activeEditor = getEditorManager().getActiveEditor();
        if (activeEditor != null) {
            return BuildConfig.APP_TITLE + " - " + activeEditor.getInput().getName();
        } else {
            return BuildConfig.APP_TITLE;
        }
    }

    private void beforeUI() {
        configureUI();

        getProjectManager().addProjectListener(new ProjectChangeListener() {
            @Override
            public void projectRemoved(@NotNull ProjectContainer container) {
                final EditorManager manager = getEditorManager();

                for (Editor editor : manager.getEditors()) {
                    final EditorInput input = editor.getInput();

                    if (isSameProject(input, container)) {
                        manager.closeEditor(editor);
                    }
                }
            }

            @Override
            public void projectOpened(@NotNull ProjectContainer container) {
                final EditorManager manager = getEditorManager();

                for (Editor editor : manager.getEditors()) {
                    final EditorInput input = editor.getInput();

                    if (input instanceof LazyEditorInput i && !i.canLoadImmediately() && isSameProject(i, container)) {
                        manager.reuseEditor(editor, i.canLoadImmediately(true));
                    }
                }
            }

            @Override
            public void projectClosed(@NotNull ProjectContainer container) {
                final EditorManager manager = getEditorManager();

                for (Editor editor : manager.getEditors()) {
                    final EditorInput input = editor.getInput();

                    if (isSameProject(input, container)) {
                        if (input instanceof UnloadableEditorInput uei) {
                            manager.reuseEditor(editor, uei.unloadInput());
                        } else {
                            manager.closeEditor(editor);
                        }
                    }
                }
            }

            private static boolean isSameProject(@NotNull EditorInput input, @NotNull ProjectContainer container) {
                return input instanceof ProjectEditorInput pei && pei.getProject().getContainer().equals(container)
                    || input instanceof NodeEditorInputLazy nei && nei.container().equals(container.getId());
            }
        });
    }

    private void postUI() {
        try {
            restoreWindow(preferences.node("window"));
        } catch (Exception e) {
            log.warn("Unable to restore window visuals", e);
        }

        try {
            pane.restoreViews(preferences.node("views"));
        } catch (Exception e) {
            log.warn("Unable to restore views", e);
        }

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                try {
                    persistenceManager.load();
                } catch (IOException e) {
                    log.warn("Error loading persistence", e);
                }

                try {
                    pane.restoreEditors(preferences.node("editors"));
                } catch (Exception e1) {
                    log.warn("Unable to restore editors", e1);
                }

                if (!BuildConfig.APP_VERSION.equals(preferences.get("version", BuildConfig.APP_VERSION))) {
                    HelpMenu.ChangelogItem.open();
                }

                if (Application.getProjectManager().getProjects().length == 0) {
                    HintManager.showHint(new HintManager.Hint(
                        "It looks like you don't have any projects.<br><br>Use <kbd>File</kbd> &rArr; <kbd>New</kbd> &rArr; <kbd>Project</kbd> to start.",
                        frame.getRootPane().getJMenuBar(),
                        SwingConstants.BOTTOM,
                        null
                    ));
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
                final NavigatorTreeModel model = Application.getNavigator().getModel();

                for (ProjectContainer container : Application.getProjectManager().getProjects()) {
                    final NavigatorProjectNode node = model.getProjectNode(new VoidProgressMonitor(), container);

                    if (node.isOpen() && !ProjectCloseItem.confirmProjectClose(node.getProject(), getEditorManager())) {
                        return;
                    }
                }

                saveState();
                System.exit(0);
            }
        });

        getEditorManager().addEditorChangeListener(new EditorChangeListener() {
            @Override
            public void editorChanged(@Nullable Editor editor) {
                frame.setTitle(getApplicationTitle());
            }
        });
    }

    private void configureUI() {
        FlatLaf.registerCustomDefaultsSource("themes");
        FlatInspector.install("ctrl shift alt X");
        FlatUIDefaultsInspector.install("ctrl shift alt Y");

        setLookAndFeel(preferences);

        UIManager.put("Action.containsIcon", new FlatSVGIcon("icons/actions/contains.svg"));
        UIManager.put("Action.editIcon", new FlatSVGIcon("icons/actions/edit.svg"));
        UIManager.put("Action.editModalIcon", new FlatSVGIcon("icons/actions/edit_modal.svg"));
        UIManager.put("Action.exportIcon", new FlatSVGIcon("icons/actions/export.svg"));
        UIManager.put("Action.importIcon", new FlatSVGIcon("icons/actions/import.svg"));
        UIManager.put("Action.packIcon", new FlatSVGIcon("icons/actions/pack.svg"));
        UIManager.put("Action.undoIcon", new FlatSVGIcon("icons/actions/undo.svg"));
        UIManager.put("Action.redoIcon", new FlatSVGIcon("icons/actions/redo.svg"));
        UIManager.put("Action.saveIcon", new FlatSVGIcon("icons/actions/save.svg"));
        UIManager.put("Action.searchIcon", new FlatSVGIcon("icons/actions/search.svg"));
        UIManager.put("Action.closeIcon", new FlatSVGIcon("icons/actions/tab_close.svg"));
        UIManager.put("Action.closeAllIcon", new FlatSVGIcon("icons/actions/tab_close_all.svg"));
        UIManager.put("Action.closeOthersIcon", new FlatSVGIcon("icons/actions/tab_close_others.svg"));
        UIManager.put("Action.closeUninitializedIcon", new FlatSVGIcon("icons/actions/tab_close_uninitialized.svg"));
        UIManager.put("Action.splitRightIcon", new FlatSVGIcon("icons/actions/split_right.svg"));
        UIManager.put("Action.splitDownIcon", new FlatSVGIcon("icons/actions/split_down.svg"));
        UIManager.put("Action.zoomInIcon", new FlatSVGIcon("icons/actions/zoom_in.svg"));
        UIManager.put("Action.zoomOutIcon", new FlatSVGIcon("icons/actions/zoom_out.svg"));
        UIManager.put("Action.zoomFitIcon", new FlatSVGIcon("icons/actions/zoom_fit.svg"));
        UIManager.put("Action.addElementIcon", new FlatSVGIcon("icons/actions/add_element.svg"));
        UIManager.put("Action.removeElementIcon", new FlatSVGIcon("icons/actions/remove_element.svg"));
        UIManager.put("Action.duplicateElementIcon", new FlatSVGIcon("icons/actions/duplicate_element.svg"));

        UIManager.put("Editor.binaryIcon", new FlatSVGIcon("icons/editors/binary.svg"));
        UIManager.put("Editor.coreIcon", new FlatSVGIcon("icons/editors/core.svg"));

        UIManager.put("Node.archiveIcon", new FlatSVGIcon("icons/nodes/archive.svg"));
        UIManager.put("Node.enumIcon", new FlatSVGIcon("icons/nodes/enum.svg"));
        UIManager.put("Node.uuidIcon", new FlatSVGIcon("icons/nodes/uuid.svg"));
        UIManager.put("Node.arrayIcon", new FlatSVGIcon("icons/nodes/array.svg"));
        UIManager.put("Node.objectIcon", new FlatSVGIcon("icons/nodes/object.svg"));
        UIManager.put("Node.referenceIcon", new FlatSVGIcon("icons/nodes/reference.svg"));
        UIManager.put("Node.decimalIcon", new FlatSVGIcon("icons/nodes/decimal.svg"));
        UIManager.put("Node.integerIcon", new FlatSVGIcon("icons/nodes/integer.svg"));
        UIManager.put("Node.stringIcon", new FlatSVGIcon("icons/nodes/string.svg"));
        UIManager.put("Node.booleanIcon", new FlatSVGIcon("icons/nodes/boolean.svg"));

        UIManager.put("Overlay.addIcon", new FlatSVGIcon("icons/overlays/add.svg"));
        UIManager.put("Overlay.modifyIcon", new FlatSVGIcon("icons/overlays/modify.svg"));

        UIManager.put("Toolbar.hideIcon", new FlatSVGIcon("icons/toolbars/hide.svg"));
        UIManager.put("Toolbar.pauseIcon", new FlatSVGIcon("icons/toolbars/pause.svg"));
        UIManager.put("Toolbar.playIcon", new FlatSVGIcon("icons/toolbars/play.svg"));
        UIManager.put("Toolbar.previousIcon", new FlatSVGIcon("icons/toolbars/previous.svg"));
        UIManager.put("Toolbar.nextIcon", new FlatSVGIcon("icons/toolbars/next.svg"));
    }

    private static void setLookAndFeel(@NotNull Preferences pref) {
        final String lafClassName = pref.node("window").get("laf", FlatLightLaf.class.getName());

        try {
            UIManager.setLookAndFeel(lafClassName);
        } catch (Exception e) {
            log.error("Failed to setup look and feel '" + lafClassName + "'l: " + e);
        }
    }

    private void saveState() {
        try {
            persistenceManager.persist();
        } catch (IOException e) {
            log.warn("Error saving persistence", e);
        }

        try {
            preferences.node("editors").removeNode();
            pane.saveEditors(preferences.node("editors"));
        } catch (Exception e) {
            log.warn("Unable to serialize editors", e);
        }

        try {
            preferences.node("views").removeNode();
            pane.saveViews(preferences.node("views"));
        } catch (Exception e) {
            log.warn("Unable to serialize views", e);
        }

        try {
            saveWindow(preferences);
        } catch (Exception e) {
            log.warn("Unable to save window visuals", e);
        }

        preferences.put("version", BuildConfig.APP_VERSION);
    }

    private void saveWindow(@NotNull Preferences pref) {
        final Preferences node = pref.node("window");
        node.putLong("size", (long) frame.getWidth() << 32 | frame.getHeight());
        node.putLong("location", (long) frame.getX() << 32 | frame.getY());
        node.putBoolean("maximized", (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) > 0);
    }

    private void restoreWindow(@NotNull Preferences pref) {
        final var size = pref.getLong("size", 0);
        final var location = pref.getLong("location", 0);
        final var maximized = pref.getBoolean("maximized", false);

        if (size > 0 && location >= 0) {
            frame.setSize((int) (size >>> 32), (int) size);
            frame.setLocation((int) (location >>> 32), (int) location);
        } else {
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
        }

        if (maximized) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }
}