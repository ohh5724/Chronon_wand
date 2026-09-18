package com.ohh.chronon_wand.compat.ae2;

import appeng.api.config.Actionable;
import appeng.api.ids.AEComponents;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.ohh.chronon_wand.ModDataComponents;
import com.ohh.chronon_wand.item.CellTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ChrononCellInventory implements StorageCell {
    public static final int BYTES_PER_CHRONON_RATIO = CellTier.BYTES_PER_CHRONON_RATIO;

    private final ItemStack stack;
    private final ISaveProvider saveProvider;
    private final CellTier tier;
    private final long capacity;
    private long storedChronons = 0L;
    private boolean isPersisted = true;

    public ChrononCellInventory(ItemStack stack, ISaveProvider saveProvider, CellTier tier) {
        this.stack = stack;
        this.saveProvider = saveProvider;
        this.tier = tier != null ? tier : CellTier.TIER_4K;
        this.capacity = this.tier.getCapacity();
        load();
    }

    public ChrononCellInventory(ItemStack stack, ISaveProvider saveProvider) {
        this(stack, saveProvider, CellTier.TIER_4K);
    }

    private void load() {
        boolean foundKey = false;
        List<GenericStack> list = stack.get(AEComponents.STORAGE_CELL_INV);
        if (list != null) {
            for (GenericStack gs : list) {
                if (gs != null && gs.what() instanceof ChrononKey) {
                    storedChronons = gs.amount();
                    foundKey = true;
                    break;
                }
            }
        }
        if (!foundKey) {
            Integer sc = stack.get(ModDataComponents.STORED_CHRONONS.get());
            if (sc != null && sc > 0) {
                storedChronons = sc.longValue();
                isPersisted = false;
            }
        }
        storedChronons = Math.clamp(storedChronons, 0L, capacity);
    }

    @Override
    public CellState getStatus() {
        if (storedChronons <= 0) {
            return CellState.EMPTY;
        } else if (storedChronons >= capacity) {
            return CellState.FULL;
        } else {
            return CellState.NOT_EMPTY;
        }
    }

    @Override
    public double getIdleDrain() {
        return 0.5 + 0.5 * (tier.ordinal() + 1);
    }

    @Override
    public void persist() {
        if (isPersisted) return;
        if (storedChronons <= 0) {
            stack.remove(AEComponents.STORAGE_CELL_INV);
            stack.remove(ModDataComponents.STORED_CHRONONS.get());
        } else {
            stack.set(AEComponents.STORAGE_CELL_INV, List.of(
                new GenericStack(ChrononKey.KEY, storedChronons)
            ));
            stack.set(ModDataComponents.STORED_CHRONONS.get(), (int) Math.min(Integer.MAX_VALUE, storedChronons));
        }
        isPersisted = true;
    }

    protected void saveChanges() {
        isPersisted = false;
        if (saveProvider != null) {
            saveProvider.saveChanges();
        } else {
            persist();
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return what instanceof ChrononKey;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what == null || amount <= 0 || !(what instanceof ChrononKey)) {
            return 0;
        }
        long space = capacity - storedChronons;
        if (space <= 0) return 0;
        long inserted = Math.min(space, amount);
        if (mode == Actionable.MODULATE) {
            storedChronons += inserted;
            saveChanges();
        }
        return inserted;
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what == null || amount <= 0 || !(what instanceof ChrononKey)) {
            return 0;
        }
        long extracted = Math.min(storedChronons, amount);
        if (extracted <= 0) return 0;
        if (mode == Actionable.MODULATE) {
            storedChronons -= extracted;
            saveChanges();
        }
        return extracted;
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (storedChronons > 0) {
            out.add(ChrononKey.KEY, storedChronons);
        }
    }

    @Override
    public Component getDescription() {
        return stack.getHoverName();
    }

    public long getStoredChronons() {
        return storedChronons;
    }

    public CellTier getTier() {
        return tier;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getUsedBytes() {
        return storedChronons <= 0 ? 0 : (storedChronons + BYTES_PER_CHRONON_RATIO - 1) / BYTES_PER_CHRONON_RATIO;
    }
}
