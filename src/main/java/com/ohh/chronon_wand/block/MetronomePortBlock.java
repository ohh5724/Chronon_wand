package com.ohh.chronon_wand.block;

import com.mojang.serialization.MapCodec;
import com.ohh.chronon_wand.block.entity.MetronomePortBlockEntity;
import com.ohh.chronon_wand.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MetronomePortBlock extends BaseEntityBlock {
    public static final MapCodec<MetronomePortBlock> CODEC = simpleCodec(MetronomePortBlock::new);

    public MetronomePortBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                 @NotNull BlockState state,
                                                                 @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.METRONOME_PORT.get(), MetronomePortBlockEntity::serverTick);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MetronomePortBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable net.minecraft.world.entity.LivingEntity placer, @NotNull net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof net.minecraft.world.entity.player.Player player) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MetronomePortBlockEntity port) {
                port.setOwner(player.getGameProfile().getId());
            }
        }
    }

    @Override
    protected @NotNull net.minecraft.world.ItemInteractionResult useItemOn(@NotNull net.minecraft.world.item.ItemStack stack,
                                                                          @NotNull BlockState state,
                                                                          @NotNull net.minecraft.world.level.Level level,
                                                                          @NotNull BlockPos pos,
                                                                          @NotNull net.minecraft.world.entity.player.Player player,
                                                                          @NotNull net.minecraft.world.InteractionHand hand,
                                                                          @NotNull net.minecraft.world.phys.BlockHitResult hitResult) {
        if (level.isClientSide) {
            return net.minecraft.world.ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MetronomePortBlockEntity port)) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (port.getOwner() == null && player != null) {
            port.setOwner(player.getGameProfile().getId());
        }

        earth.terrarium.tempad.api.tva_device.ChrononHandler itemHandler = stack.getCapability(earth.terrarium.tempad.api.tva_device.ChrononHandler.Capabilities.getItem());
        if (itemHandler != null) {
            if (player.isShiftKeyDown()) {
                // Insert chronons from item into the port
                int itemPower = itemHandler.getPower();
                if (itemPower <= 0) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.chronon_wand.item_empty"), true);
                    return net.minecraft.world.ItemInteractionResult.SUCCESS;
                }
                int simulatedInsert = port.insert(itemPower, earth.terrarium.tempad.api.ActionType.Simulate);
                if (simulatedInsert <= 0) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.chronon_wand.port_full"), true);
                    return net.minecraft.world.ItemInteractionResult.SUCCESS;
                }
                int extracted = itemHandler.extract(simulatedInsert, earth.terrarium.tempad.api.ActionType.Execute);
                port.insert(extracted, earth.terrarium.tempad.api.ActionType.Execute);
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.chronon_wand.inserted_chronons", extracted), true);
                return net.minecraft.world.ItemInteractionResult.SUCCESS;
            } else {
                // Extract chronons from port into the item
                int spaceInItem = itemHandler.getMaxPower() - itemHandler.getPower();
                if (spaceInItem <= 0) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.chronon_wand.item_full"), true);
                    return net.minecraft.world.ItemInteractionResult.SUCCESS;
                }
                int simulatedExtract = port.extract(spaceInItem, earth.terrarium.tempad.api.ActionType.Simulate);
                if (simulatedExtract <= 0) {
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.chronon_wand.port_empty"), true);
                    return net.minecraft.world.ItemInteractionResult.SUCCESS;
                }
                int inserted = itemHandler.insert(simulatedExtract, earth.terrarium.tempad.api.ActionType.Execute);
                port.extract(inserted, earth.terrarium.tempad.api.ActionType.Execute);
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.chronon_wand.extracted_chronons", inserted), true);
                return net.minecraft.world.ItemInteractionResult.SUCCESS;
            }
        }

        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull net.minecraft.world.InteractionResult useWithoutItem(@NotNull BlockState state,
                                                                          @NotNull net.minecraft.world.level.Level level,
                                                                          @NotNull BlockPos pos,
                                                                          @NotNull net.minecraft.world.entity.player.Player player,
                                                                          @NotNull net.minecraft.world.phys.BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MetronomePortBlockEntity port) {
                if (port.getOwner() == null && player != null) {
                    port.setOwner(player.getGameProfile().getId());
                }
                net.minecraft.world.inventory.ContainerData serverData = new net.minecraft.world.inventory.ContainerData() {
                    @Override
                    public int get(int index) {
                        com.ohh.chronon_wand.block.PortChannel channel = index < 6 ? com.ohh.chronon_wand.block.PortChannel.CHRONON_ENERGY : com.ohh.chronon_wand.block.PortChannel.CHRONON_FLUID;
                        net.minecraft.core.Direction dir = net.minecraft.core.Direction.from3DDataValue(index % 6);
                        return port.getSideMode(channel, dir).ordinal();
                    }

                    @Override
                    public void set(int index, int value) {
                        com.ohh.chronon_wand.block.PortChannel channel = index < 6 ? com.ohh.chronon_wand.block.PortChannel.CHRONON_ENERGY : com.ohh.chronon_wand.block.PortChannel.CHRONON_FLUID;
                        net.minecraft.core.Direction dir = net.minecraft.core.Direction.from3DDataValue(index % 6);
                        com.ohh.chronon_wand.block.SideMode[] modes = com.ohh.chronon_wand.block.SideMode.values();
                        if (value >= 0 && value < modes.length) {
                            port.setSideMode(channel, dir, modes[value]);
                        }
                    }

                    @Override
                    public int getCount() {
                        return 12;
                    }
                };

                player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, p) -> new com.ohh.chronon_wand.menu.MetronomePortMenu(id, inv, pos, serverData),
                        net.minecraft.network.chat.Component.translatable("gui.chronon_wand.metronome_port.title")
                ), pos);
            }
        }
        return net.minecraft.world.InteractionResult.sidedSuccess(level.isClientSide);
    }
}

