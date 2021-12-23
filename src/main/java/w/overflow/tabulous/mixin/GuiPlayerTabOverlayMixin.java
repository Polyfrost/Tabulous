package w.overflow.tabulous.mixin;


import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.throwables.MixinException;
import w.overflow.tabulous.config.TabulousConfig;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public abstract class GuiPlayerTabOverlayMixin {
    private float percentComplete = 0f;
    private boolean retract = false;
    private boolean active = false;
    private boolean keyState = false;
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
    public String getPlayerName(NetworkPlayerInfo playerInfo) {
        throw new MixinException("something went wrong");
    }

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

    @ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE"))
    public List<NetworkPlayerInfo> removeNPCs(List<NetworkPlayerInfo> list) {
        if (TabulousConfig.hideNPCs && EssentialAPI.getMinecraftUtil().isHypixel()) {
            try {
                list.removeIf(info -> getPlayerName(info).startsWith("\u00A78[NPC]") || !getPlayerName(info).startsWith("\u00A7"));
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    @ModifyVariable(method = "renderPlayerlist", at = @At(value = "STORE"), ordinal = 3)
    private int changeWidth(int k) {
        if (TabulousConfig.customTab) {
            if (playerName.contains(mc.getSession().getUsername())) {
                if (!TabulousConfig.myNameText.equals("default")) {
                    k = mc.fontRendererObj.getStringWidth(TabulousConfig.myNameText);
                } else {
                    k = this.mc.fontRendererObj.getStringWidth(playerName);
                }
            }
        } else {
            k = this.mc.fontRendererObj.getStringWidth(playerName);
        }
        return k;
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

    @ModifyConstant(method = "renderPlayerlist", constant = @Constant(intValue = 20))
    private int modifyTabOverflow(int overflow) {
        return TabulousConfig.overflow;
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 0))
    public void cancelHeaderRect(int i, int i1, int i2, int i3, int i4) {
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 3))
    public void cancelFooterRect(int i, int i1, int i2, int i3, int i4) {
    }

    @ModifyArgs(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 1))
    public void redrawRect(Args args) {
        if (TabulousConfig.customTab) {
            args.set(4, TabulousConfig.tabColor.getRGB());
        }
        List<String> list1 = this.mc.fontRendererObj.listFormattedStringToWidth(this.header.getFormattedText(), width - 50);
        List<String> list2 = this.mc.fontRendererObj.listFormattedStringToWidth(this.footer.getFormattedText(), width - 50);
        int top = (int) args.get(1) - (list1.size() * 10);
        args.set(1, top);
        int bottom = ((int) args.get(3) + (list2.size() * 9)) - top;
        if (TabulousConfig.animations) {
            args.set(3, top + (int) (percentComplete * bottom));
        } else args.set(3, top + bottom);
    }


    @Inject(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawRect(IIIII)V", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    public void cancelUntilReady(CallbackInfo ci) {
        if (TabulousConfig.animations) {
            if (percentComplete < 0.9f) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I", ordinal = 0))
    public int cancelHeadUntilReady(FontRenderer instance, String text, float x, float y, int color) {
        if (TabulousConfig.animations) {
            if (percentComplete > 0.9f) {
                instance.drawStringWithShadow(text, x, y, color);
            }
        } else {
            instance.drawStringWithShadow(text, x, y, color);
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
        if (TabulousConfig.hideGuilds && EssentialAPI.getMinecraftUtil().isHypixel()) {
            if (args.get(0).toString().charAt(args.get(0).toString().length() - 1) == ']') {
                args.set(0, args.get(0).toString().substring(0, args.get(0).toString().lastIndexOf("\u00A7")));
            }
        }
        if (TabulousConfig.customTab) {
            if (args.get(0).toString().contains(mc.getSession().getUsername())) {
                if (!TabulousConfig.myNameText.equals("default")) {
                    args.set(0, TabulousConfig.myNameText);
                }
            }
        }
        if (TabulousConfig.headPos == 1) {
            args.set(1, (float) args.get(1) - 8f);
        }
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
        if (TabulousConfig.customTab) args.set(4, TabulousConfig.tabItemColor.getRGB());
    }

    @Redirect(method = "renderPlayerlist", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiPlayerTabOverlay;drawPing(IIILnet/minecraft/client/network/NetworkPlayerInfo;)V"))
    public void renderPing(GuiPlayerTabOverlay instance, int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo networkPlayerInfoIn) {
        if (TabulousConfig.renderPingNums && TabulousConfig.headPos != 1) {
            p_175245_1_ += 8;
            String pingNum = "\u00A7";
            int ping = networkPlayerInfoIn.getResponseTime();
            if (ping != 1) {
                if (ping < -1) pingNum += "5";
                else if (ping < 100) {
                    pingNum += "2";
                } else if (ping < 300) {
                    pingNum += "a";
                } else if (ping < 400) {
                    pingNum += "e";
                } else {
                    pingNum += "c";
                }
                pingNum += String.valueOf(networkPlayerInfoIn.getResponseTime());
                mc.fontRendererObj.drawStringWithShadow(pingNum, p_175245_1_ + (p_175245_2_ - (shouldRenderHeads ? 9 : 0) - mc.fontRendererObj.getStringWidth(pingNum)), p_175245_3_, -1);
            }
            return;
        }
        if (TabulousConfig.renderPing && TabulousConfig.headPos != 1) {
            p_175245_1_ += 8;
            drawPing(p_175245_1_, p_175245_2_ - (shouldRenderHeads ? 9 : 0), p_175245_3_, networkPlayerInfoIn);
        }
    }

    @Inject(method = "renderPlayerlist", at = @At("HEAD"), cancellable = true)
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn, CallbackInfo ci) {
        if (TabulousConfig.modEnabled) {
            if (TabulousConfig.disabled) {
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
            if (!TabulousConfig.showHeader) header = null;
            if (!TabulousConfig.showFooter) footer = null;
            if (!TabulousConfig.headerText.equals("default")) {
                header = new ChatComponentText(TabulousConfig.headerText);
            }
            if (!TabulousConfig.footerText.equals("default")) {
                footer = new ChatComponentText(TabulousConfig.footerText);
            }
        }
    }


    @Inject(method = "updatePlayerList", at = @At("HEAD"))
    public void updatePlayerList(CallbackInfo ci) {
        if (TabulousConfig.toggle) {            // TODO fix
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode()) && !keyState) {
                active = !active;
            }
            keyState = Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode());
        }
        if (active) KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), true);

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
