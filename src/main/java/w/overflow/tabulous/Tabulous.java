package w.overflow.tabulous;

import w.overflow.tabulous.command.TabulousCommand;
import w.overflow.tabulous.config.TabulousConfig;
import w.overflow.tabulous.updater.Updater;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

@Mod(modid = Tabulous.ID, name = Tabulous.NAME, version = Tabulous.VER)
public class Tabulous {
    public static final String NAME = "@NAME@", VER = "@VER@", ID = "@ID@";
    public static File jarFile;
    public static File modDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "W-OVERFLOW"), NAME);
    public static TabulousConfig config;

    @Mod.EventHandler
    protected void onFMLPreInitialization(FMLPreInitializationEvent event) {
        if (!modDir.exists()) modDir.mkdirs();
        jarFile = event.getSourceFile();
    }

    @Mod.EventHandler
    protected void onInitialization(FMLInitializationEvent event) {
        new TabulousCommand().register();
        config = new TabulousConfig();
        config.preload();
        Updater.update();
    }
}
