package com.ohh.chronon_wand.compat.ae2;

import appeng.api.AECapabilities;
import appeng.api.client.AEKeyRendering;
import appeng.api.client.StorageCellModels;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import com.ohh.chronon_wand.ModDataComponents;
import com.ohh.chronon_wand.ModItems;
import com.ohh.chronon_wand.block.entity.ModBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;

public class AE2Compat {
    public static final boolean IS_LOADED = ModList.get().isLoaded("ae2");

    private static boolean registeredKeyType = false;

    public static synchronized void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (!IS_LOADED || registeredKeyType) return;
        if (event.getRegistryKey().equals(appeng.api.stacks.AEKeyType.REGISTRY_KEY) || event.getRegistryKey().equals(net.minecraft.core.registries.Registries.BLOCK)) {
            registeredKeyType = true;
            AEKeyTypes.register(ChrononKeyType.TYPE);
        }
    }

    public static void init() {
        if (IS_LOADED) {
            StorageCells.addCellHandler(ChrononCellHandler.INSTANCE);
            appeng.api.behaviors.GenericSlotCapacities.register(ChrononKeyType.TYPE, 1000000L);
            appeng.api.behaviors.ContainerItemStrategy.register(ChrononKeyType.TYPE, ChrononKey.class, new ChrononContainerItemStrategy());
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(AE2Compat::onContainerOpen);
        }
    }

    private static void onContainerOpen(net.neoforged.neoforge.event.entity.player.PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof appeng.menu.interfaces.KeyTypeSelectionMenu menu) {
            try {
                appeng.api.util.KeyTypeSelection sel = menu.getServerKeyTypeSelection();
                if (sel != null && !sel.isEnabled(ChrononKeyType.TYPE)) {
                    boolean shouldEnable = false;
                    if (menu instanceof appeng.menu.me.common.MEStorageMenu storageMenu) {
                        appeng.api.storage.ITerminalHost host = storageMenu.getHost();
                        if (host instanceof appeng.parts.AEBasePart part) {
                            net.minecraft.world.level.block.entity.BlockEntity be = part.getBlockEntity();
                            if (be != null) {
                                String flag = "chronon_wand_init_" + (part.getSide() != null ? part.getSide().getName() : "part");
                                if (!be.getPersistentData().getBoolean(flag)) {
                                    be.getPersistentData().putBoolean(flag, true);
                                    be.setChanged();
                                    shouldEnable = true;
                                }
                            }
                        } else if (host instanceof net.minecraft.world.level.block.entity.BlockEntity be) {
                            if (!be.getPersistentData().getBoolean("chronon_wand_init")) {
                                be.getPersistentData().putBoolean("chronon_wand_init", true);
                                be.setChanged();
                                shouldEnable = true;
                            }
                        } else {
                            shouldEnable = true;
                        }
                    } else {
                        shouldEnable = true;
                    }

                    if (shouldEnable) {
                        sel.setEnabled(ChrononKeyType.TYPE, true);
                    }
                }
            } catch (Throwable ignored) {}
        }
    }

    public static void initClient() {
        if (IS_LOADED) {
            StorageCellModels.registerModel(ModItems.CHRONON_CELL_4K.get(), ResourceLocation.parse("ae2:block/drive/cells/4k_item_cell"));
            AEKeyRendering.register(ChrononKeyType.TYPE, ChrononKey.class, ChrononKeyRenderHandler.INSTANCE);
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(ChrononAE2ClientEvents::onScreenMouseClicked);
        }
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if (IS_LOADED) {
            event.registerBlockEntity(
                AECapabilities.ME_STORAGE,
                ModBlockEntities.METRONOME_PORT.get(),
                (be, side) -> be.getMEStorage(side)
            );
        }
    }

    public static long getStoredChronons(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0L;
        if (IS_LOADED) {
            List<GenericStack> list = stack.get(AEComponents.STORAGE_CELL_INV);
            if (list != null) {
                for (GenericStack gs : list) {
                    if (gs != null) {
                        if (gs.what() instanceof ChrononKey) {
                            return gs.amount();
                        }
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
