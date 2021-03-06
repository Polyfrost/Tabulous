package com.nxtdelivery.tabulous;

import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import club.sk1er.patcher.config.PatcherConfig;
import com.google.common.collect.Sets;
import com.nxtdelivery.tabulous.command.TabulousCommand;
import com.nxtdelivery.tabulous.config.TabulousConfig;
import com.nxtdelivery.tabulous.util.TickDelay;
import com.nxtdelivery.tabulous.util.Updater;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.Set;

@Mod(modid = Tabulous.ID, name = Tabulous.NAME, version = Tabulous.VER)
public class Tabulous {
    public static final String NAME = "@NAME@", VER = "@VER@", ID = "@ID@";
    public static File jarFile;
    public static File modDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "W-OVERFLOW"), NAME);
    public static TabulousConfig config;
    private static boolean isPatcher = false;
    public static boolean isSkyblock = false;
    public static boolean hideWhiteNames = false;
    public static boolean isBedWars = false;
    private static final Set<String> skyblockInAllLanguages = Sets.newHashSet("SKYBLOCK", "\u7A7A\u5C9B\u751F\u5B58", "\u7A7A\u5CF6\u751F\u5B58");
    private static final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    protected void onFMLPreInitialization(FMLPreInitializationEvent event) {
        if (!modDir.exists()) modDir.mkdirs();
        jarFile = event.getSourceFile();
    }

    @Mod.EventHandler
    protected void onInitialization(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        CommandManager.INSTANCE.registerCommand(TabulousCommand.class);
        config = new TabulousConfig();
        Updater.update();
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

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {

        // adapted from hytils which was stolen from sba which was stolen from neu
        new TickDelay(() -> {
            final EntityPlayerSP player = mc.thePlayer;
            if (mc.theWorld != null && player != null) {
                if (!mc.isSingleplayer() && player.getClientBrand() != null) {
                    if (!player.getClientBrand().contains("Hypixel")) {
                        isSkyblock = false;
                        hideWhiteNames = false;
                        isBedWars = false;
                        return;
                    }
                } else {
                    isSkyblock = false;
                    hideWhiteNames = false;
                    isBedWars = false;
                    return;
                }

                final Scoreboard scoreboard = mc.theWorld.getScoreboard();
                final ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);
                if (sidebarObjective != null) {
                    final String objectiveName = sidebarObjective.getDisplayName().replaceAll("(?i)\\u00A7.", "");
                    for (String skyblock : skyblockInAllLanguages) {
                        isSkyblock = objectiveName.startsWith(skyblock);
                    }
                    System.out.println(objectiveName);
                    hideWhiteNames = objectiveName.contains("TNT") || objectiveName.contains("PVP RUN") || objectiveName.contains("BOW SPLEEF") || objectiveName.contains("PARTY GAMES");
                    isBedWars = objectiveName.equals("BED WARS");
                    System.out.println(hideWhiteNames + " bw=" + hideWhiteNames);
                    return;
                }
            }
            isSkyblock = false;
            hideWhiteNames = false;
            isBedWars = false;
        }, 20);
    }


    public static boolean isTabHeightAllow() {
        return isPatcher && PatcherConfig.tabHeightAllow;
    }

}
