package com.ohh.chronon_wand.client.screen;

import com.ohh.chronon_wand.config.ChrononWandConfig;
import com.ohh.chronon_wand.menu.ChrononAcceleratorMenu;
import com.ohh.chronon_wand.network.AcceleratorSettingsPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChrononAcceleratorScreen extends AbstractContainerScreen<ChrononAcceleratorMenu> {

    private int speedTierIndex;
    private int redstoneMode;
    private double xRadius, yRadius, zRadius;
    private int xOffset, yOffset, zOffset;
    private boolean renderArea;

    public ChrononAcceleratorScreen(ChrononAcceleratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 260;
        this.imageHeight = 195;
    }

    @Override
    protected void init() {
        super.init();
        this.speedTierIndex = this.menu.getSpeedTierIndex();
        this.redstoneMode = this.menu.getRedstoneMode();
        this.xRadius = this.menu.getXRadius();
        this.yRadius = this.menu.getYRadius();
        this.zRadius = this.menu.getZRadius();
        this.xOffset = this.menu.getXOffset();
        this.yOffset = this.menu.getYOffset();
        this.zOffset = this.menu.getZOffset();
        this.renderArea = this.menu.getRenderArea();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // Background dark panel
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xF018181E);
        guiGraphics.renderOutline(x, y, this.imageWidth, this.imageHeight, 0xFF3D3D4D);

        // Header banner
        guiGraphics.fill(x + 2, y + 2, x + this.imageWidth - 2, y + 20, 0xFF242430);

        // 1. Chronon Power Bar (Left Column)
        int barX = x + 10;
        int barY = y + 28;
        int barWidth = 16;
        int barHeight = 120;
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF181822);
        
        int power = this.menu.getPower();
        int maxPower = this.menu.getMaxPower();
        if (maxPower > 0) {
            int fillHeight = (int) Math.min(barHeight, (power / (float) maxPower) * barHeight);
            if (fillHeight > 0) {
                guiGraphics.fillGradient(barX + 1, barY + barHeight - fillHeight + 1, barX + barWidth - 1, barY + barHeight - 1, 0xFFAB47BC, 0xFF6A1B9A);
            }
        }
        guiGraphics.renderOutline(barX, barY, barWidth, barHeight, 0xFF444455);

        // Main content area to the right of the power bar
        int startX = x + 34;

        // 2. Speed Tier & Cost Row
        int tierCount = ChrononWandConfig.DATA.speedTiers.size();
        int tier = Math.clamp(speedTierIndex, 0, Math.max(0, tierCount - 1));
        int displaySpeed = tierCount > 0 ? ChrononWandConfig.DATA.speedTiers.get(tier) : 1;
        int displayCost = (tierCount > 0 && tier < ChrononWandConfig.DATA.costPerSecond.size()) ? ChrononWandConfig.DATA.costPerSecond.get(tier) : 0;

        guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.accelerator.speed", displaySpeed), startX, y + 30, 0xFFFFFFFF);
        
        drawButton(guiGraphics, startX + 72, y + 27, 14, 14, "<", isHovering(mouseX, mouseY, startX + 72, y + 27, 14, 14), 0xFF353545, 0xFF555566);
        drawButton(guiGraphics, startX + 90, y + 27, 14, 14, ">", isHovering(mouseX, mouseY, startX + 90, y + 27, 14, 14), 0xFF353545, 0xFF555566);

        guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.accelerator.cost", displayCost), startX + 115, y + 30, 0xFFAAAAAA);

        // 3. Area Controls Header & Separator
        guiGraphics.drawString(this.font, Component.literal("Area Configuration:"), startX, y + 49, 0xFF8E8EA0);
        guiGraphics.fill(startX, y + 59, startX + 216, y + 60, 0xFF2D2D3B);

        // Column 1: Radius
        int col1X = startX;
        guiGraphics.drawString(this.font, Component.literal("Radius (0-5)"), col1X, y + 65, 0xFFDDDDDD);
        renderAxisRow(guiGraphics, mouseX, mouseY, col1X, y + 78, "X", formatRadius(xRadius));
        renderAxisRow(guiGraphics, mouseX, mouseY, col1X, y + 95, "Y", formatRadius(yRadius));
        renderAxisRow(guiGraphics, mouseX, mouseY, col1X, y + 112, "Z", formatRadius(zRadius));

        // Column 2: Offset
        int col2X = startX + 112;
        guiGraphics.drawString(this.font, Component.literal("Offset (±9)"), col2X, y + 65, 0xFFDDDDDD);
        renderAxisRow(guiGraphics, mouseX, mouseY, col2X, y + 78, "X", String.valueOf(xOffset));
        renderAxisRow(guiGraphics, mouseX, mouseY, col2X, y + 95, "Y", String.valueOf(yOffset));
        renderAxisRow(guiGraphics, mouseX, mouseY, col2X, y + 112, "Z", String.valueOf(zOffset));

        // 4. Action Buttons
        int actionY = y + 132;
        int renderBtnBg = renderArea ? 0xFF8E24AA : 0xFF2A2A38;
        int renderBtnBorder = renderArea ? 0xFFBA68C8 : 0xFF444455;
        drawButton(guiGraphics, startX, actionY, 86, 18, Component.translatable("gui.chronon_wand.accelerator.render_area").getString(), isHovering(mouseX, mouseY, startX, actionY, 86, 18), renderBtnBg, renderBtnBorder);

        String redstoneKey = redstoneMode == 0 ? "gui.chronon_wand.accelerator.redstone.ignored" : (redstoneMode == 1 ? "gui.chronon_wand.accelerator.redstone.low" : "gui.chronon_wand.accelerator.redstone.high");
        drawButton(guiGraphics, startX + 92, actionY, 124, 18, Component.translatable(redstoneKey).getString(), isHovering(mouseX, mouseY, startX + 92, actionY, 124, 18), 0xFF2E2E3E, 0xFF4E4E62);

        // 5. Status Panel (Bottom)
        int statusY = y + 158;
        guiGraphics.fill(x + 10, statusY, x + this.imageWidth - 10, statusY + 26, 0xFF14141C);
        guiGraphics.renderOutline(x + 10, statusY, this.imageWidth - 20, 26, 0xFF282836);

        int blockCount = this.menu.getAcceleratedBlockCount();
        if (power == 0) {
            guiGraphics.fill(x + 18, statusY + 11, x + 24, statusY + 17, 0xFFFF4444);
            guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.accelerator.status.no_power"), x + 28, statusY + 9, 0xFFFF5555);
        } else if (blockCount > 0) {
            guiGraphics.fill(x + 18, statusY + 11, x + 24, statusY + 17, 0xFF44FF44);
            guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.accelerator.status.accelerating", blockCount), x + 28, statusY + 9, 0xFF55FF55);
        } else {
            guiGraphics.fill(x + 18, statusY + 11, x + 24, statusY + 17, 0xFF888888);
            guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.accelerator.status.idle"), x + 28, statusY + 9, 0xFFAAAAAA);
        }
    }

    private String formatRadius(double r) {
        if (r == Math.floor(r)) {
            return String.valueOf((int) r);
        }
        return String.valueOf(r);
    }

    private void renderAxisRow(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, String axis, String valueStr) {
        guiGraphics.drawString(this.font, axis + ":", x, y + 3, 0xFFCCCCCC);
        
        drawButton(guiGraphics, x + 16, y, 14, 14, "-", isHovering(mouseX, mouseY, x + 16, y, 14, 14), 0xFF323242, 0xFF4E4E62);
        guiGraphics.drawCenteredString(this.font, valueStr, x + 44, y + 3, 0xFFFFFFFF);
        drawButton(guiGraphics, x + 58, y, 14, 14, "+", isHovering(mouseX, mouseY, x + 58, y, 14, 14), 0xFF323242, 0xFF4E4E62);
    }

    private void drawButton(GuiGraphics guiGraphics, int x, int y, int width, int height, String text, boolean hovered, int bgColor, int borderColor) {
        int finalBg = hovered ? (bgColor | 0x222222) : bgColor;
        guiGraphics.fill(x, y, x + width, y + height, finalBg);
        guiGraphics.renderOutline(x, y, width, height, borderColor);
        guiGraphics.drawCenteredString(this.font, text, x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
    }

    private boolean isHovering(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0xFFE0E0E0, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int barX = this.leftPos + 10;
        int barY = this.topPos + 28;
        if (mouseX >= barX && mouseX < barX + 16 && mouseY >= barY && mouseY < barY + 120) {
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.chronon_wand.accelerator.chronons", this.menu.getPower(), this.menu.getMaxPower()), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.leftPos;
        int y = this.topPos;
        int startX = x + 34;

        // Speed buttons
        if (mouseY >= y + 27 && mouseY < y + 41) {
            if (mouseX >= startX + 72 && mouseX < startX + 86) {
                if (speedTierIndex > 0) {
                    speedTierIndex--;
                    sendSettings();
                }
                return true;
            } else if (mouseX >= startX + 90 && mouseX < startX + 104) {
                if (speedTierIndex < ChrononWandConfig.DATA.speedTiers.size() - 1) {
                    speedTierIndex++;
                    sendSettings();
                }
                return true;
            }
        }

        // Radius Column buttons
        double step = hasShiftDown() ? 1.0 : 0.5;
        int col1X = startX;
        if (handleAxisClick(mouseX, mouseY, col1X, y + 78, () -> { if (xRadius > 0.0) xRadius = Math.max(0.0, xRadius - step); }, () -> { if (xRadius < 5.0) xRadius = Math.min(5.0, xRadius + step); })) return true;
        if (handleAxisClick(mouseX, mouseY, col1X, y + 95, () -> { if (yRadius > 0.0) yRadius = Math.max(0.0, yRadius - step); }, () -> { if (yRadius < 5.0) yRadius = Math.min(5.0, yRadius + step); })) return true;
        if (handleAxisClick(mouseX, mouseY, col1X, y + 112, () -> { if (zRadius > 0.0) zRadius = Math.max(0.0, zRadius - step); }, () -> { if (zRadius < 5.0) zRadius = Math.min(5.0, zRadius + step); })) return true;

        // Offset Column buttons
        int col2X = startX + 112;
        if (handleAxisClick(mouseX, mouseY, col2X, y + 78, () -> { if (xOffset > -9) xOffset--; }, () -> { if (xOffset < 9) xOffset++; })) return true;
        if (handleAxisClick(mouseX, mouseY, col2X, y + 95, () -> { if (yOffset > -9) yOffset--; }, () -> { if (yOffset < 9) yOffset++; })) return true;
        if (handleAxisClick(mouseX, mouseY, col2X, y + 112, () -> { if (zOffset > -9) zOffset--; }, () -> { if (zOffset < 9) zOffset++; })) return true;

        // Action buttons
        int actionY = y + 132;
        if (mouseY >= actionY && mouseY < actionY + 18) {
            if (mouseX >= startX && mouseX < startX + 86) {
                renderArea = !renderArea;
                sendSettings();
                return true;
            } else if (mouseX >= startX + 92 && mouseX < startX + 216) {
                redstoneMode = (redstoneMode + 1) % 3;
                sendSettings();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean handleAxisClick(double mouseX, double mouseY, int x, int y, Runnable onDec, Runnable onInc) {
        if (mouseY >= y && mouseY < y + 14) {
            if (mouseX >= x + 16 && mouseX < x + 30) {
                onDec.run();
                sendSettings();
                return true;
            } else if (mouseX >= x + 58 && mouseX < x + 72) {
                onInc.run();
                sendSettings();
                return true;
            }
        }
        return false;
    }

    private void sendSettings() {
        playClickSound();
        PacketDistributor.sendToServer(new AcceleratorSettingsPayload(this.menu.getPos(), speedTierIndex, redstoneMode, xRadius, yRadius, zRadius, xOffset, yOffset, zOffset, renderArea));
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}

