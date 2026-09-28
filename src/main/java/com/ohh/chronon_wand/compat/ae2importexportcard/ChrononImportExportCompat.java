package com.ohh.chronon_wand.compat.ae2importexportcard;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.me.helpers.ActionHostEnergySource;
import appeng.util.ConfigInventory;
import com.ohh.chronon_wand.compat.ae2.ChrononKey;
import com.ultramega.ae2importexportcard.util.AEKeyFilterUtil;
import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class ChrononImportExportCompat {
    private ChrononImportExportCompat() {}

    public static boolean isChrononKey(AEKey key) {
        return key instanceof ChrononKey || key == ChrononKey.KEY;
    }

    public static void importChrononsFromItem(
            ServerPlayer player,
            IGrid grid,
            ActionHostEnergySource energySource,
            IActionSource actionSource,
            int slotIndex,
            ItemStack stackInSlot,
            ConfigInventory filterConfig,
            FuzzyMode fuzzyMode,
            boolean isFuzzy,
            boolean isInverted
    ) {
        if (stackInSlot == null || stackInSlot.isEmpty()) {
            return;
        }

        ChrononHandler handler = stackInSlot.getCapability(ChrononHandler.Capabilities.getItem());
        if (handler == null || !handler.getCanExtract()) {
            return;
        }

        if (!AEKeyFilterUtil.passesFilter(ChrononKey.KEY, filterConfig, fuzzyMode, isFuzzy, isInverted)) {
            return;
        }

        int currentPower = handler.getPower();
        if (currentPower <= 0) {
            return;
        }

        int simExtracted = handler.extract(currentPower, ActionType.Simulate);
        if (simExtracted <= 0) {
            return;
        }

        long inserted = StorageHelper.poweredInsert(
                energySource,
                grid.getStorageService().getInventory(),
                ChrononKey.KEY,
                simExtracted,
                actionSource,
                Actionable.MODULATE
        );
        if (inserted <= 0) {
            return;
        }

        int actuallyExtracted = handler.extract((int) inserted, ActionType.Execute);
        if (actuallyExtracted < inserted) {
            StorageHelper.poweredExtraction(
                    energySource,
                    grid.getStorageService().getInventory(),
                    ChrononKey.KEY,
                    inserted - actuallyExtracted,
                    actionSource,
                    Actionable.MODULATE
            );
        }

        player.getInventory().setItem(slotIndex, stackInSlot);
        player.containerMenu.broadcastChanges();
    }

    public static boolean canAcceptChronons(ItemStack stack, AEKey key, long amount) {
        if (!isChrononKey(key)) {
            return false;
        }
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        ChrononHandler handler = stack.getCapability(ChrononHandler.Capabilities.getItem());
        if (handler == null || !handler.getCanInsert()) {
            return false;
        }
        int toInsert = (int) Math.min(amount, Integer.MAX_VALUE);
        return handler.insert(toInsert, ActionType.Simulate) > 0;
    }

    public static boolean exportChrononsToItem(
            ServerPlayer player,
            IGrid grid,
            ActionHostEnergySource energySource,
            IActionSource actionSource,
            int slotIndex,
            ItemStack stackInSlot,
            AEKey key,
            long rate
    ) {
        if (!isChrononKey(key)) {
            return false;
        }
        if (stackInSlot == null || stackInSlot.isEmpty()) {
            return false;
        }

        ChrononHandler handler = stackInSlot.getCapability(ChrononHandler.Capabilities.getItem());
        if (handler == null || !handler.getCanInsert()) {
            return false;
        }

        int maxInsertable = handler.insert((int) Math.min(rate, Integer.MAX_VALUE), ActionType.Simulate);
        if (maxInsertable <= 0) {
            return false;
        }

        long simExtracted = StorageHelper.poweredExtraction(
                energySource,
                grid.getStorageService().getInventory(),
                ChrononKey.KEY,
                maxInsertable,
                actionSource,
                Actionable.SIMULATE
        );
        if (simExtracted <= 0) {
            return false;
        }

        long toExtract = Math.min(simExtracted, maxInsertable);
        long extracted = StorageHelper.poweredExtraction(
                energySource,
                grid.getStorageService().getInventory(),
                ChrononKey.KEY,
                toExtract,
                actionSource,
                Actionable.MODULATE
        );
        if (extracted <= 0) {
            return false;
        }

        int actuallyInserted = handler.insert((int) extracted, ActionType.Execute);
        if (actuallyInserted < extracted) {
            StorageHelper.poweredInsert(
                    energySource,
                    grid.getStorageService().getInventory(),
                    ChrononKey.KEY,
                    extracted - actuallyInserted,
                    actionSource,
                    Actionable.MODULATE
            );
        }

        if (actuallyInserted <= 0) {
            return false;
        }

        player.getInventory().setItem(slotIndex, stackInSlot);
        player.containerMenu.broadcastChanges();
        return true;
    }
}

