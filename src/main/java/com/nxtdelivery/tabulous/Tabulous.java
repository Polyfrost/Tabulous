package com.nxtdelivery.tabulous;

import cc.polyfrost.oneconfig.events.EventManager;
import cc.polyfrost.oneconfig.events.event.LocrawEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import club.sk1er.patcher.config.PatcherConfig;
import com.nxtdelivery.tabulous.command.TabulousCommand;
import com.nxtdelivery.tabulous.config.TabulousConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

@Mod(modid = Tabulous.ID, name = Tabulous.NAME, version = Tabulous.VER)
public class Tabulous {
    public static final String NAME = "@NAME@", VER = "@VER@", ID = "@ID@";
    public static TabulousConfig config;
    private static boolean isPatcher = false;
    public static boolean isSkyblock = false;
    public static boolean hideWhiteNames = false;
    public static boolean isBedWars = false;

    @Mod.EventHandler
    protected void onInitialization(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        CommandManager.INSTANCE.registerCommand(new TabulousCommand());
        EventManager.INSTANCE.register(this);
        config = new TabulousConfig();
    }

    @Mod.EventHandler
    protected void onPostInitialization(FMLPostInitializationEvent event) {
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            if ("patcher".equals(mod.getModId())) {
                isPatcher = true;
                System.err.println("[Tabulous] Patcher is present. Enabling popMatrix fix");
            }
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    private void onLocraw(LocrawEvent event) {
        isSkyblock = event.info.getGameType() == LocrawInfo.GameType.SKYBLOCK;
        isBedWars = event.info.getGameType() == LocrawInfo.GameType.BEDWARS;
        hideWhiteNames = event.info.getGameType() == LocrawInfo.GameType.TNT_GAMES || event.info.getGameMode().contains("PARTY");
    }

    public static boolean isTabHeightAllow() {
        return isPatcher && PatcherConfig.tabHeightAllow;
    }
}
