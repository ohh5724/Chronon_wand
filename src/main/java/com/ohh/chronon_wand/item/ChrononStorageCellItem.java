package com.ohh.chronon_wand.item;

import com.ohh.chronon_wand.ModItems;
import com.ohh.chronon_wand.compat.ae2.AE2Compat;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class ChrononStorageCellItem extends Item {
    private final CellTier tier;

    public ChrononStorageCellItem(CellTier tier) {
        super(new Item.Properties().stacksTo(1));
        this.tier = tier != null ? tier : CellTier.TIER_4K;
    }

    public ChrononStorageCellItem() {
        this(CellTier.TIER_4K);
    }

    public CellTier getTier() {
        return tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (player.isCrouching() && AE2Compat.getStoredChronons(held) == 0) {
            if (!level.isClientSide) {
                disassemble(level, player, held);
            }
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isCrouching() && AE2Compat.getStoredChronons(context.getItemInHand()) == 0) {
            Level level = context.getLevel();
            if (!level.isClientSide) {
                disassemble(level, player, context.getItemInHand());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    private void disassemble(Level level, Player player, ItemStack cellStack) {
        cellStack.shrink(1);
        Item housing = ModItems.CHRONON_CELL_HOUSING.get();
        if (housing == null || housing == Items.AIR) {
            housing = BuiltInRegistries.ITEM.get(ResourceLocation.parse("ae2:item_cell_housing"));
        }
        Item component = BuiltInRegistries.ITEM.get(ResourceLocation.parse(tier.getComponentId()));

        if (housing != null && housing != Items.AIR) {
            player.getInventory().placeItemBackInInventory(new ItemStack(housing));
        }
        if (component != null && component != Items.AIR) {
            player.getInventory().placeItemBackInInventory(new ItemStack(component));
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.PLAYERS, 1.0f, 1.0f);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);

        long stored = AE2Compat.getStoredChronons(stack);
        long totalBytes = tier.getTotalBytes();
        long capacity = tier.getCapacity();
        long usedBytes = stored <= 0 ? 0 : (stored + CellTier.BYTES_PER_CHRONON_RATIO - 1) / CellTier.BYTES_PER_CHRONON_RATIO;

        tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.bytes",
                String.format("%,d", usedBytes), String.format("%,d", totalBytes)).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.chronons",
                String.format("%,d", stored), String.format("%,d", capacity)).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.scale").withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.drain").withStyle(ChatFormatting.DARK_GRAY));

        if (stored <= 0) {
            tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.status.empty").withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.disassemble").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        } else if (stored >= capacity) {
            tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.status.full").withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.translatable("tooltip.chronon_wand.chronon_cell.status.not_empty").withStyle(ChatFormatting.AQUA));
        }
    }
}
