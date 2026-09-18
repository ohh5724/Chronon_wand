package com.ohh.chronon_wand.compat.ae2;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.ohh.chronon_wand.item.ChrononStorageCellItem;
import net.minecraft.world.item.ItemStack;

public class ChrononCellHandler implements ICellHandler {
    public static final ChrononCellHandler INSTANCE = new ChrononCellHandler();

    private ChrononCellHandler() {}

    @Override
    public boolean isCell(ItemStack is) {
        return !is.isEmpty() && is.getItem() instanceof ChrononStorageCellItem;
    }

    @Override
    public StorageCell getCellInventory(ItemStack is, ISaveProvider container) {
        if (!is.isEmpty() && is.getItem() instanceof ChrononStorageCellItem cellItem) {
            return new ChrononCellInventory(is, container, cellItem.getTier());
        }
        return null;
    }
}
