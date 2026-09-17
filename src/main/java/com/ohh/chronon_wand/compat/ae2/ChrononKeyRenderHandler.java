package com.ohh.chronon_wand.compat.ae2;

import appeng.api.client.AEKeyRenderHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ohh.chronon_wand.ChrononWand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ChrononKeyRenderHandler implements AEKeyRenderHandler<ChrononKey> {
    public static final ChrononKeyRenderHandler INSTANCE = new ChrononKeyRenderHandler();
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(ChrononWand.MOD_ID, "textures/energy/chronons.png");

    private ChrononKeyRenderHandler() {}

    @Override
    public void drawInGui(Minecraft minecraft, GuiGraphics guiGraphics, int x, int y, ChrononKey key) {
        guiGraphics.blit(ICON, x, y, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void drawOnBlockFace(PoseStack poseStack, MultiBufferSource buffers, ChrononKey key, float scale, int combinedLight, Level level) {
    }

    @Override
    public Component getDisplayName(ChrononKey key) {
        return key.getDisplayName();
    }
}

