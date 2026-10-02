package com.ohh.chronon_wand.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ohh.chronon_wand.block.entity.ChrononAcceleratorBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

public class ChrononAcceleratorRenderer implements BlockEntityRenderer<ChrononAcceleratorBlockEntity> {

    public ChrononAcceleratorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(ChrononAcceleratorBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!blockEntity.isRenderArea()) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        AABB baseAABB = blockEntity.getAABB(pos);
        AABB wireframeAABB = baseAABB.inflate(0.001);
        AABB solidAABB = baseAABB.inflate(0.001);

        poseStack.pushPose();
        poseStack.translate(-pos.getX(), -pos.getY(), -pos.getZ());

        // Wireframe
        VertexConsumer lineBuilder = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, lineBuilder, wireframeAABB, 0.0f, 1.0f, 0.0f, 0.8f);

        // Solid Fill
        VertexConsumer quadBuilder = bufferSource.getBuffer(RenderType.debugQuads());
        Matrix4f matrix = poseStack.last().pose();

        float minX = (float) solidAABB.minX;
        float minY = (float) solidAABB.minY;
        float minZ = (float) solidAABB.minZ;
        float maxX = (float) solidAABB.maxX;
        float maxY = (float) solidAABB.maxY;
        float maxZ = (float) solidAABB.maxZ;

        int red = 0;
        int green = 255;
        int blue = 0;
        int alpha = 25; // ~0.1f

        // Down
        quadBuilder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);

        // Up
        quadBuilder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);

        // North
        quadBuilder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);

        // South
        quadBuilder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);

        // West
        quadBuilder.addVertex(matrix, minX, minY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, minX, minY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, minX, maxY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, minX, maxY, minZ).setColor(red, green, blue, alpha);

        // East
        quadBuilder.addVertex(matrix, maxX, minY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, maxY, minZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha);
        quadBuilder.addVertex(matrix, maxX, minY, maxZ).setColor(red, green, blue, alpha);

        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 64;
    }

    @Override
    public AABB getRenderBoundingBox(ChrononAcceleratorBlockEntity blockEntity) {
        return blockEntity.getAABB(blockEntity.getBlockPos()).inflate(1.0);
    }
}
