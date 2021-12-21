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
import net.minecraft.util.ChatComponentText;
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
    private float percentComplete = 0f;
    private boolean retract = false;
    private boolean animations;

    @Final
    @Shadow
    private Minecraft mc;

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
        if (TabulousConfig.modEnabled) {
            switch (TabulousConfig.strategy) {
                case 0:
                    animations = false;
                    renderVanillaPlus(width, scoreboardIn, scoreObjectiveIn);
                    ci.cancel();
                    break;
                default:
                case 1:
                    animations = true;
                    renderVanillaPlus(width, scoreboardIn, scoreObjectiveIn);
                    ci.cancel();
                    break;
                case 2:
                    renderCustom(width, scoreboardIn, scoreObjectiveIn);
                    ci.cancel();
                    break;
                case 3:
                    ci.cancel();
                    break;
            }
        }
    }


    private static float clamp(float number) {
        return number < (float) 0.0 ? (float) 0.0 : Math.min(number, (float) 1.0);
    }

    private static float easeOut(float current, float goal) {
        if (Math.floor(Math.abs(goal - current) / (float) 0.01) > 0) {
            return current + (goal - current) / (float) 15.0;
        } else {
            return goal;
        }
    }


    /**
     * vanilla method for drawing the scoreboard (with custom features)
     *
     * @param width            width
     * @param scoreboardIn     current scoreboard
     * @param scoreObjectiveIn current scoreboard objectives
     */
    public void renderVanillaPlus(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        if (!TabulousConfig.showHeader) header = null;
        if (!TabulousConfig.showFooter) footer = null;

        NetHandlerPlayClient nethandlerplayclient = mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> list = getPlayerInfoOrdering().sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        if (TabulousConfig.hideNPCs) {
            try {
                list.removeIf(info -> getPlayerName(info).startsWith("\u00A78[NPC]") || !getPlayerName(info).startsWith("\u00A7"));
            } catch (Exception ignored) {
            }
        }
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

        for (j4 = 1; i4 > TabulousConfig.overflow; i4 = (l3 + j4 - 1) / j4) {
            ++j4;
        }

        boolean flag = mc.isIntegratedServerRunning() || mc.getNetHandler().getNetworkManager().getIsencrypted();
        if (TabulousConfig.dontShowHeads) flag = false;
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
        int k1 = TabulousConfig.topPosition;
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

        // RENDER HEADER
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

                // RENDER HEAD
                if (flag) {
                    EntityPlayer entityplayer = mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    int l2 = 8 + (flag1 ? 8 : 0);
                    int i3 = 8 * (flag1 ? -1 : 1);
                    int headPos = j2;
                    if (TabulousConfig.headPos == 1) {
                        System.out.println("hi");
                        headPos = j2 + i1 - 8;
                    }
                    Gui.drawScaledCustomSizeModalRect(headPos, k2, 8.0F, (float) l2, 8, i3, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j3 = 8 + (flag1 ? 8 : 0);
                        int k3 = 8 * (flag1 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(headPos, k2, 40.0F, (float) j3, 8, k3, 8, 8, 64.0F, 64.0F);
                    }

                    if (TabulousConfig.headPos == 0) j2 += 9;
                }

                // RENDER NAMES
                if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = EnumChatFormatting.ITALIC + s1;
                    mc.fontRendererObj.drawStringWithShadow(s1, (float) j2, (float) k2, -1862270977);
                } else {
                    if (TabulousConfig.hideGuilds) {
                        if (s1.charAt(s1.length() - 1) == ']') {
                            s1 = s1.substring(0, s1.lastIndexOf("\u00A7"));
                        }
                    }
                    mc.fontRendererObj.drawStringWithShadow(s1, (float) j2, (float) k2, -1);
                }

                if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int k5 = j2 + i + 1;
                    int l5 = k5 + l;

                    if (l5 - k5 > 5) {
                        if (TabulousConfig.renderScoreboardValues) {
                            renderScoreboardValues(scoreObjectiveIn, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
                        }
                    }
                }
                if (TabulousConfig.renderPingNums && TabulousConfig.headPos != 1) {
                    String pingNum = "\u00A7";
                    int ping = networkplayerinfo1.getResponseTime();
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
                        pingNum += String.valueOf(networkplayerinfo1.getResponseTime());
                        mc.fontRendererObj.drawStringWithShadow(pingNum, i1 + j2 - (flag ? 9 : 0) - mc.fontRendererObj.getStringWidth(pingNum), k2, -1);
                    }
                    return;
                }
                if (TabulousConfig.renderPing && TabulousConfig.headPos != 1)
                    renderPing(i1, j2 - (flag ? 9 : 0), k2, networkplayerinfo1);
            }
        }

        // RENDER FOOTER
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

    /**
     * Method for drawing the scoreboard with animations and color customization options. fully custom + de-obfuscated.
     * @param width            screen width
     * @param scoreboardIn     current scoreboard
     * @param scoreObjectiveIn current scoreboard objectives
     */
    public void renderCustom(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn) {
        if (!TabulousConfig.showHeader) header = null;
        if (!TabulousConfig.headerText.equals("default")) {
            header = new ChatComponentText(TabulousConfig.headerText);
        }
        if (!TabulousConfig.showFooter) footer = null;
        if (!TabulousConfig.footerText.equals("default")) {
            footer = new ChatComponentText(TabulousConfig.footerText);
        }
        NetHandlerPlayClient nethandlerplayclient = mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> list = getPlayerInfoOrdering().sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        if (TabulousConfig.hideNPCs) {
            try {
                list.removeIf(info -> getPlayerName(info).startsWith("\u00A78[NPC]") || !getPlayerName(info).startsWith("\u00A7"));
            } catch (Exception ignored) {}
        }
        int nameLength = 0;
        int objWidth = 0;
        for (NetworkPlayerInfo networkplayerinfo : list) {
            int stringWidth = mc.fontRendererObj.getStringWidth(getPlayerName(networkplayerinfo));
            nameLength = Math.max(nameLength, stringWidth);

            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                stringWidth = mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
                objWidth = Math.max(objWidth, stringWidth);
            }
        }

        list = list.subList(0, Math.min(list.size(), 80));
        int listSize = list.size();
        int columnHeight;
        for (columnHeight = 1; listSize > TabulousConfig.overflow; listSize = (listSize + columnHeight - 1) / columnHeight) {
            ++columnHeight;
        }

        boolean renderHeads = mc.isIntegratedServerRunning() || mc.getNetHandler().getNetworkManager().getIsencrypted();
        if (TabulousConfig.dontShowHeads) renderHeads = false;
        int objLength;

        if (scoreObjectiveIn != null) {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                objLength = 90;
            } else {
                objLength = objWidth;
            }
        } else {
            objLength = 0;
        }

        int columnWidth = Math.min(columnHeight * ((renderHeads ? 9 : 0) + nameLength + objLength + 13), width - 50) / columnHeight;
        int num1 = width / 2 - (columnWidth * columnHeight + (columnHeight - 1) * 5) / 2;
        int top = TabulousConfig.topPosition;
        int num2 = columnWidth * columnHeight + (columnHeight - 1) * 5;

        List<String> headerList = null;
        List<String> footerList = null;

        if (getHeader() != null) {
            headerList = mc.fontRendererObj.listFormattedStringToWidth(getHeader().getFormattedText(), width - 50);

            for (String s : headerList) {
                num2 = Math.max(num2, mc.fontRendererObj.getStringWidth(s));
            }
        }

        if (getFooter() != null) {
            footerList = mc.fontRendererObj.listFormattedStringToWidth(getFooter().getFormattedText(), width - 50);

            for (String s : footerList) {
                num2 = Math.max(num2, mc.fontRendererObj.getStringWidth(s));
            }
        }

        // RENDER HEADER
        if (headerList != null) {
            Gui.drawRect(width / 2 - num2 / 2 - 1, top - 1, width / 2 + num2 / 2 + 1, top + headerList.size() * mc.fontRendererObj.FONT_HEIGHT, TabulousConfig.tabColor.getRGB());

            for (String s : headerList) {
                int i2 = mc.fontRendererObj.getStringWidth(s);
                mc.fontRendererObj.drawStringWithShadow(s, (float) (width / 2 - i2 / 2), (float) top, -1);
                top += mc.fontRendererObj.FONT_HEIGHT;
            }

            ++top;
        }

        Gui.drawRect(width / 2 - num2 / 2 - 1, top - 1, width / 2 + num2 / 2 + 1, top + listSize * 9, TabulousConfig.tabColor.getRGB());
        for (int iterator = 0; iterator < listSize; ++iterator) {
            int current = iterator / listSize;
            int currentRemainder = iterator % listSize;
            int currentLeft = num1 + current * columnWidth + current * 5;
            int currentTop = top + currentRemainder * 9;
            Gui.drawRect(currentLeft, currentTop, currentLeft + columnWidth, currentTop + 8, TabulousConfig.tabNameColor.getRGB());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            if (iterator < list.size()) {
                NetworkPlayerInfo networkplayerinfo1 = list.get(iterator);
                String name = getPlayerName(networkplayerinfo1);
                GameProfile gameprofile = networkplayerinfo1.getGameProfile();

                // RENDER HEAD
                if (renderHeads) {
                    EntityPlayer entityplayer = mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                    boolean hasCape = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    int headTextureV = 8 + (hasCape ? 8 : 0);
                    int headTextureSizeV = 8 * (hasCape ? -1 : 1);
                    int headPos = currentLeft;
                    if (TabulousConfig.headPos == 1) {
                        headPos = currentLeft + columnWidth - 8;
                    }
                    Gui.drawScaledCustomSizeModalRect(headPos, currentTop, 8.0F, (float) headTextureV, 8, headTextureSizeV, 8, 8, 64.0F, 64.0F);

                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        Gui.drawScaledCustomSizeModalRect(headPos, currentTop, 40.0F, (float) headTextureV, 8, headTextureSizeV, 8, 8, 64.0F, 64.0F);
                    }

                    if (TabulousConfig.headPos == 0) currentLeft += 9;
                }

                // RENDER NAMES
                if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    name = EnumChatFormatting.ITALIC + name;
                    mc.fontRendererObj.drawStringWithShadow(name, (float) currentLeft, (float) currentTop, -1862270977);
                } else {
                    if (TabulousConfig.hideGuilds) {
                        if (name.charAt(name.length() - 1) == ']') {
                            name = name.substring(0, name.lastIndexOf("\u00A7"));
                        }
                    }
                    mc.fontRendererObj.drawStringWithShadow(name, (float) currentLeft, (float) currentTop, -1);
                }

                if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int rightName = currentLeft + nameLength + 1;
                    int rightObj = rightName + objLength;

                    if (rightObj - rightName > 5) {
                        if (TabulousConfig.renderScoreboardValues) {
                            renderScoreboardValues(scoreObjectiveIn, currentTop, gameprofile.getName(), rightName, rightObj, networkplayerinfo1);
                        }
                    }
                }
                if (TabulousConfig.renderPingNums && TabulousConfig.headPos != 1) {
                    String pingNum = "\u00A7";
                    int ping = networkplayerinfo1.getResponseTime();
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
                        pingNum += String.valueOf(networkplayerinfo1.getResponseTime());
                        mc.fontRendererObj.drawStringWithShadow(pingNum, nameLength + currentLeft - (renderHeads ? 9 : 0) - mc.fontRendererObj.getStringWidth(pingNum), currentTop, -1);
                    }
                    return;
                }
                if (TabulousConfig.renderPing && TabulousConfig.headPos != 1)
                    renderPing(nameLength, currentLeft - (renderHeads ? 9 : 0), currentTop, networkplayerinfo1);
            }
        }

        // RENDER FOOTER
        if (footerList != null) {
            top = top + listSize * 9 + 1;
            Gui.drawRect(width / 2 - num2 / 2 - 1, top - 1, width / 2 + num2 / 2 + 1, top + footerList.size() * mc.fontRendererObj.FONT_HEIGHT, TabulousConfig.tabColor.getRGB());

            for (String s : footerList) {
                int stringWidth = mc.fontRendererObj.getStringWidth(s);
                mc.fontRendererObj.drawStringWithShadow(s, (float) (width / 2 - stringWidth / 2), (float) top, -1);
                top += mc.fontRendererObj.FONT_HEIGHT;
            }
        }
    }
}
