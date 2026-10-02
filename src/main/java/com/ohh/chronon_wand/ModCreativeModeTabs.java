package com.ohh.chronon_wand;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ChrononWand.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CHRONON_WAND_TAB =
            CREATIVE_MODE_TABS.register("chronon_wand_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.chronon_wand"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.CHRONON_WAND.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.CHRONON_WAND.get());
                        output.accept(ModBlocks.METRONOME_PORT_ITEM.get());
                        output.accept(ModBlocks.CHRONON_ACCELERATOR_ITEM.get());
                        output.accept(ModItems.CHRONON_CELL_HOUSING.get());
                        for (var cellHolder : ModItems.CHRONON_CELLS.values()) {
                            output.accept(cellHolder.get());
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
