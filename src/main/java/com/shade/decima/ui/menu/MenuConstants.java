package com.shade.decima.ui.menu;

import com.shade.platform.ui.menus.MenuService;

public interface MenuConstants {
    // @formatter:off
    String CTX_MENU_ID                              = MenuService.CTX_MENU_ID;
    String APP_MENU_ID                              = MenuService.APP_MENU_ID;
    String BAR_MENU_ID                              = MenuService.BAR_MENU_ID;

    // Application Menu: File
    String APP_MENU_FILE_ID                         = APP_MENU_ID + ".file";
    String APP_MENU_FILE_GROUP_OPEN                 = "1000," + APP_MENU_FILE_ID + ".open";
    String APP_MENU_FILE_GROUP_SETTINGS             = "1500," + APP_MENU_FILE_ID + ".settings";
    String APP_MENU_FILE_GROUP_SAVE                 = "2000," + APP_MENU_FILE_ID + ".save";
    String APP_MENU_FILE_GROUP_EXIT                 = "3000," + APP_MENU_FILE_ID + ".exit";

    // Application Menu: File / New
    String APP_MENU_FILE_NEW_ID                     = APP_MENU_FILE_ID + ".new";
    String APP_MENU_FILE_NEW_GROUP_GENERAL          = "1000," + APP_MENU_FILE_NEW_ID + ".general";

    // Application Menu: Edit
    String APP_MENU_EDIT_ID                         = APP_MENU_ID + ".edit";
    String APP_MENU_EDIT_GROUP_UNDO                 = "1000," + APP_MENU_EDIT_ID + ".undo";
    String APP_MENU_EDIT_GROUP_GENERAL              = "2000," + APP_MENU_EDIT_ID + ".general";

    // Application Menu: View
    String APP_MENU_VIEW_ID                         = APP_MENU_ID + ".view";
    String APP_MENU_VIEW_GROUP_GENERAL              = "1000," + APP_MENU_VIEW_ID + ".general";

    String APP_MENU_VIEW_THEME_ID                   = APP_MENU_VIEW_ID + ".theme";
    String APP_MENU_VIEW_THEME_GROUP_GENERAL        = "1000," + APP_MENU_VIEW_THEME_ID + ".general";
    String APP_MENU_VIEW_TOOL_WINDOWS_ID            = APP_MENU_VIEW_ID + ".toolWindows";
    String APP_MENU_VIEW_TOOL_WINDOWS_GROUP_GENERAL = "1000," + APP_MENU_VIEW_TOOL_WINDOWS_ID + ".general";

    // Application Menu: Help
    String APP_MENU_HELP_ID                         = APP_MENU_ID + ".help";
    String APP_MENU_HELP_GROUP_HELP                 = "1000," + APP_MENU_HELP_ID + ".help";
    String APP_MENU_HELP_GROUP_ABOUT                = "2000," + APP_MENU_HELP_ID + ".about";

    // Context Menu: Navigator
    String CTX_MENU_NAVIGATOR_ID                    = CTX_MENU_ID + ".navigator";
    String CTX_MENU_NAVIGATOR_GROUP_GENERAL         = "1000," + CTX_MENU_NAVIGATOR_ID + ".general";
    String CTX_MENU_NAVIGATOR_GROUP_OPEN            = "2000," + CTX_MENU_NAVIGATOR_ID + ".open";
    String CTX_MENU_NAVIGATOR_GROUP_COPY            = "3500," + CTX_MENU_NAVIGATOR_ID + ".copy";
    String CTX_MENU_NAVIGATOR_GROUP_PROJECT         = "3000," + CTX_MENU_NAVIGATOR_ID + ".project";
    String CTX_MENU_NAVIGATOR_GROUP_EDIT            = "4000," + CTX_MENU_NAVIGATOR_ID + ".edit";
    String CTX_MENU_NAVIGATOR_GROUP_FIND            = "5000," + CTX_MENU_NAVIGATOR_ID + ".find";

    String CTX_MENU_NAVIGATOR_FIND_ID               = CTX_MENU_NAVIGATOR_ID + ".find";
    String CTX_MENU_NAVIGATOR_FIND_GROUP_GENERAL    = "1000," + CTX_MENU_NAVIGATOR_FIND_ID + ".general";

    String CTX_MENU_NAVIGATOR_OPEN_ID               = CTX_MENU_NAVIGATOR_ID + ".open";
    String CTX_MENU_NAVIGATOR_OPEN_GROUP_GENERAL    = "1000," + CTX_MENU_NAVIGATOR_OPEN_ID + ".general";

    // Context Menu: Editor Stack
    String CTX_MENU_EDITOR_STACK_ID              = CTX_MENU_ID + ".editorStack";
    String CTX_MENU_EDITOR_STACK_GROUP_CLOSE     = "1000," + CTX_MENU_EDITOR_STACK_ID + ".close";
    String CTX_MENU_EDITOR_STACK_GROUP_SPLIT     = "2000," + CTX_MENU_EDITOR_STACK_ID + ".split";
    String CTX_MENU_EDITOR_STACK_GROUP_GENERAL   = "3000," + CTX_MENU_EDITOR_STACK_ID + ".general";

    // Context Menu: Core Editor
    String CTX_MENU_CORE_EDITOR_ID                  = CTX_MENU_ID + ".coreEditor";
    String CTX_MENU_CORE_EDITOR_GROUP_EDIT          = "1000," + CTX_MENU_CORE_EDITOR_ID + ".edit";
    String CTX_MENU_CORE_EDITOR_GROUP_EDIT_ARRAY    = "1500," + CTX_MENU_CORE_EDITOR_ID + ".editArray";
    String CTX_MENU_CORE_EDITOR_GROUP_GENERAL       = "2000," + CTX_MENU_CORE_EDITOR_ID + ".general";

    // Context Menu: Core Editor / Decoration
    String CTX_MENU_CORE_EDITOR_DECORATION_ID               = CTX_MENU_CORE_EDITOR_ID + ".decoration";
    String CTX_MENU_CORE_EDITOR_DECORATION_GROUP_GENERAL    = "1000," + CTX_MENU_CORE_EDITOR_DECORATION_ID + ".general";

    // Context Menu: Binary
    String CTX_MENU_BINARY_EDITOR_ID = CTX_MENU_ID + ".binaryEditor";

    // Tool Bar: Texture Viewer
    String BAR_TEXTURE_VIEWER_ID                    = BAR_MENU_ID + ".textureViewer";
    String BAR_TEXTURE_VIEWER_GROUP_ZOOM            = "1000," + BAR_TEXTURE_VIEWER_ID + ".zoom";
    String BAR_TEXTURE_VIEWER_GROUP_VIEW            = "2000," + BAR_TEXTURE_VIEWER_ID + ".view";

    String BAR_TEXTURE_VIEWER_CHANNEL_ID            = BAR_TEXTURE_VIEWER_ID + ".channel";
    String BAR_TEXTURE_VIEWER_CHANNEL_GROUP_GENERAL = "1000," + BAR_TEXTURE_VIEWER_CHANNEL_ID + ".general";

    String BAR_TEXTURE_VIEWER_BOTTOM_ID             = BAR_MENU_ID + ".textureViewerBottom";
    String BAR_TEXTURE_VIEWER_BOTTOM_GROUP_GENERAL  = "1000," + BAR_MENU_ID + ".general";

    // @formatter:on
}
