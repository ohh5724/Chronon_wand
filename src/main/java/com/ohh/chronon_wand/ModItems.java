package com.ohh.chronon_wand;

import com.ohh.chronon_wand.item.CellTier;
import com.ohh.chronon_wand.item.ChrononStorageCellItem;
import com.ohh.chronon_wand.item.ChrononWandItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChrononWand.MOD_ID);

    public static final DeferredItem<ChrononWandItem> CHRONON_WAND = ITEMS.register("chronon_wand", ChrononWandItem::new);

    public static final DeferredItem<Item> CHRONON_CELL_HOUSING = ITEMS.register("chronon_cell_housing", () -> new Item(new Item.Properties()));

    public static final Map<CellTier, DeferredItem<ChrononStorageCellItem>> CHRONON_CELLS;
    public static final DeferredItem<ChrononStorageCellItem> CHRONON_CELL_4K;

    static {
        Map<CellTier, DeferredItem<ChrononStorageCellItem>> cells = new LinkedHashMap<>();
        boolean hasMegaCells = ModList.get().isLoaded("megacells");

        for (CellTier tier : CellTier.values()) {
            if (!tier.isMega() || hasMegaCells) {
                DeferredItem<ChrononStorageCellItem> item = ITEMS.register("chronon_cell_" + tier.getName(), () -> new ChrononStorageCellItem(tier));
                cells.put(tier, item);
            }
        }
        CHRONON_CELLS = Collections.unmodifiableMap(cells);
        CHRONON_CELL_4K = CHRONON_CELLS.get(CellTier.TIER_4K);
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
