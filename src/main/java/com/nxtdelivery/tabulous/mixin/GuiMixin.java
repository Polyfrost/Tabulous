package com.nxtdelivery.tabulous.mixin;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "drawRect", at = @At("HEAD"), cancellable = true)
    private static void cancelDrawRect(int left, int top, int right, int bottom, int color, CallbackInfo ci) {
        if ((float)(color >> 24 & 255) == 0) {
            ci.cancel();
        }
    }
}
