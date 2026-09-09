package com.ohh.chronon_wand.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class AcceleratorEntity extends Entity {
    private static final EntityDataAccessor<Integer> TIME_RATE = SynchedEntityData.defineId(AcceleratorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> REMAINING_TICKS = SynchedEntityData.defineId(AcceleratorEntity.class, EntityDataSerializers.INT);

    public AcceleratorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true;
    }

    public AcceleratorEntity(Level level, BlockPos pos, int timeRate, int durationTicks) {
        this(ModEntities.ACCELERATOR.get(), level);
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        this.setTimeRate(timeRate);
        this.setRemainingTicks(durationTicks);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(TIME_RATE, 1);
        builder.define(REMAINING_TICKS, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        setTimeRate(compound.getInt("TimeRate"));
        setRemainingTicks(compound.getInt("RemainingTicks"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("TimeRate", getTimeRate());
        compound.putInt("RemainingTicks", getRemainingTicks());
    }

    public int getTimeRate() {
        return this.entityData.get(TIME_RATE);
    }

    public void setTimeRate(int rate) {
        this.entityData.set(TIME_RATE, rate);
    }

    public int getRemainingTicks() {
        return this.entityData.get(REMAINING_TICKS);
    }

    public void setRemainingTicks(int ticks) {
        this.entityData.set(REMAINING_TICKS, ticks);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            // Optional: Spawn particles here
            return;
        }

        int remaining = getRemainingTicks();
        if (remaining <= 0) {
            this.discard();
            return;
        }
        setRemainingTicks(remaining - 1);

        int rate = getTimeRate();
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);

        for (int i = 0; i < rate - 1; i++) {
            if (state.isRandomlyTicking()) {
                if (this.level().random.nextInt(4096 / (rate + 1)) == 0) { // Rough approximation to match standard random ticks
                    state.randomTick((ServerLevel) this.level(), pos, this.level().random);
                }
            }

            if (state.getBlock() instanceof EntityBlock) {
                BlockEntity be = this.level().getBlockEntity(pos);
                if (be != null) {
                    @SuppressWarnings("unchecked")
                    BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) state.getTicker(this.level(), be.getType());
                    if (ticker != null) {
                        ticker.tick(this.level(), pos, state, be);
                    }
                }
            }
        }
    }
}

