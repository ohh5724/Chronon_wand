package com.ohh.chronon_wand.network;

import com.ohh.chronon_wand.ChrononWand;
import com.ohh.chronon_wand.block.PortChannel;
import com.ohh.chronon_wand.block.SideMode;
import com.ohh.chronon_wand.block.entity.MetronomePortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetSideModePayload(BlockPos pos, PortChannel channel, Direction direction, SideMode mode) implements CustomPacketPayload {
    public static final Type<SetSideModePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ChrononWand.MOD_ID, "set_side_mode"));

    public static final StreamCodec<FriendlyByteBuf, SetSideModePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeBlockPos(payload.pos());
                buf.writeEnum(payload.channel());
                buf.writeEnum(payload.direction());
                buf.writeEnum(payload.mode());
            },
            buf -> new SetSideModePayload(
                    buf.readBlockPos(),
                    buf.readEnum(PortChannel.class),
                    buf.readEnum(Direction.class),
                    buf.readEnum(SideMode.class)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SetSideModePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.level().isLoaded(payload.pos()) && player.level().getBlockEntity(payload.pos()) instanceof MetronomePortBlockEntity be) {
                    be.setSideMode(payload.channel(), payload.direction(), payload.mode());
                }
            }
        });
    }
}
