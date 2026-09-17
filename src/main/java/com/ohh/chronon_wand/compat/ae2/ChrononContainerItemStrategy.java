package com.ohh.chronon_wand.compat.ae2;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import earth.terrarium.tempad.api.ActionType;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class ChrononContainerItemStrategy implements ContainerItemStrategy<ChrononKey, ChrononContainerItemStrategy.ItemContext> {

    public interface ItemContext {
        ItemStack getStack();
        void setStack(ItemStack stack);
        void addOverflow(ItemStack stack);
    }

    public record CarriedContext(Player player, AbstractContainerMenu menu) implements ItemContext {
        @Override
        public ItemStack getStack() {
            return menu.getCarried();
        }

        @Override
        public void setStack(ItemStack stack) {
            menu.setCarried(stack);
        }

        @Override
        public void addOverflow(ItemStack stack) {
            if (menu.getCarried().isEmpty()) {
                menu.setCarried(stack);
            } else {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }

    public record PlayerInvContext(Player player, int slot) implements ItemContext {
        @Override
        public ItemStack getStack() {
            return player.getInventory().getItem(slot);
        }

        @Override
        public void setStack(ItemStack stack) {
            player.getInventory().setItem(slot, stack);
        }

        @Override
        public void addOverflow(ItemStack stack) {
            if (player.getInventory().getItem(slot).isEmpty()) {
                player.getInventory().setItem(slot, stack);
            } else {
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }

    @Override
    public GenericStack getContainedStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        ChrononHandler handler = stack.getCapability(ChrononHandler.Capabilities.getItem());
        if (handler != null && handler.getPower() > 0) {
            return new GenericStack(ChrononKey.KEY, handler.getPower());
        }
        return null;
    }

    @Override
    public ItemContext findCarriedContext(Player player, AbstractContainerMenu menu) {
        ItemStack carried = menu.getCarried();
        if (!carried.isEmpty() && carried.getCapability(ChrononHandler.Capabilities.getItem()) != null) {
            return new CarriedContext(player, menu);
        }
        return null;
    }

    @Override
    public ItemContext findPlayerSlotContext(Player player, int slot) {
        ItemStack stack = player.getInventory().getItem(slot);
        if (!stack.isEmpty() && stack.getCapability(ChrononHandler.Capabilities.getItem()) != null) {
            return new PlayerInvContext(player, slot);
        }
        return null;
    }

    @Override
    public long extract(ItemContext context, ChrononKey key, long maxAmount, Actionable mode) {
        if (context == null || maxAmount <= 0) return 0;
        ItemStack original = context.getStack();
        if (original.isEmpty()) return 0;

        ItemStack copy = original.copyWithCount(1);
        ChrononHandler handler = copy.getCapability(ChrononHandler.Capabilities.getItem());
        if (handler == null) return 0;

        int toExtract = (int) Math.min(Integer.MAX_VALUE, maxAmount);
        ActionType actionType = (mode == Actionable.MODULATE) ? ActionType.Execute : ActionType.Simulate;
        long extracted = handler.extract(toExtract, actionType);
        if (extracted > 0 && mode == Actionable.MODULATE) {
            original.shrink(1);
            context.addOverflow(copy);
        }
        return extracted;
    }

    @Override
    public long insert(ItemContext context, ChrononKey key, long maxAmount, Actionable mode) {
        if (context == null || maxAmount <= 0) return 0;
        ItemStack original = context.getStack();
        if (original.isEmpty()) return 0;

        ItemStack copy = original.copyWithCount(1);
        ChrononHandler handler = copy.getCapability(ChrononHandler.Capabilities.getItem());
        if (handler == null) return 0;

        int toInsert = (int) Math.min(Integer.MAX_VALUE, maxAmount);
        ActionType actionType = (mode == Actionable.MODULATE) ? ActionType.Execute : ActionType.Simulate;
        long inserted = handler.insert(toInsert, actionType);
        if (inserted > 0 && mode == Actionable.MODULATE) {
            original.shrink(1);
            context.addOverflow(copy);
        }
        return inserted;
    }

    @Override
    public void playFillSound(Player player, ChrononKey key) {
    }

    @Override
    public void playEmptySound(Player player, ChrononKey key) {
    }

    @Override
    public GenericStack getExtractableContent(ItemContext context) {
        if (context == null) return null;
        return getContainedStack(context.getStack());
    }
}

