package com.nxtdelivery.tabulous.mixin;

import com.nxtdelivery.tabulous.config.TabulousConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    public void cancelBossBar(CallbackInfo ci) {
        if (Minecraft.getMinecraft().gameSettings.keyBindPlayerList.isKeyDown()) {
            if (TabulousConfig.cancelBossbar) {
                ci.cancel();
            }
        }
    }
}
