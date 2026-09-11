package com.ohh.chronon_wand;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChrononWand.MOD_ID);

    public static final DeferredItem<com.ohh.chronon_wand.item.ChrononWandItem> CHRONON_WAND = ITEMS.register("chronon_wand", com.ohh.chronon_wand.item.ChrononWandItem::new);
    public static final DeferredItem<com.ohh.chronon_wand.item.ChrononStorageCellItem> CHRONON_CELL_4K = ITEMS.register("chronon_cell_4k", () -> new com.ohh.chronon_wand.item.ChrononStorageCellItem());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

