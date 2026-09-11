package com.ohh.chronon_wand.compat.ae2;

import appeng.api.config.Actionable;
import appeng.api.ids.AEComponents;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import com.ohh.chronon_wand.ModDataComponents;
import com.ohh.chronon_wand.fluid.ModFluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ChrononCellInventory implements StorageCell {
    public static final long TOTAL_BYTES = 4096L;
    public static final int BYTES_PER_CHRONON_RATIO = 8; // 1 Byte = 8 Chronons
    public static final long CAPACITY = TOTAL_BYTES * BYTES_PER_CHRONON_RATIO; // 32,768 Chronons

    private final ItemStack stack;
    private final ISaveProvider saveProvider;
    private long storedChronons = 0L;
    private boolean isPersisted = true;

    public ChrononCellInventory(ItemStack stack, ISaveProvider saveProvider) {
        this.stack = stack;
        this.saveProvider = saveProvider;
        load();
    }

    private void load() {
        List<GenericStack> list = stack.get(AEComponents.STORAGE_CELL_INV);
        if (list != null) {
            for (GenericStack gs : list) {
                if (gs != null && gs.what() instanceof AEFluidKey fk && fk.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get())) {
                    storedChronons += gs.amount();
                }
            }
        } else {
            Integer sc = stack.get(ModDataComponents.STORED_CHRONONS.get());
            if (sc != null) {
                storedChronons = sc.longValue();
            }
        }
        storedChronons = Math.clamp(storedChronons, 0L, CAPACITY);
    }

    @Override
    public CellState getStatus() {
        if (storedChronons <= 0) {
            return CellState.EMPTY;
        } else if (storedChronons >= CAPACITY) {
            return CellState.FULL;
        } else {
            return CellState.NOT_EMPTY;
        }
    }

    @Override
    public double getIdleDrain() {
        return 1.0;
    }

    @Override
    public void persist() {
        if (isPersisted) return;
        if (storedChronons <= 0) {
            stack.remove(AEComponents.STORAGE_CELL_INV);
            stack.remove(ModDataComponents.STORED_CHRONONS.get());
        } else {
            stack.set(AEComponents.STORAGE_CELL_INV, List.of(new GenericStack(AEFluidKey.of(ModFluids.CHRONON_FLUID_SOURCE.get()), storedChronons)));
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
        return what instanceof AEFluidKey fk && fk.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get());
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (what == null || amount <= 0) return 0;
        if (!(what instanceof AEFluidKey fk) || !fk.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get())) {
            return 0;
        }
        long space = CAPACITY - storedChronons;
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
        if (what == null || amount <= 0) return 0;
        if (!(what instanceof AEFluidKey fk) || !fk.getFluid().isSame(ModFluids.CHRONON_FLUID_SOURCE.get())) {
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
            out.add(AEFluidKey.of(ModFluids.CHRONON_FLUID_SOURCE.get()), storedChronons);
        }
    }

    @Override
    public Component getDescription() {
        return stack.getHoverName();
    }

    public long getStoredChronons() {
        return storedChronons;
    }

    public long getUsedBytes() {
        return storedChronons <= 0 ? 0 : (storedChronons + BYTES_PER_CHRONON_RATIO - 1) / BYTES_PER_CHRONON_RATIO;
    }
}

