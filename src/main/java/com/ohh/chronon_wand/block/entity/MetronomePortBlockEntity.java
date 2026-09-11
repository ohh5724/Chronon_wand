package com.ohh.chronon_wand.block.entity;

import com.ohh.chronon_wand.fluid.ModFluids;
import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import com.ohh.chronon_wand.block.PortChannel;
import com.ohh.chronon_wand.block.SideMode;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class MetronomePortBlockEntity extends BlockEntity implements IFluidHandler, ChrononHandler {
    public static final int BUFFER_CAPACITY = 6000;

    private final Map<Direction, SideMode> energySideModes = new EnumMap<>(Direction.class);
    private final Map<Direction, SideMode> fluidSideModes = new EnumMap<>(Direction.class);

    private final FluidTank buffer = new FluidTank(BUFFER_CAPACITY) {
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

    private final IFluidHandler inputFluidWrapper = new IFluidHandler() {
        @Override public int getTanks() { return MetronomePortBlockEntity.this.getTanks(); }
        @Override public @NotNull FluidStack getFluidInTank(int tank) { return MetronomePortBlockEntity.this.getFluidInTank(tank); }
        @Override public int getTankCapacity(int tank) { return MetronomePortBlockEntity.this.getTankCapacity(tank); }
        @Override public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return MetronomePortBlockEntity.this.isFluidValid(tank, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) { return MetronomePortBlockEntity.this.fill(resource, action); }
        @Override public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return FluidStack.EMPTY; }
        @Override public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return FluidStack.EMPTY; }
    };

    private final IFluidHandler outputFluidWrapper = new IFluidHandler() {
        @Override public int getTanks() { return MetronomePortBlockEntity.this.getTanks(); }
        @Override public @NotNull FluidStack getFluidInTank(int tank) { return MetronomePortBlockEntity.this.getFluidInTank(tank); }
        @Override public int getTankCapacity(int tank) { return MetronomePortBlockEntity.this.getTankCapacity(tank); }
        @Override public boolean isFluidValid(int tank, @NotNull FluidStack stack) { return MetronomePortBlockEntity.this.isFluidValid(tank, stack); }
        @Override public int fill(FluidStack resource, FluidAction action) { return 0; }
        @Override public @NotNull FluidStack drain(FluidStack resource, FluidAction action) { return MetronomePortBlockEntity.this.drain(resource, action); }
        @Override public @NotNull FluidStack drain(int maxDrain, FluidAction action) { return MetronomePortBlockEntity.this.drain(maxDrain, action); }
    };

    private final ChrononHandler inputEnergyWrapper = new ChrononHandler() {
        @Override public int getPower() { return MetronomePortBlockEntity.this.getPower(); }
        @Override public int getMaxPower() { return MetronomePortBlockEntity.this.getMaxPower(); }
        @Override public boolean getCanInsert() { return true; }
        @Override public boolean getCanExtract() { return false; }
        @Override public int extract(int amount, ActionType action) { return 0; }
        @Override public int insert(int amount, ActionType action) { return MetronomePortBlockEntity.this.insert(amount, action); }
    };

    private final ChrononHandler outputEnergyWrapper = new ChrononHandler() {
        @Override public int getPower() { return MetronomePortBlockEntity.this.getPower(); }
        @Override public int getMaxPower() { return MetronomePortBlockEntity.this.getMaxPower(); }
        @Override public boolean getCanInsert() { return false; }
        @Override public boolean getCanExtract() { return true; }
        @Override public int extract(int amount, ActionType action) { return MetronomePortBlockEntity.this.extract(amount, action); }
        @Override public int insert(int amount, ActionType action) { return 0; }
    };

    public MetronomePortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.METRONOME_PORT.get(), pos, state);
        for (Direction direction : Direction.values()) {
            energySideModes.put(direction, SideMode.BOTH);
            fluidSideModes.put(direction, SideMode.BOTH);
        }
    }

    public FluidTank getBuffer() {
        return this.buffer;
    }

    public SideMode getSideMode(PortChannel channel, Direction direction) {
        return channel == PortChannel.CHRONON_ENERGY
                ? energySideModes.getOrDefault(direction, SideMode.BOTH)
                : fluidSideModes.getOrDefault(direction, SideMode.BOTH);
    }

    public void setSideMode(PortChannel channel, Direction direction, SideMode mode) {
        if (channel == PortChannel.CHRONON_ENERGY) {
            energySideModes.put(direction, mode);
        } else {
            fluidSideModes.put(direction, mode);
        }
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), 3);
            this.level.invalidateCapabilities(this.worldPosition);
        }
    }

    public BlockState getAdjacentBlock(Direction direction) {
        if (this.level != null) {
            return this.level.getBlockState(this.worldPosition.relative(direction));
        }
        return Blocks.AIR.defaultBlockState();
    }

    public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (side == null) {
            return this;
        }
        SideMode mode = fluidSideModes.getOrDefault(side, SideMode.BOTH);
        return switch (mode) {
            case BOTH -> this;
            case INPUT -> this.inputFluidWrapper;
            case OUTPUT -> this.outputFluidWrapper;
            case NONE -> null;
        };
    }

    public @Nullable ChrononHandler getChrononHandler(@Nullable Direction side) {
        if (side == null) {
            return this;
        }
        SideMode mode = energySideModes.getOrDefault(side, SideMode.BOTH);
        return switch (mode) {
            case BOTH -> this;
            case INPUT -> this.inputEnergyWrapper;
            case OUTPUT -> this.outputEnergyWrapper;
            case NONE -> null;
        };
    }

    public ChrononHandler findConnectedMetronome() {
        if (this.level == null) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            BlockPos adjacent = this.worldPosition.relative(direction);
            if (this.level.getBlockEntity(adjacent) instanceof MetronomePortBlockEntity) {
                continue;
            }
            ChrononHandler handler = this.level.getCapability(
                    ChrononHandler.Capabilities.getBlock(),
                    adjacent,
                    direction.getOpposite()
            );
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Buffer", this.buffer.writeToNBT(registries, new CompoundTag()));

        CompoundTag energyTag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            energyTag.putString(dir.getName(), energySideModes.getOrDefault(dir, SideMode.BOTH).name());
        }
        tag.put("EnergySideModes", energyTag);

        CompoundTag fluidTag = new CompoundTag();
        for (Direction dir : Direction.values()) {
            fluidTag.putString(dir.getName(), fluidSideModes.getOrDefault(dir, SideMode.BOTH).name());
        }
        tag.put("FluidSideModes", fluidTag);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Buffer")) {
            this.buffer.readFromNBT(registries, tag.getCompound("Buffer"));
        }

        if (tag.contains("EnergySideModes")) {
            CompoundTag energyTag = tag.getCompound("EnergySideModes");
            for (Direction dir : Direction.values()) {
                if (energyTag.contains(dir.getName())) {
                    try {
                        energySideModes.put(dir, SideMode.valueOf(energyTag.getString(dir.getName())));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }

        if (tag.contains("FluidSideModes")) {
            CompoundTag fluidTag = tag.getCompound("FluidSideModes");
            for (Direction dir : Direction.values()) {
                if (fluidTag.contains(dir.getName())) {
                    try {
                        fluidSideModes.put(dir, SideMode.valueOf(fluidTag.getString(dir.getName())));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        ChrononHandler metronome = findConnectedMetronome();
        int bufferAmount = this.buffer.getFluidAmount();
        int metronomeAmount = metronome != null ? metronome.getPower() : 0;
        int totalDisplay = Math.min(BUFFER_CAPACITY, bufferAmount + metronomeAmount);

        if (totalDisplay > 0) {
            return new FluidStack(ModFluids.CHRONON_FLUID_SOURCE.get(), totalDisplay);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return BUFFER_CAPACITY;
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

        int toFill = resource.getAmount();
        ChrononHandler metronome = findConnectedMetronome();

        // If connected to a Metronome, insert into the metronome first
        int insertedIntoMetronome = 0;
        if (metronome != null) {
            ActionType actionType = action.execute() ? ActionType.Execute : ActionType.Simulate;
            insertedIntoMetronome = metronome.insert(toFill, actionType);
        }

        int remaining = toFill - insertedIntoMetronome;
        int filledIntoBuffer = 0;
        if (remaining > 0) {
            FluidStack remainingStack = resource.copyWithAmount(remaining);
            filledIntoBuffer = this.buffer.fill(remainingStack, action);
        }

        if (action.execute()) {
            setChanged();
        }

        return insertedIntoMetronome + filledIntoBuffer;
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

        int drainedFromBuffer = 0;
        // First try to drain from the internal buffer
        if (!this.buffer.isEmpty()) {
            FluidStack drainedStack = this.buffer.drain(maxDrain, action);
            drainedFromBuffer = drainedStack.getAmount();
        }

        int remainingNeeded = maxDrain - drainedFromBuffer;
        int extractedFromMetronome = 0;

        // If more is needed, extract chronons from connected metronome (1 Chronon = 1 mB)
        if (remainingNeeded > 0) {
            ChrononHandler metronome = findConnectedMetronome();
            if (metronome != null) {
                ActionType actionType = action.execute() ? ActionType.Execute : ActionType.Simulate;
                extractedFromMetronome = metronome.extract(remainingNeeded, actionType);
            }
        }

        int totalDrained = drainedFromBuffer + extractedFromMetronome;
        if (totalDrained > 0) {
            if (action.execute()) {
                setChanged();
            }
            return new FluidStack(ModFluids.CHRONON_FLUID_SOURCE.get(), totalDrained);
        }

        return FluidStack.EMPTY;
    }

    // ChrononHandler implementation

    @Override
    public int getPower() {
        ChrononHandler metronome = findConnectedMetronome();
        int bufferAmount = this.buffer.getFluidAmount();
        int metronomeAmount = metronome != null ? metronome.getPower() : 0;
        return bufferAmount + metronomeAmount;
    }

    @Override
    public int getMaxPower() {
        ChrononHandler metronome = findConnectedMetronome();
        int metronomeMax = metronome != null ? metronome.getMaxPower() : 0;
        return BUFFER_CAPACITY + metronomeMax;
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

        int insertedIntoMetronome = 0;
        if (metronome != null) {
            insertedIntoMetronome = metronome.insert(amount, action);
        }

        int remaining = amount - insertedIntoMetronome;
        int filledIntoBuffer = 0;
        if (remaining > 0) {
            FluidStack fluid = new FluidStack(ModFluids.CHRONON_FLUID_SOURCE.get(), remaining);
            FluidAction fluidAction = (action == ActionType.Execute) ? FluidAction.EXECUTE : FluidAction.SIMULATE;
            filledIntoBuffer = this.buffer.fill(fluid, fluidAction);
        }

        if (action == ActionType.Execute && (insertedIntoMetronome > 0 || filledIntoBuffer > 0)) {
            setChanged();
        }

        return insertedIntoMetronome + filledIntoBuffer;
    }

    @Override
    public int extract(int maxDrain, ActionType action) {
        if (maxDrain <= 0) {
            return 0;
        }

        int drainedFromBuffer = 0;
        if (!this.buffer.isEmpty()) {
            FluidAction fluidAction = (action == ActionType.Execute) ? FluidAction.EXECUTE : FluidAction.SIMULATE;
            FluidStack drained = this.buffer.drain(maxDrain, fluidAction);
            drainedFromBuffer = drained.getAmount();
        }

        int remainingNeeded = maxDrain - drainedFromBuffer;
        int extractedFromMetronome = 0;
        if (remainingNeeded > 0) {
            ChrononHandler metronome = findConnectedMetronome();
            if (metronome != null) {
                extractedFromMetronome = metronome.extract(remainingNeeded, action);
            }
        }

        int total = drainedFromBuffer + extractedFromMetronome;
        if (action == ActionType.Execute && total > 0) {
            setChanged();
        }

        return total;
    }
}

