package com.ohh.chronon_wand.item;

import com.ohh.chronon_wand.ModDataComponents;
import com.ohh.chronon_wand.config.ChrononWandConfig;
import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.minecraft.world.item.ItemStack;

public class ItemChrononHandler implements ChrononHandler {
    private final ItemStack stack;

    public ItemChrononHandler(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getPower() {
        return stack.getOrDefault(ModDataComponents.STORED_CHRONONS, 0);
    }

    @Override
    public int getMaxPower() {
        return ChrononWandConfig.DATA.maxChronons;
    }

    @Override
    public boolean getCanInsert() {
        return true;
    }

    @Override
    public boolean getCanExtract() {
        return true;
    }

    @Override
    public int extract(int amount, ActionType actionType) {
        if (!getCanExtract()) return 0;
        int current = getPower();
        int extracted = Math.min(amount, current);
        if (actionType == ActionType.Execute && extracted > 0) {
            stack.set(ModDataComponents.STORED_CHRONONS, current - extracted);
        }
        return extracted;
    }

    @Override
    public int insert(int amount, ActionType actionType) {
        if (!getCanInsert()) return 0;
        int current = getPower();
        int max = getMaxPower();
        int inserted = Math.min(amount, max - current);
        if (actionType == ActionType.Execute && inserted > 0) {
            stack.set(ModDataComponents.STORED_CHRONONS, current + inserted);
        }
        return inserted;
    }
}

