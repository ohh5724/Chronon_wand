package com.ohh.chronon_wand.block.entity;

import com.ohh.chronon_wand.block.ChrononAcceleratorBlock;
import com.ohh.chronon_wand.config.ChrononWandConfig;
import com.ohh.chronon_wand.fluid.ModFluids;
import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import earth.terrarium.tempad.common.block.MetronomeBe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ChrononAcceleratorBlockEntity extends BlockEntity implements IFluidHandler, ChrononHandler {
    private int speedTierIndex = 0;
    private double xRadius = 0, yRadius = 0, zRadius = 0;
    private int xOffset = 0, yOffset = 0, zOffset = 0;
    private boolean renderArea = true;
    private int redstoneMode = 0; // 0=IGNORED, 1=LOW, 2=HIGH
    
    private final FluidTank buffer = new FluidTank(10000) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.CHRONON_FLUID_SOURCE.get() ||
                   stack.getFluid() == ModFluids.CHRONON_FLUID_FLOWING.get();
        }

        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };
    
    private UUID owner;

    private double chrononAccumulator = 0;
    private int lastAcceleratedCount = 0;
    private boolean isTicking = false;

    private ChrononHandler cachedMetronome = null;
    private boolean metronomeChecked = false;
    private int metronomeCheckCooldown = 0;

    public void invalidateConnectedMetronome() {
        this.metronomeChecked = false;
        this.cachedMetronome = null;
    }

    public ChrononAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHRONON_ACCELERATOR.get(), pos, state);
        if (state.hasProperty(ChrononAcceleratorBlock.FACING)) {
            Direction facing = state.getValue(ChrononAcceleratorBlock.FACING);
            this.xOffset = facing.getStepX();
            this.yOffset = facing.getStepY();
            this.zOffset = facing.getStepZ();
        } else {
            this.yOffset = 1;
        }
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    public ChrononHandler findConnectedMetronome() {
        if (this.level == null) {
            return null;
        }

        if (this.metronomeChecked) {
            return this.cachedMetronome;
        }

        this.metronomeChecked = true;
        this.cachedMetronome = null;

        for (Direction direction : Direction.values()) {
            BlockPos adjacent = this.worldPosition.relative(direction);
            if (this.level.getBlockEntity(adjacent) instanceof MetronomePortBlockEntity) {
                continue;
            }
            if (this.level.getBlockEntity(adjacent) instanceof ChrononAcceleratorBlockEntity) {
                continue;
            }
            BlockEntity adjacentBe = this.level.getBlockEntity(adjacent);
            if (adjacentBe instanceof MetronomeBe metronomeBe) {
                if (metronomeBe.getBootTime() > 0) {
                    continue;
                }
                if (this.owner == null && metronomeBe.getOwner() != null) {
                    this.owner = metronomeBe.getOwner().getId();
                    setChanged();
                }
            }
            ChrononHandler handler = this.level.getCapability(
                    ChrononHandler.Capabilities.getBlock(),
                    adjacent,
                    direction.getOpposite()
            );
            if (handler != null) {
                if (!handler.getCanExtract()) {
                    continue;
                }
                this.cachedMetronome = handler;
                return handler;
            }
        }

        return null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("SpeedTierIndex", speedTierIndex);
        tag.putDouble("XRadius", xRadius);
        tag.putDouble("YRadius", yRadius);
        tag.putDouble("ZRadius", zRadius);
        tag.putInt("XOffset", xOffset);
        tag.putInt("YOffset", yOffset);
        tag.putInt("ZOffset", zOffset);
        tag.putBoolean("RenderArea", renderArea);
        tag.putInt("RedstoneMode", redstoneMode);
        tag.put("Buffer", buffer.writeToNBT(registries, new CompoundTag()));
        if (owner != null) {
            tag.putUUID("Owner", owner);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("SpeedTierIndex")) speedTierIndex = tag.getInt("SpeedTierIndex");
        if (tag.contains("XRadius")) xRadius = tag.getDouble("XRadius");
        if (tag.contains("YRadius")) yRadius = tag.getDouble("YRadius");
        if (tag.contains("ZRadius")) zRadius = tag.getDouble("ZRadius");
        if (tag.contains("XOffset")) xOffset = tag.getInt("XOffset");
        if (tag.contains("YOffset")) yOffset = tag.getInt("YOffset");
        if (tag.contains("ZOffset")) zOffset = tag.getInt("ZOffset");
        if (tag.contains("RenderArea")) renderArea = tag.getBoolean("RenderArea");
        if (tag.contains("RedstoneMode")) redstoneMode = tag.getInt("RedstoneMode");
        if (tag.contains("Buffer")) {
            buffer.readFromNBT(registries, tag.getCompound("Buffer"));
        }
        if (tag.hasUUID("Owner")) {
            owner = tag.getUUID("Owner");
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            loadAdditional(tag, lookupProvider);
        }
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        ChrononHandler metronome = findConnectedMetronome();
        int amount = (metronome != null && metronome.getMaxPower() > 0) ? metronome.getPower() : this.buffer.getFluidAmount();
        if (amount > 0) {
            return new FluidStack(ModFluids.CHRONON_FLUID_SOURCE.get(), amount);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        ChrononHandler metronome = findConnectedMetronome();
        return (metronome != null && metronome.getMaxPower() > 0) ? metronome.getMaxPower() : this.buffer.getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid() == ModFluids.CHRONON_FLUID_SOURCE.get() ||
               stack.getFluid() == ModFluids.CHRONON_FLUID_FLOWING.get();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource)) {
            return 0;
        }

        ChrononHandler metronome = findConnectedMetronome();
        if (metronome != null && metronome.getMaxPower() > 0) {
            int toFill = resource.getAmount();
            ActionType actionType = action.execute() ? ActionType.Execute : ActionType.Simulate;
            int inserted = metronome.insert(toFill, actionType);
            if (action.execute() && inserted > 0) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return inserted;
        } else {
            int filled = this.buffer.fill(resource, action);
            if (action.execute() && filled > 0) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return filled;
        }
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(0, resource)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0) {
            return FluidStack.EMPTY;
        }

        ChrononHandler metronome = findConnectedMetronome();
        if (metronome != null && metronome.getMaxPower() > 0) {
            ActionType actionType = action.execute() ? ActionType.Execute : ActionType.Simulate;
            int extracted = metronome.extract(maxDrain, actionType);
            if (extracted <= 0) {
                return FluidStack.EMPTY;
            }
            if (action.execute()) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return new FluidStack(ModFluids.CHRONON_FLUID_SOURCE.get(), extracted);
        } else {
            FluidStack drained = this.buffer.drain(maxDrain, action);
            if (action.execute() && !drained.isEmpty()) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return drained;
        }
    }

    @Override
    public int getPower() {
        ChrononHandler metronome = findConnectedMetronome();
        return (metronome != null && metronome.getMaxPower() > 0) ? metronome.getPower() : this.buffer.getFluidAmount();
    }

    @Override
    public int getMaxPower() {
        ChrononHandler metronome = findConnectedMetronome();
        return (metronome != null && metronome.getMaxPower() > 0) ? metronome.getMaxPower() : this.buffer.getCapacity();
    }

    @Override
    public boolean getCanInsert() {
        return true;
    }

    @Override
    public boolean getCanExtract() {
        return true;
    }

    @Override
    public int insert(int amount, ActionType action) {
        if (amount <= 0) {
            return 0;
        }
        ChrononHandler metronome = findConnectedMetronome();
        if (metronome != null && metronome.getMaxPower() > 0) {
            int inserted = metronome.insert(amount, action);
            if (action == ActionType.Execute && inserted > 0) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return inserted;
        } else {
            FluidAction fluidAction = (action == ActionType.Execute) ? FluidAction.EXECUTE : FluidAction.SIMULATE;
            FluidStack fluid = new FluidStack(ModFluids.CHRONON_FLUID_SOURCE.get(), amount);
            int filled = this.buffer.fill(fluid, fluidAction);
            if (action == ActionType.Execute && filled > 0) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return filled;
        }
    }

    @Override
    public int extract(int maxDrain, ActionType action) {
        if (maxDrain <= 0) {
            return 0;
        }
        ChrononHandler metronome = findConnectedMetronome();
        if (metronome != null && metronome.getMaxPower() > 0) {
            int extracted = metronome.extract(maxDrain, action);
            if (action == ActionType.Execute && extracted > 0) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return extracted;
        } else {
            FluidAction fluidAction = (action == ActionType.Execute) ? FluidAction.EXECUTE : FluidAction.SIMULATE;
            FluidStack drained = this.buffer.drain(maxDrain, fluidAction);
            if (action == ActionType.Execute && !drained.isEmpty()) {
                setChanged();
                if (this.level != null && !this.level.isClientSide) {
                    this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 2);
                }
            }
            return drained.getAmount();
        }
    }

    public AABB getAABB(BlockPos pos) {
        double xOff = xOffset;
        double yOff = yOffset;
        double zOff = zOffset;

        if (xRadius != Math.floor(xRadius)) {
            xOff += 0.5;
        }
        if (yRadius != Math.floor(yRadius)) {
            yOff += 0.5;
        }
        if (zRadius != Math.floor(zRadius)) {
            zOff += 0.5;
        }

        double minX = pos.getX() + xOff;
        double minY = pos.getY() + yOff;
        double minZ = pos.getZ() + zOff;
        double maxX = minX + 1.0;
        double maxY = minY + 1.0;
        double maxZ = minZ + 1.0;
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(xRadius, yRadius, zRadius);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChrononAcceleratorBlockEntity blockEntity) {
        if (blockEntity.isTicking) return;
        blockEntity.isTicking = true;
        try {
            if (++blockEntity.metronomeCheckCooldown >= 10) {
                blockEntity.metronomeCheckCooldown = 0;
                blockEntity.invalidateConnectedMetronome();
            }

            if (blockEntity.redstoneMode == 1 && level.hasNeighborSignal(pos)) return;
            if (blockEntity.redstoneMode == 2 && !level.hasNeighborSignal(pos)) return;

            int tier = Math.clamp(blockEntity.speedTierIndex, 0, ChrononWandConfig.DATA.speedTiers.size() - 1);
            int rate = ChrononWandConfig.DATA.speedTiers.get(tier);
            int costPerSec = ChrononWandConfig.DATA.costPerSecond.get(tier);

            AABB aabb = blockEntity.getAABB(pos);
            BlockPos minBlockPos = BlockPos.containing(aabb.minX + 0.001, aabb.minY + 0.001, aabb.minZ + 0.001);
            BlockPos maxBlockPos = BlockPos.containing(aabb.maxX - 0.001, aabb.maxY - 0.001, aabb.maxZ - 0.001);
            
            int blockCount = 0;
            for (BlockPos p : BlockPos.betweenClosed(minBlockPos, maxBlockPos)) {
                if (p.equals(pos)) continue;
                BlockState s = level.getBlockState(p);
                if (s.isAir()) continue;
                
                BlockEntity be = level.getBlockEntity(p);
                if (be instanceof ChrononAcceleratorBlockEntity) continue;

                if (com.ohh.chronon_wand.util.BlockTickHelper.isAcceleratable(level, p, s, be)) {
                    blockCount++;
                }
            }

            if (blockCount == 0) {
                blockEntity.lastAcceleratedCount = 0;
                blockEntity.chrononAccumulator = 0;
                return;
            }

            ChrononHandler metronome = blockEntity.findConnectedMetronome();
            int currentPower = (metronome != null && metronome.getMaxPower() > 0) ? metronome.getPower() : blockEntity.buffer.getFluidAmount();
            if (currentPower <= 0) {
                blockEntity.lastAcceleratedCount = 0;
                blockEntity.chrononAccumulator = 0;
                return;
            }

            double costPerTick = (costPerSec * blockCount) / 20.0;
            double nextAccumulator = blockEntity.chrononAccumulator + costPerTick;
            int toConsume = (int) nextAccumulator;

            if (toConsume > 0) {
                if (currentPower < toConsume) {
                    blockEntity.lastAcceleratedCount = 0;
                    blockEntity.chrononAccumulator = 0;
                    return;
                }

                if (metronome != null && metronome.getMaxPower() > 0) {
                    metronome.extract(toConsume, ActionType.Execute);
                } else {
                    blockEntity.buffer.drain(toConsume, FluidAction.EXECUTE);
                }

                blockEntity.chrononAccumulator = nextAccumulator - toConsume;
            } else {
                blockEntity.chrononAccumulator = nextAccumulator;
            }

            for (BlockPos p : BlockPos.betweenClosed(minBlockPos, maxBlockPos)) {
                if (p.equals(pos)) continue;
                BlockState s = level.getBlockState(p);
                if (s.isAir()) continue;

                BlockEntity be = level.getBlockEntity(p);
                if (be instanceof ChrononAcceleratorBlockEntity) continue;

                com.ohh.chronon_wand.util.BlockTickHelper.tick(level, p, s, be, rate);
            }
            
            blockEntity.lastAcceleratedCount = blockCount;

            if (level.getGameTime() % 20 == 0) {
                level.sendBlockUpdated(pos, state, state, 3);
            }
        } finally {
            blockEntity.isTicking = false;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    public int getSpeedTierIndex() { return speedTierIndex; }
    public void setSpeedTierIndex(int index) { this.speedTierIndex = index; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
    
    public int getRedstoneMode() { return redstoneMode; }
    public void setRedstoneMode(int mode) { this.redstoneMode = mode; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }

    public double getXRadius() { return xRadius; }
    public void setXRadius(double r) { this.xRadius = r; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
    
    public double getYRadius() { return yRadius; }
    public void setYRadius(double r) { this.yRadius = r; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
    
    public double getZRadius() { return zRadius; }
    public void setZRadius(double r) { this.zRadius = r; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }

    public int getXOffset() { return xOffset; }
    public void setXOffset(int o) { this.xOffset = o; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
    
    public int getYOffset() { return yOffset; }
    public void setYOffset(int o) { this.yOffset = o; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
    
    public int getZOffset() { return zOffset; }
    public void setZOffset(int o) { this.zOffset = o; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }

    public void setOffsets(int x, int y, int z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isRenderArea() { return renderArea; }
    public void setRenderArea(boolean b) { this.renderArea = b; setChanged(); if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
    
    public int getLastAcceleratedCount() { return lastAcceleratedCount; }
}
