package com.ohh.chronon_wand.fluid;

import com.ohh.chronon_wand.ChrononWand;
import com.ohh.chronon_wand.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, ChrononWand.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ChrononWand.MOD_ID);

    public static final Supplier<FluidType> CHRONON_FLUID_TYPE = FLUID_TYPES.register("chronon_fluid", () -> new FluidType(
            FluidType.Properties.create()
                    .density(1500)
                    .viscosity(1500)
                    .lightLevel(12)
                    .descriptionId("fluid_type.chronon_wand.chronon_fluid")
    ));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CHRONON_FLUID_SOURCE = FLUIDS.register("chronon_fluid", () ->
            new BaseFlowingFluid.Source(ModFluids.CHRONON_FLUID_PROPERTIES)
    );

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CHRONON_FLUID_FLOWING = FLUIDS.register("flowing_chronon_fluid", () ->
            new BaseFlowingFluid.Flowing(ModFluids.CHRONON_FLUID_PROPERTIES)
    );

    public static final BaseFlowingFluid.Properties CHRONON_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            CHRONON_FLUID_TYPE,
            CHRONON_FLUID_SOURCE,
            CHRONON_FLUID_FLOWING
    ).block(() -> ModBlocks.CHRONON_FLUID_BLOCK.get());

    public static final Supplier<FluidType> ANTITIME_FLUID_TYPE = FLUID_TYPES.register("antitime_fluid", () -> new FluidType(
            FluidType.Properties.create()
                    .density(2000)
                    .viscosity(2000)
                    .temperature(-727)
                    .lightLevel(5)
                    .descriptionId("fluid_type.chronon_wand.antitime_fluid")
    ));

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> ANTITIME_FLUID_SOURCE = FLUIDS.register("antitime_fluid", () ->
            new BaseFlowingFluid.Source(ModFluids.ANTITIME_FLUID_PROPERTIES)
    );

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> ANTITIME_FLUID_FLOWING = FLUIDS.register("flowing_antitime_fluid", () ->
            new BaseFlowingFluid.Flowing(ModFluids.ANTITIME_FLUID_PROPERTIES)
    );

    public static final BaseFlowingFluid.Properties ANTITIME_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(
            ANTITIME_FLUID_TYPE,
            ANTITIME_FLUID_SOURCE,
            ANTITIME_FLUID_FLOWING
    ).block(() -> ModBlocks.ANTITIME_FLUID_BLOCK.get()).bucket(() -> com.ohh.chronon_wand.ModItems.ANTITIME_FLUID_BUCKET.get());

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}

