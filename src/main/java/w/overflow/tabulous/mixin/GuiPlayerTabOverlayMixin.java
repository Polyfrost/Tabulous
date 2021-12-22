package w.overflow.tabulous.mixin;


import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;
import org.lwjgl.input.Keyboard;
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
    private boolean active = false;
    private boolean keyState = false;

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


    @Inject(method = "renderPlayerlist", at = @At("HEAD"), cancellable = true)
    public void renderPlayerlist(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn, CallbackInfo ci) {
        if (TabulousConfig.modEnabled) {
            switch (TabulousConfig.strategy) {
                case 0:
                    render(width, scoreboardIn, scoreObjectiveIn, false, false);
                    ci.cancel();
                    break;
                default:
                case 1:
                    render(width, scoreboardIn, scoreObjectiveIn, true, false);
                    ci.cancel();
                    break;
                case 2:
                    render(width, scoreboardIn, scoreObjectiveIn, true, true);
                    ci.cancel();
                    break;
                case 3:
                    ci.cancel();
                    break;
            }
        }
    }

    @Inject(method = "updatePlayerList", at = @At("HEAD"))
    public void updatePlayerList(CallbackInfo ci) {
        if (TabulousConfig.toggle) {            // TODO fix
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode()) && !keyState) {
                active = !active;
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), active);
                System.out.println(active);
            }
            keyState = Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode());
        }
        if (TabulousConfig.strategy == 1 || TabulousConfig.strategy == 2) {
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


    /**
     * Method for drawing the scoreboard with animations and color customization options. fully custom + de-obfuscated.
     *
     * @param width            screen width
     * @param scoreboardIn     current scoreboard
     * @param scoreObjectiveIn current scoreboard objectives
     * @param animations       should use animations
     * @param useNewStyling    should use the Tabulous (quickGUI) styling
     */
    public void render(int width, Scoreboard scoreboardIn, ScoreObjective scoreObjectiveIn, boolean animations, boolean useNewStyling) {
        if (!TabulousConfig.showHeader) header = null;
        if (!TabulousConfig.showFooter) footer = null;
        int playerNameColor = 553648127;
        int tabColor = Integer.MIN_VALUE;
        if (TabulousConfig.customTab) {
            if (!TabulousConfig.headerText.equals("default")) {
                header = new ChatComponentText(TabulousConfig.headerText);
            }
            if (!TabulousConfig.footerText.equals("default")) {
                footer = new ChatComponentText(TabulousConfig.footerText);
            }
            playerNameColor = TabulousConfig.tabItemColor.getRGB();
            tabColor = TabulousConfig.tabColor.getRGB();
        }

        NetHandlerPlayClient nethandlerplayclient = mc.thePlayer.sendQueue;
        List<NetworkPlayerInfo> list = getPlayerInfoOrdering().sortedCopy(nethandlerplayclient.getPlayerInfoMap());
        if (TabulousConfig.hideNPCs) {
            try {
                list.removeIf(info -> getPlayerName(info).startsWith("\u00A78[NPC]") || !getPlayerName(info).startsWith("\u00A7"));
            } catch (Exception ignored) {
            }
        }
        int nameLength = 0;
        int objectiveWidth = 0;
        for (NetworkPlayerInfo networkplayerinfo : list) {
            int stringWidth = mc.fontRendererObj.getStringWidth(getPlayerName(networkplayerinfo));
            if (TabulousConfig.customTab) {
                if (getPlayerName(networkplayerinfo).contains(mc.getSession().getUsername())) {
                    stringWidth = mc.fontRendererObj.getStringWidth(TabulousConfig.myNameText);
                }
            }
            nameLength = Math.max(nameLength, stringWidth);
            if (scoreObjectiveIn != null && scoreObjectiveIn.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                stringWidth = mc.fontRendererObj.getStringWidth(" " + scoreboardIn.getValueFromObjective(networkplayerinfo.getGameProfile().getName(), scoreObjectiveIn).getScorePoints());
                objectiveWidth = Math.max(objectiveWidth, stringWidth);
            }
        }

        list = list.subList(0, Math.min(list.size(), 80));
        int listSize = list.size();
        int listSize2 = listSize;
        int columnHeight;

        for (columnHeight = 1; listSize2 > TabulousConfig.overflow; listSize2 = (listSize + columnHeight - 1) / columnHeight) {
            ++columnHeight;
        }

        boolean shouldRenderHeads = mc.isIntegratedServerRunning() || mc.getNetHandler().getNetworkManager().getIsencrypted();
        if (TabulousConfig.dontShowHeads) shouldRenderHeads = false;
        int objRowWidth;

        if (scoreObjectiveIn != null) {
            if (scoreObjectiveIn.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                objRowWidth = 90;
            } else {
                objRowWidth = objectiveWidth;
            }
        } else {
            objRowWidth = 0;
        }

        int rowWidth = Math.min(columnHeight * ((shouldRenderHeads ? 9 : 0) + nameLength + objRowWidth + 13), width - 50) / columnHeight;
        int num1 = width / 2 - (rowWidth * columnHeight + (columnHeight - 1) * 5) / 2;
        int top = TabulousConfig.topPosition;
        int num2 = rowWidth * columnHeight + (columnHeight - 1) * 5;
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

        if (animations) {
            percentComplete = clamp(easeOut(percentComplete, retract ? 0f : 1f));
        } else {
            percentComplete = 1f;
        }
        if (retract && Keyboard.isKeyDown(mc.gameSettings.keyBindPlayerList.getKeyCode())) {
            retract = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), false);
        }
        int bottom = listSize2 * 9;
        if (headerList != null) bottom += (headerList.size() * 10);
        if (footerList != null) bottom += (footerList.size() * 9);
        int currentBottom = (int) (percentComplete * bottom);
        Gui.drawRect(width / 2 - num2 / 2 - 1, TabulousConfig.topPosition - 1, width / 2 + num2 / 2 + 1, top + currentBottom, tabColor);
        if (retract && percentComplete == 0f) {
            retract = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindPlayerList.getKeyCode(), false);
        }


        if (percentComplete > 0.9f) {
            if (headerList != null) {
                for (String s : headerList) {
                    int stringWidth = mc.fontRendererObj.getStringWidth(s);
                    mc.fontRendererObj.drawStringWithShadow(s, (float) (width / 2 - stringWidth / 2), (float) top, -1);
                    top += mc.fontRendererObj.FONT_HEIGHT;
                }

                ++top;
            }
            for (int iterator = 0; iterator < listSize; ++iterator) {
                int current = iterator / listSize2;
                int currentRemainder = iterator % listSize2;
                int currentLeft = num1 + current * rowWidth + current * 5;
                int currentTop = top + currentRemainder * 9;
                Gui.drawRect(currentLeft, currentTop, currentLeft + rowWidth, currentTop + 8, playerNameColor);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

                if (iterator < list.size()) {
                    NetworkPlayerInfo networkplayerinfo1 = list.get(iterator);
                    String playerName = getPlayerName(networkplayerinfo1);
                    GameProfile gameprofile = networkplayerinfo1.getGameProfile();

                    // RENDER HEAD
                    if (shouldRenderHeads) {
                        EntityPlayer entityplayer = mc.theWorld.getPlayerEntityByUUID(gameprofile.getId());
                        boolean hasCape = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE) && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                        mc.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                        int headTextureV = 8 + (hasCape ? 8 : 0);
                        int headTextureSizeV = 8 * (hasCape ? -1 : 1);
                        int headPos = currentLeft;
                        if (TabulousConfig.headPos == 1) {
                            headPos = currentLeft + rowWidth - 8;
                        }
                        Gui.drawScaledCustomSizeModalRect(headPos, currentTop, 8.0F, (float) headTextureV, 8, headTextureSizeV, 8, 8, 64.0F, 64.0F);

                        if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                            int j3 = 8 + (hasCape ? 8 : 0);
                            int k3 = 8 * (hasCape ? -1 : 1);
                            Gui.drawScaledCustomSizeModalRect(headPos, currentTop, 40.0F, (float) j3, 8, k3, 8, 8, 64.0F, 64.0F);
                        }

                        if (TabulousConfig.headPos == 0) currentLeft += 9;
                    }

                    // RENDER NAMES
                    if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                        playerName = EnumChatFormatting.ITALIC + playerName;
                        mc.fontRendererObj.drawStringWithShadow(playerName, (float) currentLeft, (float) currentTop, -1862270977);
                    } else {
                        if (TabulousConfig.hideGuilds) {
                            if (playerName.charAt(playerName.length() - 1) == ']') {
                                playerName = playerName.substring(0, playerName.lastIndexOf("\u00A7"));
                            }
                        }
                        if (TabulousConfig.customTab) {
                            if (playerName.contains(mc.getSession().getUsername())) {
                                if (!TabulousConfig.myNameText.equals("default")) {
                                    playerName = TabulousConfig.myNameText;
                                }
                            }
                        }
                        mc.fontRendererObj.drawStringWithShadow(playerName, (float) currentLeft, (float) currentTop, -1);
                    }

                    if (scoreObjectiveIn != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR) {
                        int nameRight = currentLeft + nameLength + 1;
                        int objRight = nameRight + objRowWidth;

                        if (objRight - nameRight > 5) {
                            if (TabulousConfig.renderScoreboardValues) {
                                renderScoreboardValues(scoreObjectiveIn, currentTop, gameprofile.getName(), nameRight, objRight, networkplayerinfo1);
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
                            mc.fontRendererObj.drawStringWithShadow(pingNum, rowWidth + (currentLeft - (shouldRenderHeads ? 9 : 0) - mc.fontRendererObj.getStringWidth(pingNum)), currentTop, -1);
                        }
                        return;
                    }
                    if (TabulousConfig.renderPing && TabulousConfig.headPos != 1) {
                        renderPing(rowWidth, currentLeft - (shouldRenderHeads ? 9 : 0), currentTop, networkplayerinfo1);
                    }
                }
            }

            // RENDER FOOTER
            if (footerList != null) {
                top = top + listSize2 * 9 + 1;

                for (String s : footerList) {
                    int stringWidth = mc.fontRendererObj.getStringWidth(s);
                    mc.fontRendererObj.drawStringWithShadow(s, (float) (width / 2 - stringWidth / 2), (float) top, -1);
                    top += mc.fontRendererObj.FONT_HEIGHT;
                }
            }
        }
    }


}
