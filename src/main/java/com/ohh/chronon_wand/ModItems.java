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

    public static final DeferredItem<com.ohh.chronon_wand.item.ProgressiveCrystalItem> UNFINISHED_CHRONON_CRYSTAL = ITEMS.register(
            "unfinished_chronon_crystal",
            () -> new com.ohh.chronon_wand.item.ProgressiveCrystalItem(new Item.Properties().stacksTo(1), 69420, "tooltip.chronon_wand.chronon_infusion", 0x00E5FF)
    );

    public static final DeferredItem<com.ohh.chronon_wand.item.ProgressiveCrystalItem> CHRONON_INFUSED_CHRONON_CRYSTAL = ITEMS.register(
            "chronon_infused_chronon_crystal",
            () -> new com.ohh.chronon_wand.item.ProgressiveCrystalItem(new Item.Properties().stacksTo(1), 1001, "tooltip.chronon_wand.nucleosynthesis", 0xD500F9)
    );

    public static final DeferredItem<com.ohh.chronon_wand.item.ProgressiveCrystalItem> CHRONON_INFUSED_CHRONON_ANTICRYSTAL = ITEMS.register(
            "chronon_infused_chronon_anticrystal",
            () -> new com.ohh.chronon_wand.item.ProgressiveCrystalItem(new Item.Properties().stacksTo(1), 70, "tooltip.chronon_wand.antitime_warping", 0x2979FF)
    );

    public static final DeferredItem<Item> ANTITIME_WARPED_CHRONON_INFUSED_CHRONON_ANTICRYSTAL = ITEMS.register(
            "antitime_warped_chronon_infused_chronon_anticrystal",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static final DeferredItem<Item> ANTITIME_WARPED_CHRONON_INFUSED_UNOBTAINIUM_REINFORCED_CHRONON_ANTICRYSTAL = ITEMS.register(
            "antitime_warped_chronon_infused_unobtainium_reinforced_chronon_anticrystal",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static final DeferredItem<Item> ORANGE_ANTITIME_WARPED_CHRONON_INFUSED_UNOBTAINIUM_REINFORCED_CHRONON_ANTICRYSTAL = ITEMS.register(
            "orange_antitime_warped_chronon_infused_unobtainium_reinforced_chronon_anticrystal",
            () -> new Item(new Item.Properties().stacksTo(16))
    );

    public static final DeferredItem<net.minecraft.world.item.BucketItem> ANTITIME_FLUID_BUCKET = ITEMS.register(
            "antitime_fluid_bucket",
            () -> new net.minecraft.world.item.BucketItem(
                    com.ohh.chronon_wand.fluid.ModFluids.ANTITIME_FLUID_SOURCE.get(),
                    new Item.Properties().craftRemainder(net.minecraft.world.item.Items.BUCKET).stacksTo(1)
            )
    );

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
