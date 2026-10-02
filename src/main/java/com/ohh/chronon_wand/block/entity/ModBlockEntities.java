package com.ohh.chronon_wand.block.entity;

import com.ohh.chronon_wand.ChrononWand;
import com.ohh.chronon_wand.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ChrononWand.MOD_ID);

    public static final Supplier<BlockEntityType<MetronomePortBlockEntity>> METRONOME_PORT =
            BLOCK_ENTITIES.register("metronome_port", () ->
                    BlockEntityType.Builder.of(MetronomePortBlockEntity::new, ModBlocks.METRONOME_PORT.get()).build(null)
            );

    public static final Supplier<BlockEntityType<ChrononAcceleratorBlockEntity>> CHRONON_ACCELERATOR = 
            BLOCK_ENTITIES.register("chronon_accelerator", () -> 
                    BlockEntityType.Builder.of(ChrononAcceleratorBlockEntity::new, ModBlocks.CHRONON_ACCELERATOR.get()).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

