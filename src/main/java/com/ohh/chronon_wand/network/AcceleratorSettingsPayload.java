package com.ohh.chronon_wand.network;

import com.ohh.chronon_wand.ChrononWand;
import com.ohh.chronon_wand.block.entity.ChrononAcceleratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AcceleratorSettingsPayload(BlockPos pos, int speedTierIndex, int redstoneMode, double xRadius, double yRadius, double zRadius, int xOffset, int yOffset, int zOffset, boolean renderArea) implements CustomPacketPayload {
    public static final Type<AcceleratorSettingsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ChrononWand.MOD_ID, "accelerator_settings"));

    public static final StreamCodec<FriendlyByteBuf, AcceleratorSettingsPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.pos());
                buf.writeInt(payload.speedTierIndex());
                buf.writeInt(payload.redstoneMode());
                buf.writeDouble(payload.xRadius());
                buf.writeDouble(payload.yRadius());
                buf.writeDouble(payload.zRadius());
                buf.writeInt(payload.xOffset());
                buf.writeInt(payload.yOffset());
                buf.writeInt(payload.zOffset());
                buf.writeBoolean(payload.renderArea());
            },
            buf -> new AcceleratorSettingsPayload(
                    buf.readBlockPos(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AcceleratorSettingsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.level().isLoaded(payload.pos()) && player.level().getBlockEntity(payload.pos()) instanceof ChrononAcceleratorBlockEntity be) {
                    be.setSpeedTierIndex(payload.speedTierIndex());
                    be.setRedstoneMode(payload.redstoneMode());
                    be.setXRadius(payload.xRadius());
                    be.setYRadius(payload.yRadius());
                    be.setZRadius(payload.zRadius());
                    be.setXOffset(payload.xOffset());
                    be.setYOffset(payload.yOffset());
                    be.setZOffset(payload.zOffset());
                    be.setRenderArea(payload.renderArea());
                }
            }
        });
    }
}
