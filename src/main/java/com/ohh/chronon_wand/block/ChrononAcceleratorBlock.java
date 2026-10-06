package com.ohh.chronon_wand.block;

import com.mojang.serialization.MapCodec;
import com.ohh.chronon_wand.block.entity.ChrononAcceleratorBlockEntity;
import com.ohh.chronon_wand.block.entity.ModBlockEntities;
import com.ohh.chronon_wand.menu.ChrononAcceleratorMenu;
import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChrononAcceleratorBlock extends BaseEntityBlock {
    public static final MapCodec<ChrononAcceleratorBlock> CODEC = simpleCodec(ChrononAcceleratorBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public ChrononAcceleratorBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChrononAcceleratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.CHRONON_ACCELERATOR.get(), ChrononAcceleratorBlockEntity::serverTick);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ChrononAcceleratorBlockEntity blockEntity) {
            if (!level.isClientSide && placer instanceof Player player) {
                blockEntity.setOwner(player.getGameProfile().getId());
            }
            if (state.hasProperty(FACING)) {
                Direction facing = state.getValue(FACING);
                blockEntity.setOffsets(facing.getStepX(), facing.getStepY(), facing.getStepZ());
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChrononAcceleratorBlockEntity accelerator) {
                accelerator.invalidateConnectedMetronome();
            }
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof ChrononAcceleratorBlockEntity blockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (blockEntity.getOwner() == null && player != null) {
            blockEntity.setOwner(player.getGameProfile().getId());
        }

        ChrononHandler itemHandler = stack.getCapability(ChrononHandler.Capabilities.getItem());
        if (itemHandler != null) {
            if (player.isShiftKeyDown()) {
                int itemPower = itemHandler.getPower();
                if (itemPower <= 0) {
                    player.displayClientMessage(Component.translatable("message.chronon_wand.item_empty"), true);
                    return ItemInteractionResult.SUCCESS;
                }
                int simulatedInsert = blockEntity.insert(itemPower, ActionType.Simulate);
                if (simulatedInsert <= 0) {
                    player.displayClientMessage(Component.translatable("message.chronon_wand.port_full"), true);
                    return ItemInteractionResult.SUCCESS;
                }
                int extracted = itemHandler.extract(simulatedInsert, ActionType.Execute);
                blockEntity.insert(extracted, ActionType.Execute);
                player.displayClientMessage(Component.translatable("message.chronon_wand.inserted_chronons", extracted), true);
                return ItemInteractionResult.SUCCESS;
            } else {
                int spaceInItem = itemHandler.getMaxPower() - itemHandler.getPower();
                if (spaceInItem <= 0) {
                    player.displayClientMessage(Component.translatable("message.chronon_wand.item_full"), true);
                    return ItemInteractionResult.SUCCESS;
                }
                int simulatedExtract = blockEntity.extract(spaceInItem, ActionType.Simulate);
                if (simulatedExtract <= 0) {
                    player.displayClientMessage(Component.translatable("message.chronon_wand.port_empty"), true);
                    return ItemInteractionResult.SUCCESS;
                }
                int inserted = itemHandler.insert(simulatedExtract, ActionType.Execute);
                blockEntity.extract(inserted, ActionType.Execute);
                player.displayClientMessage(Component.translatable("message.chronon_wand.extracted_chronons", inserted), true);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChrononAcceleratorBlockEntity blockEntity) {
                if (blockEntity.getOwner() == null && player != null) {
                    blockEntity.setOwner(player.getGameProfile().getId());
                }
                
                ContainerData serverData = new ContainerData() {
                    @Override
                    public int get(int index) {
                        return switch (index) {
                            case 0 -> blockEntity.getPower() & 0xFFFF;
                            case 1 -> (blockEntity.getPower() >> 16) & 0xFFFF;
                            case 2 -> blockEntity.getMaxPower() & 0xFFFF;
                            case 3 -> (blockEntity.getMaxPower() >> 16) & 0xFFFF;
                            case 4 -> blockEntity.getSpeedTierIndex();
                            case 5 -> blockEntity.getRedstoneMode();
                            case 6 -> (int) Math.round(blockEntity.getXRadius() * 2);
                            case 7 -> (int) Math.round(blockEntity.getYRadius() * 2);
                            case 8 -> (int) Math.round(blockEntity.getZRadius() * 2);
                            case 9 -> blockEntity.getXOffset();
                            case 10 -> blockEntity.getYOffset();
                            case 11 -> blockEntity.getZOffset();
                            case 12 -> blockEntity.isRenderArea() ? 1 : 0;
                            case 13 -> blockEntity.getLastAcceleratedCount();
                            default -> 0;
                        };
                    }

                    @Override
                    public void set(int index, int value) {
                        switch (index) {
                            case 4 -> blockEntity.setSpeedTierIndex(value);
                            case 5 -> blockEntity.setRedstoneMode(value);
                            case 6 -> blockEntity.setXRadius(value / 2.0);
                            case 7 -> blockEntity.setYRadius(value / 2.0);
                            case 8 -> blockEntity.setZRadius(value / 2.0);
                            case 9 -> blockEntity.setXOffset(value);
                            case 10 -> blockEntity.setYOffset(value);
                            case 11 -> blockEntity.setZOffset(value);
                            case 12 -> blockEntity.setRenderArea(value != 0);
                        }
                    }

                    @Override
                    public int getCount() {
                        return 14;
                    }
                };

                player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                        (id, inv, p) -> new ChrononAcceleratorMenu(id, inv, pos, serverData),
                        Component.translatable("gui.chronon_wand.chronon_accelerator.title")
                ), buf -> {
                    buf.writeBlockPos(pos);
                    buf.writeInt(blockEntity.getPower());
                    buf.writeInt(blockEntity.getMaxPower());
                    buf.writeInt(blockEntity.getSpeedTierIndex());
                    buf.writeInt(blockEntity.getRedstoneMode());
                    buf.writeDouble(blockEntity.getXRadius());
                    buf.writeDouble(blockEntity.getYRadius());
                    buf.writeDouble(blockEntity.getZRadius());
                    buf.writeInt(blockEntity.getXOffset());
                    buf.writeInt(blockEntity.getYOffset());
                    buf.writeInt(blockEntity.getZOffset());
                    buf.writeBoolean(blockEntity.isRenderArea());
                    buf.writeInt(blockEntity.getLastAcceleratedCount());
                });
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
