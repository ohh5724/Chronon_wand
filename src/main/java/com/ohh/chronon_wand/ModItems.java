package com.ohh.chronon_wand;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChrononWand.MOD_ID);

    public static final DeferredItem<com.ohh.chronon_wand.item.ChrononWandItem> CHRONON_WAND = ITEMS.register("chronon_wand", com.ohh.chronon_wand.item.ChrononWandItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

