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

public class MetronomePortBlockEntity extends BlockEntity implements IFluidHandler {
    public static final int BUFFER_CAPACITY = 6000;

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

    public MetronomePortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.METRONOME_PORT.get(), pos, state);
    }

    public ChrononHandler findConnectedMetronome() {
        if (this.level == null) {
            return null;
        }
        for (Direction direction : Direction.values()) {
            BlockPos adjacent = this.worldPosition.relative(direction);
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
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Buffer")) {
            this.buffer.readFromNBT(registries, tag.getCompound("Buffer"));
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
}

