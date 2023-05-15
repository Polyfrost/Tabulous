package com.nxtdelivery.tabulous.mixin;

import com.nxtdelivery.tabulous.config.TabulousConfig;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin {
    @ModifyArg(method = "renderPlayerList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;renderPlayerlist(ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreObjective;)V"), index = 0)
    public int setWidth(int width) {
        switch (TabulousConfig.position) {
            default:
            case 0:
                return width;
            case 1:
            case 3:
                return width / 3;
            case 2:
            case 4:
                return width + (width / 2);
        }
    }
}
