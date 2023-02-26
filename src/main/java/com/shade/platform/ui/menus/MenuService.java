package com.shade.platform.ui.menus;

import com.shade.platform.model.LazyWithMetadata;
import com.shade.platform.model.data.DataContext;
import com.shade.platform.model.util.ReflectionUtils;
import com.shade.platform.ui.controls.Mnemonic;
import com.shade.platform.ui.util.UIUtils;
import com.shade.util.NotNull;
import com.shade.util.Nullable;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;
import java.util.*;

public class MenuService {
    public static final String CTX_MENU_ID = "menu.ctx";
    public static final String APP_MENU_ID = "menu.app";

    private final List<LazyWithMetadata<Menu, MenuRegistration>> contributedMenus;
    private final List<LazyWithMetadata<MenuItem, MenuItemRegistration>> contributedItems;

    private Map<String, List<LazyWithMetadata<Menu, MenuRegistration>>> menus;
    private Map<String, List<MenuItemGroup>> groups;

    public MenuService() {
        this.contributedMenus = ReflectionUtils.findAnnotatedTypes(Menu.class, MenuRegistration.class);
        this.contributedItems = ReflectionUtils.findAnnotatedTypes(MenuItem.class, MenuItemRegistration.class);
    }

    public void installPopupMenu(@NotNull JTree tree, @NotNull String id, @NotNull DataContext context) {
        UIUtils.installPopupMenu(tree, createContextMenu(tree, id, context));
        createMenuKeyBindings(tree, id, context);
    }

    public void installPopupMenu(@NotNull JTabbedPane pane, @NotNull String id, @NotNull DataContext context) {
        UIUtils.installPopupMenu(pane, createContextMenu(pane, id, context));
        createMenuKeyBindings(pane, id, context);
    }

    public void installMenuBar(@NotNull JRootPane pane, @NotNull String id) {
        pane.setJMenuBar(createMenuBar(id));
        createMenuBarKeyBindings(pane, id);
    }

    @NotNull
    public JMenuBar createMenuBar(@NotNull String id) {
        initializeMenus();

        final var menuBar = new JMenuBar();
        final var contributions = menus.get(id);

        if (contributions == null || contributions.isEmpty()) {
            return menuBar;
        }

        for (var contribution : contributions) {
            final JMenu menu = new JMenu();
            final Mnemonic mnemonic = Mnemonic.extract(contribution.metadata().name());

            if (mnemonic != null) {
                mnemonic.setText(menu);
            } else {
                menu.setText(contribution.metadata().name());
            }

            final JPopupMenu popupMenu = menu.getPopupMenu();
            popupMenu.addPopupMenuListener(new MyPopupMenuListener(popupMenu, contribution.metadata().id()));

            menuBar.add(menu);
        }

        return menuBar;
    }

    @NotNull
    public JPopupMenu createContextMenu(@NotNull JComponent component, @NotNull String id, @NotNull DataContext context) {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(new MyPopupMenuListener(component, popupMenu, id, context));
        return popupMenu;
    }

    public void createMenuBarKeyBindings(@NotNull JComponent target, @NotNull String id) {
        initializeMenus();

        final var menus = this.menus.get(id);

        if (menus == null || menus.isEmpty()) {
            return;
        }

        for (var menu : menus) {
            createMenuKeyBindings(target, menu.metadata().id(), DataContext.EMPTY);
        }
    }

    private void createMenuKeyBindings(@NotNull JComponent target, @NotNull String id, @NotNull DataContext context) {
        initializeMenuItems();

        final List<MenuItemGroup> groups = this.groups.get(id);

        if (groups == null || groups.isEmpty()) {
            return;
        }

        for (MenuItemGroup group : groups) {
            createMenuGroupKeyBindings(target, group, new MenuItemContext(context, target));
        }
    }

    public void createMenuGroupKeyBindings(@NotNull JComponent target, @NotNull MenuItemProvider provider, @NotNull MenuItemContext context) {
        for (var contribution : provider.create(context)) {
            final MenuItem item = contribution.get();

            if (!contribution.metadata().id().isEmpty()) {
                createMenuKeyBindings(target, contribution.metadata().id(), context);
            }

            if (!contribution.metadata().keystroke().isEmpty()) {
                UIUtils.putAction(target, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, KeyStroke.getKeyStroke(contribution.metadata().keystroke()), new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (item.isEnabled(context)) {
                            item.perform(context);
                        }
                    }
                });
            }

            if (item instanceof MenuItemProvider p && !p.isInitializedOnDemand()) {
                createMenuGroupKeyBindings(target, p, context);
            }
        }
    }

    @NotNull
    private JMenuItem createMenuItem(@NotNull MenuItem item, @NotNull MenuItemRegistration metadata, @NotNull MenuItemContext context) {
        final JMenuItem menuItem;

        if (groups.containsKey(metadata.id())) {
            final JMenu menu = new JMenu();

            final JPopupMenu popupMenu = menu.getPopupMenu();
            popupMenu.addPopupMenuListener(new MyPopupMenuListener(null, popupMenu, metadata.id(), context));

            menuItem = menu;
        } else if (item.isChecked(context)) {
            menuItem = item.isRadio(context) ? new JRadioButtonMenuItem((String) null, true) : new JCheckBoxMenuItem(null, true);
        } else {
            menuItem = new JMenuItem();
        }

        final var name = Objects.requireNonNullElseGet(item.getName(context), metadata::name);
        final var mnemonic = Mnemonic.extract(name);
        final var icon = item.getIcon(context);

        if (mnemonic != null) {
            mnemonic.setText(menuItem);
        } else {
            menuItem.setText(name);
        }

        if (icon != null) {
            menuItem.setIcon(icon);
        } else if (!metadata.icon().isEmpty()) {
            menuItem.setIcon(UIManager.getIcon(metadata.icon()));
        }

        if (!metadata.keystroke().isEmpty()) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(metadata.keystroke()));
        }

        if (!item.isEnabled(context)) {
            menuItem.setEnabled(false);
        }

        menuItem.addActionListener(e -> {
            if (item.isEnabled(context)) {
                item.perform(context);
            }
        });

        return menuItem;
    }

    private void populateMenu(@NotNull JPopupMenu menu, @NotNull String id, @NotNull MenuItemContext context) {
        initializeMenuItems();

        final List<MenuItemGroup> groups = this.groups.get(id);

        if (groups == null || groups.isEmpty()) {
            return;
        }

        for (MenuItemGroup group : groups) {
            populateMenuGroup(menu, group, context);
        }

        if (menu.getComponentCount() == 0) {
            menu.add(new PlaceholderAction());
        }
    }

    private void populateMenuGroup(@NotNull JPopupMenu menu, @NotNull MenuItemProvider provider, @NotNull MenuItemContext context) {
        final var contributions = provider.create(context).stream()
            .filter(contribution -> contribution.get().isVisible(context))
            .toList();

        if (contributions.isEmpty()) {
            return;
        }

        if (menu.getComponentCount() > 0) {
            menu.addSeparator();
        }

        for (var contribution : contributions) {
            final MenuItem item = contribution.get();

            if (item instanceof MenuItemProvider p) {
                populateMenuGroup(menu, p, context);
            } else {
                menu.add(createMenuItem(item, contribution.metadata(), context));
            }
        }
    }

    private void initializeMenus() {
        if (menus != null) {
            return;
        }

        menus = new HashMap<>(contributedMenus.size());

        for (var contribution : contributedMenus) {
            menus
                .computeIfAbsent(contribution.metadata().parent(), key -> new ArrayList<>())
                .add(contribution);
        }

        for (var menus : menus.values()) {
            final var seen = new HashSet<>();
            final var ordered = menus.stream()
                .filter(menu -> seen.add(menu.metadata().id()))
                .sorted(Comparator.comparingInt(menu -> menu.metadata().order()))
                .toList();

            menus.clear();
            menus.addAll(ordered);
        }
    }

    private void initializeMenuItems() {
        if (groups != null) {
            return;
        }

        final Map<String, Map<String, MenuItemGroup>> groups = new HashMap<>();

        for (var contribution : contributedItems) {
            final var metadata = MenuItemGroup.Metadata.valueOf(contribution.metadata().group());

            groups
                .computeIfAbsent(contribution.metadata().parent(), key -> new HashMap<>())
                .computeIfAbsent(metadata.id(), key -> new MenuItemGroup(metadata, new ArrayList<>()))
                .items().add(contribution);
        }

        this.groups = new HashMap<>(groups.size());

        for (var entry : groups.entrySet()) {
            final var ordered = entry.getValue().values().stream()
                .sorted(Comparator.comparingInt(group -> group.metadata().order()))
                .peek(group -> group.items().sort(Comparator.comparingInt(item -> item.metadata().order())))
                .toList();

            this.groups.put(entry.getKey(), ordered);
        }
    }

    private class MyPopupMenuListener implements PopupMenuListener {
        private final JComponent source;
        private final JPopupMenu popupMenu;
        private final String menuId;
        private final DataContext context;

        public MyPopupMenuListener(@Nullable JComponent source, @NotNull JPopupMenu popupMenu, @NotNull String menuId, @NotNull DataContext context) {
            this.source = source;
            this.popupMenu = popupMenu;
            this.menuId = menuId;
            this.context = context;
        }

        public MyPopupMenuListener(@NotNull JPopupMenu popupMenu, @NotNull String menuId) {
            this(null, popupMenu, menuId, DataContext.EMPTY);
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            populateMenu(popupMenu, menuId, new MenuItemContext(context, source));
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            popupMenu.removeAll();
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            popupMenu.removeAll();
        }
    }

    private record MenuItemGroup(@NotNull Metadata metadata, @NotNull List<LazyWithMetadata<MenuItem, MenuItemRegistration>> items) implements MenuItemProvider {
        @NotNull
        @Override
        public List<LazyWithMetadata<MenuItem, MenuItemRegistration>> create(@NotNull MenuItemContext ctx) {
            return items;
        }

        private record Metadata(@NotNull String id, int order) {
            @NotNull
            public static Metadata valueOf(@NotNull String value) {
                final int separator = value.indexOf(',');

                if (separator < 0) {
                    throw new IllegalArgumentException("Group has no separator: '" + value + "'");
                }

                final var order = Integer.parseInt(value.substring(0, separator));
                final var id = value.substring(separator + 1);

                return new Metadata(id, order);
            }
        }
    }

    private static class PlaceholderAction extends AbstractAction {
        public PlaceholderAction() {
            super("<Empty>");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // do nothing
        }
    }
}
