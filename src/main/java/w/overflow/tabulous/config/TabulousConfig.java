package w.overflow.tabulous.config;

import gg.essential.api.EssentialAPI;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import w.overflow.tabulous.Tabulous;
import w.overflow.tabulous.updater.DownloadGui;
import w.overflow.tabulous.updater.Updater;

import java.awt.*;
import java.io.File;

public class TabulousConfig extends Vigilant {
    @Property(
            type = PropertyType.SWITCH,
            name = "Enable",
            description = "Enable/Disable the mod.",
            category = "General"
    )
    public static boolean modEnabled = true;
    @Property(
            type = PropertyType.SWITCH,
            name = "Toggle",
            description = "Enable tab toggling open and closed.",
            category = "General", subcategory = "General"
    )
    public static boolean toggle = false;


    @Property(type = PropertyType.SELECTOR,
            name = "Tab Styling", description = "Styling to use when rendering the tab menu.",
            category = "Tab", options = {"Vanilla-Style", "Vanilla+ (Animated)", "Tabulous (Reimagined Gui)", "Tab Disabled"})
    public static int strategy = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Header",
            description = "Show the header on the tab menu.",
            category = "Tab", subcategory = "Headers/Footers"
    )
    public static boolean showHeader = true;
    @Property(
            type = PropertyType.SWITCH,
            name = "Show Footer",
            description = "Show the footer on the tab menu.",
            category = "Tab", subcategory = "Headers/Footers"
    )
    public static boolean showFooter = true;
    @Property(
            type = PropertyType.SLIDER,
            name = "Top Position",
            description = "Position of the top of the tab menu. (default: 10)\nSet it to 0 to be seamless with the top of the screen.",
            category = "Tab", subcategory = "General", max = 20
    )
    public static int topPosition = 10;
    @Property(
            type = PropertyType.SWITCH,
            name = "Show Ping",
            description = "Show the ping values on the tab menu.",
            category = "Tab", subcategory = "Ping"
    )
    public static boolean renderPing = true;
    @Property(
            type = PropertyType.SWITCH,
            name = "Show Ping Numbers",
            description = "Show the ping values as numbers instead on the tab menu.\nThis will replace the normal ping render method, so don't try having Show Ping on as well. It won't work.",
            category = "Tab", subcategory = "Ping"
    )
    public static boolean renderPingNums = false;
    @Property(
            type = PropertyType.SWITCH,
            name = "Show Scoreboard Values",
            description = "Show the extended scoreboard values in the tab menu. \nNote: Not really used on Hypixel, but is on other servers.",
            category = "Tab", subcategory = "General"
    )
    public static boolean renderScoreboardValues = true;
    @Property(
            type = PropertyType.SWITCH,
            name = "Force Hide NPCs",
            description = "Force hide NPCs from the tab list on Hypixel.",
            category = "Tab", subcategory = "Hypixel"
    )
    public static boolean hideNPCs = true;
    @Property(
            type = PropertyType.SWITCH,
            name = "Don't Show Heads",
            description = "Don't show the head of the player in the tab menu.",
            category = "Tab", subcategory = "Heads"
    )
    public static boolean dontShowHeads = false;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Head Position",
            description = "Which side to show the head on in the tab menu.",
            category = "Tab", subcategory = "Heads",
            options = {"Left", "Right"}
    )
    public static int headPos = 0;
    @Property(
            type = PropertyType.SWITCH,
            name = "Hide Guild Tags",
            description = "Hide the guild tags shown in tab on Hypixel.",
            category = "Tab", subcategory = "Hypixel"
    )
    public static boolean hideGuilds = false;
    @Property(
            type = PropertyType.SLIDER,
            name = "Overflow Amount",
            description = "Amount of names to show before splitting into multiple columns. (default: 20)",
            category = "Tab", subcategory = "General", min = 5, max = 40
    )
    public static int overflow = 20;


    @Property(
            type = PropertyType.CHECKBOX,
            name = "Custom Tab Options",
            description = "These settings are for tab customization, including colors, animations, and more.\n\u00A7eThese settings only take effect on the Tabulous mode!",
            category = "Tabulous Customization"
    )
    public static boolean customTab = true;
    @Property(
            type = PropertyType.PARAGRAPH,
            name = "Custom Header Text",
            description = "Text for the custom header. Supports Minecraft color codes.\nSet to 'default' to disable.",
            category = "Tabulous Customization", subcategory = "Headers/Footers"
    )
    public static String headerText = "default";
    @Property(
            type = PropertyType.PARAGRAPH,
            name = "Custom Footer Text",
            description = "Text for the custom footer. Supports Minecraft color codes.\nSet to 'default' to disable.",
            category = "Tabulous Customization", subcategory = "Headers/Footers"
    )
    public static String footerText = "default";
    @Property(
            type = PropertyType.COLOR,
            name = "Tab Color",
            description = "Color for the tab menu.",
            category = "Tabulous Customization", subcategory = "Colors"
    )
    public static Color tabColor = new Color(50, 50, 50, 100);
    @Property(
            type = PropertyType.COLOR,
            name = "Tab Name Color",
            description = "Color for the names of people in tab.",
            category = "Tabulous Customization", subcategory = "Colors"
    )
    public static Color tabNameColor = new Color(50, 50, 50, 200);


    @Property(
            type = PropertyType.SWITCH,
            name = "Show Update Notification",
            description = "Show a notification when you start Minecraft informing you of new updates.",
            category = "Updates & Support", subcategory = "Updater"
    )
    public static boolean showUpdate = true;

    @Property(
            type = PropertyType.BUTTON,
            name = "Update Now",
            description = "Update by clicking the button.",
            category = "Updates & Support", subcategory = "Updater"
    )
    public void update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
                .openScreen(new DownloadGui());
        else EssentialAPI.getNotifications()
                .push(Tabulous.NAME, "No update had been detected at startup, and thus the update GUI has not been shown.");
    }

    public TabulousConfig() {
        super(new File(Tabulous.modDir, Tabulous.ID + ".toml"), Tabulous.NAME);
        initialize();
    }
}
