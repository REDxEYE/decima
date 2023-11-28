package com.shade.decima.ui.data.viewer.model.menu;

import com.shade.decima.model.viewer.MeshViewerCanvas;
import com.shade.decima.model.viewer.isr.impl.NodeModel;
import com.shade.platform.ui.menus.MenuItem;
import com.shade.platform.ui.menus.MenuItemContext;
import com.shade.platform.ui.menus.MenuItemRegistration;
import com.shade.util.NotNull;

import static com.shade.decima.ui.menu.MenuConstants.*;

@MenuItemRegistration(parent = BAR_MODEL_VIEWER_ID, icon = "Action.outlineIcon", description = "Toggle outline", group = BAR_MODEL_VIEWER_GROUP_MISC, order = 2000)
public class ToggleOutlineItem extends MenuItem implements MenuItem.Check {
    @Override
    public void perform(@NotNull MenuItemContext ctx) {
        final MeshViewerCanvas canvas = ctx.getData(MeshViewerCanvas.CANVAS_KEY);
        canvas.setShowOutline(!canvas.isShowOutline());
    }

    @Override
    public boolean isEnabled(@NotNull MenuItemContext ctx) {
        return ctx.getData(MeshViewerCanvas.CANVAS_KEY).getModel() instanceof NodeModel;
    }

    @Override
    public boolean isChecked(@NotNull MenuItemContext ctx) {
        return ctx.getData(MeshViewerCanvas.CANVAS_KEY).isShowOutline();
    }
}
