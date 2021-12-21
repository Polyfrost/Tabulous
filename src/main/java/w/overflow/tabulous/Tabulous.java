package w.overflow.tabulous;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import w.overflow.tabulous.command.TabulousCommand;
import w.overflow.tabulous.config.TabulousConfig;
import w.overflow.tabulous.updater.Updater;

import java.io.File;

@Mod(modid = Tabulous.ID, name = Tabulous.NAME, version = Tabulous.VER)
public class Tabulous {
    public static final String NAME = "@NAME@", VER = "@VER@", ID = "@ID@";
    public static File jarFile;
    public static File modDir = new File(new File(Minecraft.getMinecraft().mcDataDir, "W-OVERFLOW"), NAME);
    public static TabulousConfig config;
    private static boolean active = false;

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

    @SubscribeEvent
    protected void onKeyPress(TickEvent.RenderTickEvent event) {
        if (TabulousConfig.toggle) {
            if (Keyboard.getEventKey() == Minecraft.getMinecraft().gameSettings.keyBindPlayerList.getKeyCode()) {       // TODO make this work
                if (Keyboard.getEventKeyState()) {
                    active = !active;
                    if(active) KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindPlayerList.getKeyCode(), true);
                } else {
                    KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindPlayerList.getKeyCode(), false);
                }
            }
        }
    }
}
