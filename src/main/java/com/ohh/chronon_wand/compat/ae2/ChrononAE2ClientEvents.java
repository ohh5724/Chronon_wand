package com.ohh.chronon_wand.compat.ae2;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.common.RepoSlot;
import appeng.helpers.InventoryAction;
import appeng.menu.me.common.GridInventoryEntry;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ScreenEvent;

public class ChrononAE2ClientEvents {

    public static void onScreenMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getScreen() instanceof MEStorageScreen<?> storageScreen) {
            int button = event.getButton();
            if (button != 0 && button != 1) return;

            ItemStack carried = storageScreen.getMenu().getCarried();
            if (carried.isEmpty()) return;

            ChrononHandler handler = carried.getCapability(ChrononHandler.Capabilities.getItem());
            if (handler == null) return;

            Slot slot = storageScreen.getSlotUnderMouse();
            if (slot == null) {
                int left = storageScreen.getGuiLeft();
                int top = storageScreen.getGuiTop();
                for (Slot s : storageScreen.getMenu().slots) {
                    if (event.getMouseX() >= left + s.x && event.getMouseX() < left + s.x + 16
                            && event.getMouseY() >= top + s.y && event.getMouseY() < top + s.y + 16) {
                        slot = s;
                        break;
                    }
                }
            }

            if (!(slot instanceof RepoSlot repoSlot)) return;

            // 1: Right-click empties the Chronon Wand into AE2
            if (button == 1) {
                if (handler.getPower() > 0) {
                    storageScreen.getMenu().handleInteraction(-1L, InventoryAction.EMPTY_ENTIRE_ITEM);
                    event.setCanceled(true);
                }
                return;
            }

            // 2: Left-click on Chronons fills the item with the max amount of Chronons
            if (button == 0) {
                GridInventoryEntry entry = repoSlot.getEntry();
                if (entry != null && entry.getWhat() instanceof ChrononKey) {
                    storageScreen.getMenu().handleInteraction(entry.getSerial(), InventoryAction.FILL_ENTIRE_ITEM);
                    event.setCanceled(true);
                }
            }
        }
    }
}

