package com.desaysv.hmi.provider.table;

/**
 * 快捷应用管理数据表。
 */
public class DsvWidgetTable extends CommonTable {

    /**
     * 应用可以删除标识
     */
    public static final int FAVOURITE_CAN_DELETE = 0;

    /**
     * 应用不可以删除标识
     */
    public static final int FAVOURITE_CAN_NOT_DELETE = 1;

    /**
     * 应用可显示消息号标识
     */
    public static final int FAVOURITE_CAN_SHOW_MESSAGE = 1;

    /**
     * 应用不可显示消息号标识
     */
    public static final int FAVOURITE_CAN_NOT_SHOW_MESSAGE = 0;

    /**
     * 通过ContentsProvider访问的URI。
     */
    public static final String CONTENTURI = "default_widget";
    /**
     * 表名
     */
    public static final String TABLE_NAME = "dafault_widget";
    /**
     * 表名＋"."
     */
    public static final String TABLE_NAMEDOT = TABLE_NAME + ".";


    public static final String ITEM_TYPE = "itemType";

    public static final String ICON = "icon";

    /**
     * The container holding the favorite
     * <P>Type: INTEGER</P>
     */
    public static final String CONTAINER = "container";

    /**
     * The icon is a resource identified by a package name and an integer id.
     */
    public static final int CONTAINER_DESKTOP = -100;
    public static final int CONTAINER_HOTSEAT = -101;

    /**
     * The screen holding the favorite (if container is CONTAINER_DESKTOP)
     * <P>Type: INTEGER</P>
     */
    public static final String SCREEN = "screen";

    /**
     * The X coordinate of the cell holding the favorite
     * (if container is CONTAINER_HOTSEAT or CONTAINER_HOTSEAT)
     * <P>Type: INTEGER</P>
     */
    public static final String CELLX = "cellX";

    /**
     * The Y coordinate of the cell holding the favorite
     * (if container is CONTAINER_DESKTOP)
     * <P>Type: INTEGER</P>
     */
    public static final String CELLY = "cellY";

    /**
     * The X span of the cell holding the favorite
     * <P>Type: INTEGER</P>
     */
    public static final String SPANX = "spanX";

    /**
     * The Y span of the cell holding the favorite
     * <P>Type: INTEGER</P>
     */
    public static final String SPANY = "spanY";

    /**
     * The profile id of the item in the cell.
     * <P>
     * Type: INTEGER
     * </P>
     */
    public static final String PROFILE_ID = "profileId";

    /**
     * The favorite is a user created folder
     */
    public static final int ITEM_TYPE_FOLDER = 2;

    /**
     * The favorite is a live folder
     *
     * Note: live folders can no longer be added to Launcher, and any live folders which
     * exist within the launcher database will be ignored when loading.  That said, these
     * entries in the database may still exist, and are not automatically stripped.
     */
    public static final int ITEM_TYPE_LIVE_FOLDER = 3;

    /**
     * The favorite is a widget
     */
    public static final int ITEM_TYPE_APPWIDGET = 4;

    /**
     * The favorite is a clock
     */
    public static final int ITEM_TYPE_WIDGET_CLOCK = 1000;

    /**
     * The favorite is a search widget
     */
    public static final int ITEM_TYPE_WIDGET_SEARCH = 1001;

    /**
     * The favorite is a photo frame
     */
    public static final int ITEM_TYPE_WIDGET_PHOTO_FRAME = 1002;

    /**
     * The appWidgetId of the widget
     *
     * <P>Type: INTEGER</P>
     */
    public static final String APPWIDGET_ID = "appWidgetId";

    /**
     * Indicates whether this favorite is an application-created shortcut or not.
     * If the value is 0, the favorite is not an application-created shortcut, if the
     * value is 1, it is an application-created shortcut.
     * <P>Type: INTEGER</P>
     */
    @Deprecated
    public static final String IS_SHORTCUT = "isShortcut";

    /**
     * The URI associated with the favorite. It is used, for instance, by
     * live folders to find the content provider.
     * <P>Type: TEXT</P>
     */
    public static final String URI = "uri";

    /**
     * The display mode if the item is a live folder.
     * <P>Type: INTEGER</P>
     *
     * @see android.provider.LiveFolders#DISPLAY_MODE_GRID
     * @see android.provider.LiveFolders#DISPLAY_MODE_LIST
     */
    public static final String DISPLAY_MODE = "displayMode";


    public static final String TITLE = "title";

    /**
     * The Intent URL of the gesture, describing what it points to. This
     * value is given to {@link android.content.Intent#parseUri(String, int)} to create
     * an Intent that can be launched.
     * <P>Type: TEXT</P>
     */
    public static final String INTENT = "intent";


    /**
     * The gesture is an application
     */
    public static final int ITEM_TYPE_APPLICATION = 0;

    /**
     * The gesture is an application created shortcut
     */
    public static final int ITEM_TYPE_SHORTCUT = 1;

    /**
     * The icon type.
     * <P>Type: INTEGER</P>
     */
    public static final String ICON_TYPE = "iconType";

    /**
     * The icon is a resource identified by a package name and an integer id.
     */
    public static final int ICON_TYPE_RESOURCE = 0;

    /**
     * The icon is a bitmap.
     */
    public static final int ICON_TYPE_BITMAP = 1;

    /**
     * The icon package name, if icon type is ICON_TYPE_RESOURCE.
     * <P>Type: TEXT</P>
     */
    public static final String ICON_PACKAGE = "iconPackage";

    /**
     * The icon resource id, if icon type is ICON_TYPE_RESOURCE.
     * <P>Type: TEXT</P>
     */
    public static final String ICON_RESOURCE = "iconResource";



    /**
     * 列表。
     */
    public static final String[][] COLUMNS = new String[][] {
            new String[] { "_id", "INTEGER PRIMARY KEY" }, // 0
            new String[] { "title", "TEXT" }, // 1
            new String[] { "intent", "TEXT" }, // 2
            new String[] { "container", "INTEGER" }, // 3
            new String[] { "screen", "INTEGER  DEFAULT 0" }, // 4
            new String[] { "cellX", "INTEGER  DEFAULT 0" }, // 5
            new String[] { "cellY", "INTEGER" }, // 6
            new String[] { "spanX", "INTEGER" }, // 7
            new String[] { "spanY", "INTEGER" }, // 8
            new String[] { "itemType", "INTEGER" }, // 9
            new String[] { "appWidgetId", "INTEGER" }, // 10
            new String[] { "isShortcut", "INTEGER" }, // 11
            new String[] { "iconType", "INTEGER" }, // 12
            new String[] { "iconPackage", "TEXT" }, // 13
            new String[] { "iconResource", "TEXT" }, // 14
            new String[] { "icon", "BLOB" }, // 15
            new String[] { "uri", "TEXT" }, // 16
            new String[] { "displayMode", "INTEGER" }, // 17
            new String[] { "profileId", "INTEGER DEFAULT 0" }//18
    };

    /**
     * 字段：id
     */
    public static final String _ID = COLUMNS[0][0];

    /**
     * 字段：应用包名
     */
    public static final String PACKE_NAME = COLUMNS[1][0];

    /**
     * 字段：主Class名
     */
    public static final String MAIN_NAME = COLUMNS[2][0];

    /**
     * 字段：序号
     */
    public static final String SHOW_NUMBER = COLUMNS[3][0];

    /**
     * 字段：可删除标志
     */
    public static final String CAN_DELETE = COLUMNS[4][0];

    /**
     * 字段：可以显示消息标志
     */
    public static final String CAN_SHOW_MSG = COLUMNS[5][0];

    /**
     * 字段：添加时间
     */
    public static final String ADD_TIME = COLUMNS[6][0];

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getContentURI() {
        return CONTENTURI;
    }

    @Override
    public String[][] getColumns() {
        return COLUMNS;
    }
}
