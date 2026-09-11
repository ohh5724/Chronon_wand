package com.ohh.chronon_wand.compat.ae2;

import appeng.api.client.StorageCellModels;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import com.ohh.chronon_wand.ModDataComponents;
import com.ohh.chronon_wand.ModItems;
import com.ohh.chronon_wand.fluid.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.List;

public class AE2Compat {
    public static final boolean IS_LOADED = ModList.get().isLoaded("ae2");

    public static void init() {
        if (IS_LOADED) {
            StorageCells.addCellHandler(ChrononCellHandler.INSTANCE);
        }
    }

    public static void initClient() {
        if (IS_LOADED) {
            StorageCellModels.registerModel(ModItems.CHRONON_CELL_4K.get(), ResourceLocation.parse("ae2:block/drive/cells/4k_fluid_cell"));
        }
    }

    public static long getStoredChronons(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        if (IS_LOADED) {
            List<GenericStack> list = stack.get(AEComponents.STORAGE_CELL_INV);
            if (list != null) {
                for (GenericStack gs : list) {
                    if (gs != null && gs.what() instanceof AEFluidKey fk && fk.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get())) {
                        return gs.amount();
                    }
                }
            }
        }
        Integer sc = stack.get(ModDataComponents.STORED_CHRONONS.get());
        return sc != null ? sc.longValue() : 0L;
    }

    public static int getCellLedColor(ItemStack stack) {
        if (!IS_LOADED) return 0x00FF00;
        StorageCell inv = StorageCells.getCellInventory(stack, null);
        CellState state = inv != null ? inv.getStatus() : CellState.EMPTY;
        return state.getStateColor();
    }
}

