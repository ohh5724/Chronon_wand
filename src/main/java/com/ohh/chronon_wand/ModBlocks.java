package com.ohh.chronon_wand;

import com.ohh.chronon_wand.block.MetronomePortBlock;
import com.ohh.chronon_wand.fluid.ModFluids;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ChrononWand.MOD_ID);

    public static final DeferredBlock<MetronomePortBlock> METRONOME_PORT = BLOCKS.register(
            "metronome_port",
            () -> new MetronomePortBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final DeferredItem<BlockItem> METRONOME_PORT_ITEM = ModItems.ITEMS.registerSimpleBlockItem("metronome_port", METRONOME_PORT);

    public static final DeferredBlock<com.ohh.chronon_wand.block.ChrononAcceleratorBlock> CHRONON_ACCELERATOR = BLOCKS.register(
            "chronon_accelerator",
            () -> new com.ohh.chronon_wand.block.ChrononAcceleratorBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final DeferredItem<BlockItem> CHRONON_ACCELERATOR_ITEM = ModItems.ITEMS.registerSimpleBlockItem("chronon_accelerator", CHRONON_ACCELERATOR);

    public static final DeferredBlock<LiquidBlock> CHRONON_FLUID_BLOCK = BLOCKS.register(
            "chronon_fluid_block",
            () -> new LiquidBlock(
                    ModFluids.CHRONON_FLUID_SOURCE.get(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).noLootTable()
            )
    );

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
