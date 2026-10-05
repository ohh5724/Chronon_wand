package com.ohh.chronon_wand.mixin;

import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.WirelessTerminalItem;
import appeng.me.helpers.ActionHostEnergySource;
import appeng.util.ConfigInventory;
import com.ohh.chronon_wand.compat.ae2importexportcard.ChrononImportExportCompat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Mixin for AE2 Import Export Card versions >= 1.9.0 (14-parameter exportToPlayerSlot).
 */
@Mixin(value = WirelessTerminalItem.class, priority = 1100)
public abstract class WirelessTerminalItemModernMixin {

    @Dynamic
    @Inject(
            method = "ae2ImportExportCard$importFromPlayerSlot",
            at = @At("HEAD"),
            remap = false,
            require = 0
    )
    private void chronon_wand$importChrononsFromPlayerSlot(
            ServerPlayer player,
            IGrid grid,
            ActionHostEnergySource energySource,
            IActionSource actionSource,
            int slotIndex,
            ItemStack stackInSlot,
            ConfigInventory filterConfig,
            FuzzyMode fuzzyMode,
            boolean isFuzzy,
            boolean isInverted,
            CallbackInfo ci
    ) {
        ChrononImportExportCompat.importChrononsFromItem(
                player, grid, energySource, actionSource, slotIndex, stackInSlot, filterConfig, fuzzyMode, isFuzzy, isInverted
        );
    }

    @Dynamic
    @Inject(
            method = "ae2ImportExportCard$exportToPlayerSlot",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void chronon_wand$exportChrononsToPlayerSlot(
            ServerPlayer player,
            Level level,
            IGrid grid,
            ActionHostEnergySource energySource,
            IActionSource actionSource,
            int slotIndex,
            ItemStack stackInSlot,
            int configSlotIndex,
            ConfigInventory filterConfig,
            IUpgradeInventory upgrades,
            FuzzyMode fuzzyMode,
            IItemHandler playerItemHandler,
            Consumer<ItemStack> slotUpdateConsumer,
            boolean canExtract,
            CallbackInfo ci
    ) {
        int configSlot = configSlotIndex - 1;
        if (configSlot < 0 || configSlot >= filterConfig.size()) {
            return;
        }

        GenericStack filterStack = filterConfig.getStack(configSlot);
        if (filterStack == null) {
            return;
        }

        AEKey key = filterStack.what();
        if (ChrononImportExportCompat.isChrononKey(key)) {
            long rate = upgrades.isInstalled(AEItems.SPEED_CARD) ? 64000L : 1000L;
            if (!ChrononImportExportCompat.canAcceptChronons(stackInSlot, key, rate)) {
                ci.cancel();
                return;
            }

            boolean exported = ChrononImportExportCompat.exportChrononsToItem(
                    player, grid, energySource, actionSource, slotIndex, stackInSlot, key, rate
            );

            if (!exported) {
                try {
                    java.lang.reflect.Method m = this.getClass().getDeclaredMethod(
                            "ae2ImportExportCard$requestCraftingIfPossible",
                            Level.class, IGrid.class, AEKey.class, int.class, IUpgradeInventory.class
                    );
                    m.setAccessible(true);
                    m.invoke(this, level, grid, key, (int) rate, upgrades);
                } catch (Throwable ignored) {
                }
            }

            ci.cancel();
        }
    }
}
