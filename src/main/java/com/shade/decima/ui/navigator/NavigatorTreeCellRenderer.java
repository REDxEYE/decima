package com.shade.decima.ui.navigator;

import com.shade.decima.model.util.IOUtils;
import com.shade.decima.model.util.NotNull;
import com.shade.decima.model.util.Nullable;
import com.shade.decima.ui.controls.ColoredTreeCellRenderer;
import com.shade.decima.ui.controls.TextAttributes;
import com.shade.decima.ui.navigator.impl.NavigatorFileNode;
import com.shade.decima.ui.navigator.impl.NavigatorPackfileNode;
import com.shade.decima.ui.navigator.impl.NavigatorProjectNode;

import javax.swing.*;

public class NavigatorTreeCellRenderer extends ColoredTreeCellRenderer<NavigatorNode> {
    private final NavigatorTreeModel model;

    public NavigatorTreeCellRenderer(@NotNull NavigatorTreeModel model) {
        this.model = model;
    }

    @Override
    protected void customizeCellRenderer(@NotNull JTree tree, @NotNull NavigatorNode value, boolean selected, boolean expanded, boolean focused, boolean leaf, int row) {
        if (model.isLoading(value)) {
            append(value.getLabel(), TextAttributes.GRAYED_ATTRIBUTES);
        } else if (value instanceof NavigatorFileNode node && node.getSize() > 0) {
            append("%s ".formatted(value.getLabel()), TextAttributes.REGULAR_ATTRIBUTES);
            append(IOUtils.formatSize(node.getSize()), TextAttributes.GRAYED_SMALL_ATTRIBUTES);
        } else if (value instanceof NavigatorPackfileNode node && node.getPackfile().getInfo() != null && node.getPackfile().getInfo().getLang() != null) {
            append("%s ".formatted(node.getPackfile().getName()), TextAttributes.REGULAR_ATTRIBUTES);
            append("(%s)".formatted(node.getPackfile().getInfo().getLang().getLabel()), TextAttributes.GRAYED_ATTRIBUTES);
        } else if (value instanceof NavigatorProjectNode node && !node.needsInitialization()) {
            append(value.getLabel(), TextAttributes.REGULAR_BOLD_ATTRIBUTES);
        } else {
            append(value.getLabel(), TextAttributes.REGULAR_ATTRIBUTES);
        }
    }

    @Nullable
    @Override
    public Icon getIcon(@NotNull JTree tree, @NotNull NavigatorNode value, boolean selected, boolean expanded, boolean focused, boolean leaf, int row) {
        final Icon icon = value.getIcon();
        if (icon != null) {
            return icon;
        } else {
            return super.getIcon(tree, value, selected, expanded, focused, leaf, row);
        }
    }
}