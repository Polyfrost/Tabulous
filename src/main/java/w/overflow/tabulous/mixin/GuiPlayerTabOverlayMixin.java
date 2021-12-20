package w.overflow.tabulous.mixin;


import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.throwables.MixinException;
import w.overflow.tabulous.config.TabulousConfig;

import java.util.List;

@Mixin(GuiPlayerTabOverlay.class)
public class GuiPlayerTabOverlayMixin {

    @Final
    @Shadow
    private final Minecraft mc = Minecraft.getMinecraft();
    @Final
    @Shadow
    private static Ordering<NetworkPlayerInfo> field_175252_a;
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

    @Shadow
    private boolean isBeingRendered;


    public void renderPing(int a, int b, int c, NetworkPlayerInfo playerInfo) {
        drawPing(a, b, c, playerInfo);
    }

    public Ordering<NetworkPlayerInfo> getPlayerInfoOrdering() {
        return field_175252_a;
    }

    public void renderScoreboardValues(ScoreObjective scoreObjective, int p_175247_2_, String objName, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo playerInfo) {
        drawScoreboardValues(scoreObjective, p_175247_2_, objName, p_175247_4_, p_175247_5_, playerInfo);
    }

    public IChatComponent getFooter() {
        return footer;
    }

    public IChatComponent getHeader() {
        return header;
    }

    public boolean getRenderState() {
        return isBeingRendered;
    }


    @Inject(method = "renderPlayerlist", at = @At("HEAD"), cancellable = true)
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn, CallbackInfo ci) {
        if(TabulousConfig.modEnabled) {
            switch (TabulousConfig.strategy) {
                default:
                case 0:
                    renderVanilla(width, scoreboardIn, scoreObjectiveIn);
                    break;
                case 1:
                    break;
                case 2:
                    break;

            }
            ci.cancel();
        }
        //System.out.println("fin");
    }


    /**
     * vanilla method for drawing the scoreboard (with custom features)
     * @param width            width
     * @param scoreboardIn     current scoreboard
     * @param scoreObjectiveIn current scoreboard objectives
     */
    public void renderVanilla(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        if(!TabulousConfig.showHeader) header = null;
        if(!TabulousConfig.showFooter) footer = null;

        NetHandlerPlayClient nethandlerplayclient = mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> list = getPlayerInfoOrdering().sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        int i = 0;
        int j = 0;
        for (NetworkPlayerInfo networkplayerinfo : list) {
            int k = mc.fontRendererObj.getStringWidth(getPlayerName(networkplayerinfo));
            i = Math.max(i, k);

            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                k = mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
                j = Math.max(j, k);
            }
        }

        list = list.subList(0, Math.min(list.size(), 80));
        int l3 = list.size();
        int i4 = l3;
        int j4;

        for (j4 = 1; i4 > 20; i4 = (l3 + j4 - 1) / j4) {
            ++j4;
        }

        boolean flag = mc.isIntegratedServerRunning() || mc.getNetHandler().getNetworkManager().getIsencrypted();
        int l;

        if (scoreObjectiveIn != null) {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                l = 90;
            } else {
                l = j;
            }
        } else {
            l = 0;
        }

        int i1 = Math.min(j4 * ((flag ? 9 : 0) + i + l + 13), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;
        List<String> list1 = null;
        List<String> list2 = null;

        if (getHeader() != null) {
            list1 = mc.fontRendererObj.listFormattedStringToWidth(getHeader().getFormattedText(), width - 50);

            for (String s : list1) {
                l1 = Math.max(l1, mc.fontRendererObj.getStringWidth(s));
            }
        }

        if (getFooter() != null) {
            list2 = mc.fontRendererObj.listFormattedStringToWidth(getFooter().getFormattedText(), width - 50);

            for (String s2 : list2) {
                l1 = Math.max(l1, mc.fontRendererObj.getStringWidth(s2));
            }
        }

        if (list1 != null) {
            Gui.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s3 : list1) {
                int i2 = mc.fontRendererObj.getStringWidth(s3);
                mc.fontRendererObj.drawStringWithShadow(s3, (float) (width / 2 - i2 / 2), (float) k1, -1);
                k1 += mc.fontRendererObj.FONT_HEIGHT;
            }

            ++k1;
        }

        Gui.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);

        for (int k4 = 0; k4 < l3; ++k4) {
            int l4 = k4 / i4;
            int i5 = k4 % i4;
            int j2 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            Gui.drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            if (k4 < list.size()) {
                NetworkPlayerInfo networkplayerinfo1 = list.get(k4);
                String s1 = getPlayerName(networkplayerinfo1);
                GameProfile gameprofile = networkplayerinfo1.getGameProfile();

                if (flag) {
                    EntityPlayer entityplayer = mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    int l2 = 8 + (flag1 ? 8 : 0);
                    int i3 = 8 * (flag1 ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(j2, k2, 8.0F, (float) l2, 8, i3, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j3 = 8 + (flag1 ? 8 : 0);
                        int k3 = 8 * (flag1 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(j2, k2, 40.0F, (float) j3, 8, k3, 8, 8, 64.0F, 64.0F);
                    }

                    j2 += 9;
                }

                if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = EnumChatFormatting.ITALIC + s1;
                    mc.fontRendererObj.drawStringWithShadow(s1, (float) j2, (float) k2, -1862270977);
                } else {
                    mc.fontRendererObj.drawStringWithShadow(s1, (float) j2, (float) k2, -1);
                }

                if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int k5 = j2 + i + 1;
                    int l5 = k5 + l;

                    if (l5 - k5 > 5) {
                        renderScoreboardValues(scoreObjectiveIn, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
                    }
                }

                renderPing(i1, j2 - (flag ? 9 : 0), k2, networkplayerinfo1);
            }
        }

        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            Gui.drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * mc.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);

            for (String s4 : list2) {
                int j5 = mc.fontRendererObj.getStringWidth(s4);
                mc.fontRendererObj.drawStringWithShadow(s4, (float) (width / 2 - j5 / 2), (float) k1, -1);
                k1 += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

}
