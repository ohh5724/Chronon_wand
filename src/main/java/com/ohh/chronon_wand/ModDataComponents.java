package com.ohh.chronon_wand;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, ChrononWand.MOD_ID);

    public static final Supplier<DataComponentType<Integer>> STORED_CHRONONS = DATA_COMPONENTS.register("stored_chronons", () ->
            DataComponentType.<Integer>builder().persistent(com.mojang.serialization.Codec.INT).networkSynchronized(net.minecraft.network.codec.ByteBufCodecs.VAR_INT).build()
    );

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
