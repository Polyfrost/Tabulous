package com.nxtdelivery.tabulous.mixin;


import com.nxtdelivery.tabulous.Tabulous;
import com.nxtdelivery.tabulous.config.TabulousConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.GuiIngameForge;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.throwables.MixinException;

import java.util.List;

@Mixin(value = GuiPlayerTabOverlay.class, priority = Integer.MIN_VALUE)
public abstract class GuiPlayerTabOverlayMixin {
    private float percentComplete = 0f;
    private boolean retract = false;
    private String playerName = "i am not null!";
    private int rowRight = 0;
    private boolean shouldRenderHeads = true;
    private int width = 0;

    @Final
    @Shadow
    private Minecraft mc;


    @Shadow
    private IChatComponent footer;
    @Shadow
    private IChatComponent header;

    @Shadow
    private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_) {
        throw new MixinException("something went wrong");
    }

    @Shadow
    protected void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        throw new MixinException("something went wrong");
    }


    @ModifyArg(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I", ordinal = 0))
    public String getPlayerName(String args) {
        playerName = args;
        return args;
    }

    @ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE"), ordinal = 3)
    private int changeWidth(int k) {
        int newThing;
        if (playerName.contains(mc.getSession().getUsername())) {
            if (!TabulousConfig.myNameText.equals("default")) {
                newThing = mc.fontRendererObj.getStringWidth(TabulousConfig.myNameText);
            } else {
                newThing = k;
            }
        } else {
            newThing = k;
        }
        return newThing;
    }

    @ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE", ordinal = 0), ordinal = 9)
    public int setTop(int top) {
        return TabulousConfig.topPosition;
    }

    @ModifyVariable(method = "renderPlayerlist", at = @At("STORE"))
    public boolean setFlag(boolean flag) {
        if (TabulousConfig.dontShowHeads) flag = false;
        shouldRenderHeads = flag;
        return flag;
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 0))
    public void cancelHeaderRect(int i, int i1, int i2, int i3, int i4) {

    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 3))
    public void cancelFooterRect(int i, int i1, int i2, int i3, int i4) {

    }

    @ModifyArgs(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 1))
    public void redrawRect(Args args) {
        args.set(4, TabulousConfig.tabColor.getRGB());
        int top = args.get(1);
        int bottom = args.get(3);
        if(header != null) {
            List<String> list1 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);
            top -= (list1.size() * 10);
        }
        if(footer != null) {
            List<String> list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);
            bottom += (list2.size() * 10);
        }
        args.set(0, (int) args.get(0) - TabulousConfig.marginFix);
        args.set(2, (int) args.get(2) + TabulousConfig.marginFix);
        bottom = bottom - top;
        args.set(1, top);
        if (TabulousConfig.animations) {
            args.set(3, top + (int) (percentComplete * bottom));
        } else args.set(3, top + bottom);
    }
/*          don't worry about this wyvest, just a prototype
@ModifyVariable(method = "renderPlayerlist", at = @At(value = "LOAD", ordinal = 0), index = 17)
    public List<String> renderBoxHereInstead(List<String> list1) {
        int color = TabulousConfig.tabColor.getRGB();
        int left = width / 2 - l1 / 2 - 1;
        int top = k1 - 1;
        int right = width / 2 + l1 / 2 + 1;
        int bottom = k1 + i4 * 9;
        int currentBottom;
        List<String> list2;
        if (header != null) {
            top = top - (list1.size() * 10);
        }
        if (footer != null) {
            list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);
            bottom = (bottom + (list2.size() * 9)) - top;
        } else bottom = bottom - top;
        if (TabulousConfig.animations) {
            currentBottom = top + (int) (percentComplete * bottom);
        } else currentBottom = top + bottom;
        Gui.drawRect(left, top, right, currentBottom, color);
        return list1;
    }
@ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE"), ordinal = 4)
    public int getI4(int i4) {
        this.i4 = i4;
        return i4;
    }

    @ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE"), ordinal = 10)
    public int getL1(int l1) {
        this.l1 = l1;
        return l1;
    }

    @ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE"), ordinal = 9)
    public int getK1(int k1) {
        this.k1 = k1;
        return k1;
    }
 */


    @Inject(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    public void cancelUntilReady(CallbackInfo ci) {
        if (TabulousConfig.animations) {
            if (percentComplete < 0.9f) {
                if (Tabulous.isTabHeightAllow() && BossStatus.bossName != null && BossStatus.statusBarTime > 0 && GuiIngameForge.renderBossHealth) {
                    GlStateManager.popMatrix();
                }
                ci.cancel();
            }
        }
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 0))
    public int cancelHeadUntilReady(FontRenderer instance, String text, float x, float y, int color) {
        if (TabulousConfig.animations) {
            if (percentComplete > 0.9f) {
                drawString(instance, text, x, y, color);
            }
        } else {
            drawString(instance, text, x, y, color);
        }
        return 0;
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawScoreboardValues(Lnet/minecraft/scoreboard/ScoreObjective;ILjava/lang/String;IILnet/minecraft/client/network/NetworkPlayerInfo;)V", ordinal = 0))
    public void redirectScoreBoardRender(GuiPlayerTabOverlay instance, ScoreObjective j1, int f1, String i1, int s, int f, NetworkPlayerInfo j) {
        if (TabulousConfig.renderScoreboardValues) {
            drawScoreboardValues(j1, f1, i1, s, f, j);
        }
    }

    @ModifyArgs(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 2))
    public void renderNames(Args args) {
        if (args.get(0).toString().contains(mc.getSession().getUsername())) {
            if (!TabulousConfig.myNameText.equals("default")) {
                args.set(0, TabulousConfig.myNameText);
            }
        }
        if (TabulousConfig.headPos == 1) {
            args.set(1, (float) args.get(1) - 8f);
        }
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    public int drawString(FontRenderer instance, String text, float x, float y, int color) {
        if (TabulousConfig.textShadow) {
            instance.drawStringWithShadow(text, x, y, color);
        } else {
            instance.drawString(text, (int) x, (int) y, color);
        }
        return 0;
    }

    @ModifyArgs(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawScaledCustomSizeModalRect(IIFFIIIIFF)V", ordinal = 0))
    public void setPositionHead(Args args) {
        if (TabulousConfig.headPos == 1) {
            args.set(0, rowRight - 8);
        }
    }

    @ModifyArgs(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;drawScaledCustomSizeModalRect(IIFFIIIIFF)V", ordinal = 1))
    public void setPositionHat(Args args) {
        if (TabulousConfig.headPos == 1) {
            args.set(0, rowRight - 8);
        }
    }

    @ModifyArgs(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 2))
    public void setColor(Args args) {
        rowRight = args.get(2);
        args.set(4, TabulousConfig.tabItemColor.getRGB());
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawPing(IIILnet/minecraft/client/network/NetworkPlayerInfo;)V"))
    public void renderPing(GuiPlayerTabOverlay instance, int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        if (TabulousConfig.renderPing && TabulousConfig.headPos != 1) {
            if(shouldRenderHeads) p_175245_1_ += 8;
            drawPing(p_175245_1_, p_175245_2_ - (shouldRenderHeads ? 9 : 0), p_175245_3_, networkPlayerInfoIn);
        }
    }


    @Inject(method = "renderPlayerlist", at = @At("HEAD"), cancellable = true)
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn, CallbackInfo ci) {
        if (TabulousConfig.disabled) {
            if (Tabulous.isTabHeightAllow() && BossStatus.bossName != null && BossStatus.statusBarTime > 0 && GuiIngameForge.renderBossHealth) {
                GlStateManager.popMatrix();
            }
            ci.cancel();
        }
        percentComplete = clamp(easeOut(percentComplete, retract ? 0f : 1f));
        if (retract && Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode())) {
            retract = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), false);
        }
        if (retract && percentComplete == 0f) {
            retract = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), false);
        }
        this.width = width;
        modifyFooter();
        modifyHeader();
    }

    private void modifyFooter() {
        if (!TabulousConfig.footerText.equals("default")) {
            footer = new ChatComponentText(TabulousConfig.footerText);
        }
        if (!TabulousConfig.showFooter) {
            footer = null;
        }
    }

    private void modifyHeader() {
        if (!TabulousConfig.headerText.equals("default")) {
            header = new ChatComponentText(TabulousConfig.headerText);
        }
        if (!TabulousConfig.showHeader) {
            header = null;
        }
    }


    @Inject(method = "updatePlayerList", at = @At("HEAD"))
    public void updatePlayerList(CallbackInfo ci) {
        if (TabulousConfig.animations) {
            if (percentComplete != 0f && !mc.gameSettings.keyBindPlayerList.isKeyDown()) {
                retract = true;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), true);
            }
        }
    }


    private static float clamp(float number) {
        return number < (float) 0.0 ? (float) 0.0 : Math.min(number, (float) 1.0);
    }

    private static float easeOut(float current, float goal) {
        if (Math.floor(Math.abs(goal - current) / (float) 0.01) > 0) {
            return current + (goal - current) / TabulousConfig.animSpeed;
        } else {
            return goal;
        }
    }


}
