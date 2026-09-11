package com.ohh.chronon_wand.compat.ae2;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.ohh.chronon_wand.ModItems;
import net.minecraft.world.item.ItemStack;

public class ChrononCellHandler implements ICellHandler {
    public static final ChrononCellHandler INSTANCE = new ChrononCellHandler();

    private ChrononCellHandler() {}

    @Override
    public boolean isCell(ItemStack is) {
        return !is.isEmpty() && is.is(ModItems.CHRONON_CELL_4K.get());
    }

    @Override
    public StorageCell getCellInventory(ItemStack is, ISaveProvider container) {
        if (!isCell(is)) {
            return null;
        }
        return new ChrononCellInventory(is, container);
    }
}

