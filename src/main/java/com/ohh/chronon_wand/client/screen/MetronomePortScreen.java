package com.ohh.chronon_wand.client.screen;

import com.ohh.chronon_wand.block.PortChannel;
import com.ohh.chronon_wand.block.SideMode;
import com.ohh.chronon_wand.menu.MetronomePortMenu;
import com.ohh.chronon_wand.network.SetSideModePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class MetronomePortScreen extends AbstractContainerScreen<MetronomePortMenu> {
    private PortChannel currentChannel = PortChannel.CHRONON_ENERGY;

    private static final int TILE_SIZE = 28;

    // Relative positions from center for the 6 faces in unfolded cube layout
    private record FacePos(Direction direction, int offsetX, int offsetY, String labelKey) {}

    private final List<FacePos> facePositions = List.of(
            new FacePos(Direction.UP, 0, -34, "gui.chronon_wand.direction.up"),
            new FacePos(Direction.WEST, -34, 0, "gui.chronon_wand.direction.west"),
            new FacePos(Direction.NORTH, 0, 0, "gui.chronon_wand.direction.north"),
            new FacePos(Direction.EAST, 34, 0, "gui.chronon_wand.direction.east"),
            new FacePos(Direction.SOUTH, 68, 0, "gui.chronon_wand.direction.south"),
            new FacePos(Direction.DOWN, 0, 34, "gui.chronon_wand.direction.down")
    );

    public MetronomePortScreen(MetronomePortMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 200;
        this.imageHeight = 185;
    }

    @Override
    protected void init() {
        super.init();
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

        // Tab bar (Chronons vs Chronon Liquid)
        int tabWidth = (this.imageWidth - 12) / 2;
        int tabY = y + 24;

        // Tab 1: Chronons
        boolean isEnergy = currentChannel == PortChannel.CHRONON_ENERGY;
        int tab1Color = isEnergy ? 0xFF3E3E58 : 0xFF22222C;
        guiGraphics.fill(x + 5, tabY, x + 5 + tabWidth, tabY + 18, tab1Color);
        guiGraphics.renderOutline(x + 5, tabY, tabWidth, 18, isEnergy ? 0xFF8E24AA : 0xFF444455);
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.chronon_wand.tab.chronons"), x + 5 + tabWidth / 2, tabY + 5, isEnergy ? 0xFFFFFFFF : 0xFFAAAAAA);

        // Tab 2: Chronon Liquid
        boolean isFluid = currentChannel == PortChannel.CHRONON_FLUID;
        int tab2Color = isFluid ? 0xFF3E3E58 : 0xFF22222C;
        guiGraphics.fill(x + 7 + tabWidth, tabY, x + 7 + tabWidth * 2, tabY + 18, tab2Color);
        guiGraphics.renderOutline(x + 7 + tabWidth, tabY, tabWidth, 18, isFluid ? 0xFFFFA020 : 0xFF444455);
        guiGraphics.drawCenteredString(this.font, Component.translatable("gui.chronon_wand.tab.chronon_fluid"), x + 7 + tabWidth + tabWidth / 2, tabY + 5, isFluid ? 0xFFFFFFFF : 0xFFAAAAAA);

        // Unfolded cube face buttons
        int centerX = x + 74;
        int centerY = y + 92;

        for (FacePos face : facePositions) {
            int tileX = centerX + face.offsetX();
            int tileY = centerY + face.offsetY();

            SideMode mode = this.menu.getSideMode(this.currentChannel, face.direction());
            int modeColor = mode.getColor();

            // Tile background & colored border
            guiGraphics.fill(tileX, tileY, tileX + TILE_SIZE, tileY + TILE_SIZE, 0xFF282834);
            guiGraphics.fill(tileX + 1, tileY + 1, tileX + TILE_SIZE - 1, tileY + TILE_SIZE - 1, 0x44000000 | (modeColor & 0x00FFFFFF));
            guiGraphics.renderOutline(tileX, tileY, TILE_SIZE, TILE_SIZE, modeColor);

            // Render adjacent block item inside the tile
            BlockState adjacent = this.menu.getLevel().getBlockState(this.menu.getPos().relative(face.direction()));
            if (!adjacent.isAir()) {
                ItemStack blockItem = adjacent.getBlock().asItem().getDefaultInstance();
                if (!blockItem.isEmpty()) {
                    guiGraphics.renderFakeItem(blockItem, tileX + (TILE_SIZE - 16) / 2, tileY + (TILE_SIZE - 16) / 2);
                } else {
                    // Fallback letter if block has no item representation
                    String initial = face.direction().getName().substring(0, 1).toUpperCase();
                    guiGraphics.drawCenteredString(this.font, initial, tileX + TILE_SIZE / 2, tileY + (TILE_SIZE - 8) / 2, 0xFFDDDDDD);
                }
            } else {
                // If air, display small direction indicator letter
                String initial = face.direction().getName().substring(0, 1).toUpperCase();
                guiGraphics.drawCenteredString(this.font, initial, tileX + TILE_SIZE / 2, tileY + (TILE_SIZE - 8) / 2, 0xFF666677);
            }
        }

        // Bottom Color Legend
        int legendY = y + 164;
        int legendBoxSize = 7;

        // Red: Input
        guiGraphics.fill(x + 10, legendY, x + 10 + legendBoxSize, legendY + legendBoxSize, SideMode.INPUT.getColor());
        guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.legend.input"), x + 20, legendY, 0xFFCCCCCC);

        // Blue: Output
        guiGraphics.fill(x + 58, legendY, x + 58 + legendBoxSize, legendY + legendBoxSize, SideMode.OUTPUT.getColor());
        guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.legend.output"), x + 68, legendY, 0xFFCCCCCC);

        // Purple: Both
        guiGraphics.fill(x + 110, legendY, x + 110 + legendBoxSize, legendY + legendBoxSize, SideMode.BOTH.getColor());
        guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.legend.both"), x + 120, legendY, 0xFFCCCCCC);

        // Gray: None
        guiGraphics.fill(x + 152, legendY, x + 152 + legendBoxSize, legendY + legendBoxSize, SideMode.NONE.getColor());
        guiGraphics.drawString(this.font, Component.translatable("gui.chronon_wand.legend.none"), x + 162, legendY, 0xFFCCCCCC);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0xFFE0E0E0, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        // Check if mouse is hovering over any side tile
        int centerX = this.leftPos + 74;
        int centerY = this.topPos + 92;

        for (FacePos face : facePositions) {
            int tileX = centerX + face.offsetX();
            int tileY = centerY + face.offsetY();

            if (mouseX >= tileX && mouseX < tileX + TILE_SIZE && mouseY >= tileY && mouseY < tileY + TILE_SIZE) {
                SideMode mode = this.menu.getSideMode(this.currentChannel, face.direction());
                BlockState adjacent = this.menu.getLevel().getBlockState(this.menu.getPos().relative(face.direction()));
                Component blockName = adjacent.isAir()
                        ? Component.translatable("gui.chronon_wand.block.air")
                        : adjacent.getBlock().getName();

                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable(face.labelKey()).withColor(0xFFFFFF));
                tooltip.add(Component.translatable("gui.chronon_wand.tooltip.mode", Component.translatable(mode.getTranslationKey())).withColor(mode.getColor()));
                tooltip.add(Component.translatable("gui.chronon_wand.tooltip.adjacent", blockName).withColor(0xFFAAAAAA));
                tooltip.add(Component.translatable("gui.chronon_wand.tooltip.cycle_hint").withColor(0xFF777788));

                guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = this.leftPos;
        int y = this.topPos;

        // Tab switching
        int tabWidth = (this.imageWidth - 12) / 2;
        int tabY = y + 24;

        if (mouseY >= tabY && mouseY < tabY + 18) {
            if (mouseX >= x + 5 && mouseX < x + 5 + tabWidth) {
                currentChannel = PortChannel.CHRONON_ENERGY;
                playClickSound();
                return true;
            } else if (mouseX >= x + 7 + tabWidth && mouseX < x + 7 + tabWidth * 2) {
                currentChannel = PortChannel.CHRONON_FLUID;
                playClickSound();
                return true;
            }
        }

        // Side tile clicking
        int centerX = x + 74;
        int centerY = y + 92;

        for (FacePos face : facePositions) {
            int tileX = centerX + face.offsetX();
            int tileY = centerY + face.offsetY();

            if (mouseX >= tileX && mouseX < tileX + TILE_SIZE && mouseY >= tileY && mouseY < tileY + TILE_SIZE) {
                SideMode currentMode = this.menu.getSideMode(this.currentChannel, face.direction());
                SideMode newMode = (button == 1) ? currentMode.previous() : currentMode.next();

                PacketDistributor.sendToServer(new SetSideModePayload(
                        this.menu.getPos(),
                        this.currentChannel,
                        face.direction(),
                        newMode
                ));

                playClickSound();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
