package com.nxtdelivery.tabulous.config;

import com.nxtdelivery.tabulous.Tabulous;
import com.nxtdelivery.tabulous.updater.DownloadGui;
import com.nxtdelivery.tabulous.updater.Updater;
import gg.essential.api.EssentialAPI;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.awt.*;
import java.io.File;

public class TabulousConfig extends Vigilant {
    @Property(
            type = PropertyType.SWITCH,
            name = "Disable Tab",
            description = "Disable the tab from rendering.",
            category = "General", subcategory = "General"
    )
    public static boolean disabled = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Animations",
            description = "Enable animations on the window.",
            category = "Tab"
    )
    public static boolean animations = true;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Tab Location",
            description = "Location to draw the tab menu.\n\u00a7eDoesn't currently work!",
            category = "Tab", options = {"Top", "Bottom", "Left"}
    )
    public static int location = 0;

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
            name = "Text Shadow",
            description = "Render the text with a shadow on the tab menu.",
            category = "Tab", subcategory = "General"
    )
    public static boolean textShadow = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Ping",
            description = "Show the ping values on the tab menu.",
            category = "Tab", subcategory = "Ping"
    )
    public static boolean renderPing = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Scoreboard Values",
            description = "Show the extended scoreboard values in the tab menu. \nNote: Not really used on Hypixel, but is on other servers.",
            category = "Tab", subcategory = "General"
    )
    public static boolean renderScoreboardValues = true;

    @Property(
            type = PropertyType.SLIDER,
            name = "Fix Margins",
            description = "Fix the margins on the side of the tab menu.\nSet to 5 to be in line with the margins on the rest of the menu.",
            category = "Tab", subcategory = "Margins", max = 10
    )
    public static int marginFix = 0;
    @Property(
            type = PropertyType.SWITCH,
            name = "Don't Render Bossbar",
            description = "Disable rendering of the bossbar while the tab menu is open.",
            category = "Tab", subcategory = "General"
    )
    public static boolean cancelBossbar = false;

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
            type = PropertyType.DECIMAL_SLIDER,
            name = "Animation Speed",
            description = "Animation speed for when opening the window. (default: 10)",
            category = "Tabulous Customization", subcategory = "Animations",
            minF = 2f, maxF = 30f
    )
    public static float animSpeed = 10f;

    @Property(
            type = PropertyType.PARAGRAPH,
            name = "Custom Header Text",
            description = "Text for the custom header. Supports Minecraft color codes.\nSet to 'default' to disable.",
            category = "Tabulous Customization", subcategory = "Custom Text"
    )
    public static String headerText = "default";

    @Property(
            type = PropertyType.PARAGRAPH,
            name = "Custom Footer Text",
            description = "Text for the custom footer. Supports Minecraft color codes.\nSet to 'default' to disable.",
            category = "Tabulous Customization", subcategory = "Custom Text"
    )
    public static String footerText = "default";

    @Property(
            type = PropertyType.TEXT,
            name = "Custom Name Text",
            description = "Text for your name in the tab list. Supports Minecraft color codes.\nSet to 'default' to disable.",
            category = "Tabulous Customization", subcategory = "Custom Text"
    )
    public static String myNameText = "default";

    @Property(
            type = PropertyType.SWITCH, name = "Hide Custom Name Ingame",
            description = "Hide your custom name in game, so you look like everyone else.",
            category = "Tabulous Customization", subcategory = "Custom Text"
    )
    public static boolean hideCustomNameIngame = false;

    @Property(
            type = PropertyType.COLOR,
            name = "Tab Color",
            description = "Color for the tab menu.",
            category = "Tabulous Customization", subcategory = "Colors"
    )
    public static Color tabColor = new Color(50, 50, 50, 128);

    @Property(
            type = PropertyType.COLOR,
            name = "Tab Entry Color",
            description = "Color for the entries of people in tab. (Background color beneath the names)",
            category = "Tabulous Customization", subcategory = "Colors"
    )
    public static Color tabItemColor = new Color(255, 255, 255, 32);


    @Property(
            type = PropertyType.SWITCH, name = "Hide Guild Tags in Tab",
            description = "Prevent Guild tags from showing up in tab.",
            category = "Hypixel", subcategory = "Players"
    )
    public static boolean hideGuilds = false;

    @Property(
            type = PropertyType.SWITCH, name = "Hide Player Ranks in Tab",
            description = "Prevent player ranks from showing up in tab.",
            category = "Hypixel", subcategory = "Players"
    )
    public static boolean hidePlayerRanksInTab = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Force Hide NPCs",
            description = "Force hide NPCs in the tab menu.",
            category = "Hypixel", subcategory = "NPCs"
    )
    public static boolean hideNPCs = true;

    @Property(
            type = PropertyType.SWITCH, name = "Cleaner Tab in SkyBlock",
            description = "Don't render ping for tab entries in SkyBlock.",
            category = "Hypixel", subcategory = "SkyBlock"
    )
    public static boolean cleanerSkyBlockTabInfo = true;

    @Property(
            type = PropertyType.SWITCH, name = "Hide Ping Ingame",
            description = "Prevent ping from showing up in tab while playing games, since the value is misleading. Ping will remain visible in lobbies.",
            category = "Hypixel", subcategory = "Players"
    )
    public static boolean hidePingInGame = true;






    @Property(
            type = PropertyType.SWITCH,
            name = "Show Update Notification",
            description = "Show a notification when you start Minecraft informing you of new updates.",
            category = "General", subcategory = "Updates"
    )
    public static boolean showUpdate = true;

    @Property(
            type = PropertyType.BUTTON,
            name = "Update Now",
            description = "Update by clicking the button.",
            category = "General", subcategory = "Updates", placeholder = "Update"
    )
    public void update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
                .openScreen(new DownloadGui());
        else EssentialAPI.getNotifications()
                .push(Tabulous.NAME, "No update had been detected at startup, and thus the update GUI has not been shown.");
    }

    public TabulousConfig() {
        super(new File(Tabulous.modDir, Tabulous.ID + ".toml"), Tabulous.NAME + " v" + Tabulous.VER);
        initialize();
    }
}
