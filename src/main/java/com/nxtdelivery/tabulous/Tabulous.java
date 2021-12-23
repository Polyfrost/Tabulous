package com.nxtdelivery.tabulous;

import club.sk1er.patcher.config.PatcherConfig;
import com.nxtdelivery.tabulous.command.TabulousCommand;
import com.nxtdelivery.tabulous.config.TabulousConfig;
import com.nxtdelivery.tabulous.updater.Updater;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = Tabulous.ID, name = Tabulous.NAME, version = Tabulous.VER)
public class Tabulous {
    public static final String NAME = "@NAME@", VER = "@VER@", ID = "@ID@";
    public static File jarFile;
    public static File modDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "W-OVERFLOW"), NAME);
    public static TabulousConfig config;
    private static boolean isPatcher = false;

    @Mod.EventHandler
    protected void onFMLPreInitialization(FMLPreInitializationEvent event) {
        if (!modDir.exists()) modDir.mkdirs();
        jarFile = event.getSourceFile();
    }

    @Mod.EventHandler
    protected void onInitialization(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        new TabulousCommand().register();
        config = new TabulousConfig();
        config.preload();
        Updater.update();
    }

    @Mod.EventHandler
    protected void onPostInitialization(FMLPostInitializationEvent event) {
        isPatcher = Loader.isModLoaded("patcher");
    }

    public static boolean isTabHeightAllow() {
        return isPatcher && PatcherConfig.tabHeightAllow;
    }

}
