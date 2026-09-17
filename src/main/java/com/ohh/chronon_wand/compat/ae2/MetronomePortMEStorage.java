package com.ohh.chronon_wand.compat.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import com.ohh.chronon_wand.block.SideMode;
import com.ohh.chronon_wand.block.entity.MetronomePortBlockEntity;
import com.ohh.chronon_wand.fluid.ModFluids;
import earth.terrarium.tempad.api.ActionType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class MetronomePortMEStorage implements MEStorage {
    private final MetronomePortBlockEntity port;
    private final Direction side;

    public MetronomePortMEStorage(MetronomePortBlockEntity port, Direction side) {
        this.port = port;
        this.side = side;
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        if (what instanceof ChrononKey) {
            SideMode mode = port.getEffectiveEnergyMode(side);
            return mode == SideMode.BOTH || mode == SideMode.INPUT;
        }
        if (what instanceof AEFluidKey fluidKey && fluidKey.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get())) {
            SideMode mode = port.getEffectiveFluidMode(side);
            return mode == SideMode.BOTH || mode == SideMode.INPUT;
        }
        return false;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what == null || amount <= 0) return 0;

        if (what instanceof ChrononKey) {
            SideMode modeSetting = port.getEffectiveEnergyMode(side);
            if (modeSetting != SideMode.BOTH && modeSetting != SideMode.INPUT) {
                return 0;
            }
            int toInsert = (int) Math.min(amount, Integer.MAX_VALUE);
            ActionType action = (mode == Actionable.MODULATE) ? ActionType.Execute : ActionType.Simulate;
            return port.insert(toInsert, action);
        }

        if (what instanceof AEFluidKey fluidKey && fluidKey.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get())) {
            SideMode modeSetting = port.getEffectiveFluidMode(side);
            if (modeSetting != SideMode.BOTH && modeSetting != SideMode.INPUT) {
                return 0;
            }
            int toInsert = (int) Math.min(amount, Integer.MAX_VALUE);
            IFluidHandler.FluidAction action = (mode == Actionable.MODULATE) ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE;
            return port.fill(new FluidStack(ModFluids.CHRONON_FLUID_SOURCE.get(), toInsert), action);
        }

        return 0;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what == null || amount <= 0) return 0;

        if (what instanceof ChrononKey) {
            SideMode modeSetting = port.getEffectiveEnergyMode(side);
            if (modeSetting != SideMode.BOTH && modeSetting != SideMode.OUTPUT) {
                return 0;
            }
            int toExtract = (int) Math.min(amount, Integer.MAX_VALUE);
            ActionType action = (mode == Actionable.MODULATE) ? ActionType.Execute : ActionType.Simulate;
            return port.extract(toExtract, action);
        }

        if (what instanceof AEFluidKey fluidKey && fluidKey.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get())) {
            SideMode modeSetting = port.getEffectiveFluidMode(side);
            if (modeSetting != SideMode.BOTH && modeSetting != SideMode.OUTPUT) {
                return 0;
            }
            int toExtract = (int) Math.min(amount, Integer.MAX_VALUE);
            IFluidHandler.FluidAction action = (mode == Actionable.MODULATE) ? IFluidHandler.FluidAction.EXECUTE : IFluidHandler.FluidAction.SIMULATE;
            FluidStack drained = port.drain(toExtract, action);
            return drained.getAmount();
        }

        return 0;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        SideMode energyMode = port.getEffectiveEnergyMode(side);
        SideMode fluidMode = port.getEffectiveFluidMode(side);

        int chronons = port.getPower();
        if (chronons > 0) {
            if (energyMode != SideMode.NONE) {
                out.add(ChrononKey.KEY, chronons);
            }
            if (fluidMode != SideMode.NONE) {
                out.add(AEFluidKey.of(ModFluids.CHRONON_FLUID_SOURCE.get()), chronons);
            }
        }
    }

    @Override
    public Component getDescription() {
        return port.getBlockState().getBlock().getName();
    }
}
