package w.overflow.tabulous.config;

import gg.essential.api.EssentialAPI;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import w.overflow.tabulous.Tabulous;
import w.overflow.tabulous.updater.DownloadGui;
import w.overflow.tabulous.updater.Updater;

import java.io.File;

public class TabulousConfig extends Vigilant {
    @Property(
            type = PropertyType.SWITCH,
            name = "Enable",
            description = "Enable/Disable the mod.",
            category = "General"
    )
    public static boolean modEnabled = true;



    @Property(type = PropertyType.SELECTOR,
            name = "Tab Styling", description = "Styling to use when rendering the tab menu.",
            category = "Tab", options = {"Vanilla", "Vanilla+", "Custom"})
    public static int strategy = 0;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show Header",
            description = "Show the header on the scoreboard.",
            category = "Tab", subcategory = "Headers/Footers"
    )
    public static boolean showHeader = true;
    @Property(
            type = PropertyType.SWITCH,
            name = "Show Footer",
            description = "Show the footer on the scoreboard.",
            category = "Tab", subcategory = "Headers/Footers"
    )
    public static boolean showFooter = true;






    @Property(
            type = PropertyType.SWITCH,
            name = "Show Update Notification",
            description = "Show a notification when you start Minecraft informing you of new updates.",
            category = "Updater"
    )
    public static boolean showUpdate = true;

    @Property(
            type = PropertyType.BUTTON,
            name = "Update Now",
            description = "Update by clicking the button.",
            category = "Updater"
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
