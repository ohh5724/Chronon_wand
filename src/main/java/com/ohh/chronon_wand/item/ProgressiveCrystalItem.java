package com.ohh.chronon_wand.item;

import com.ohh.chronon_wand.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ProgressiveCrystalItem extends Item {
    private final int targetCount;
    private final String processKey;
    private final int barColor;

    public ProgressiveCrystalItem(Properties properties, int targetCount, String processKey, int barColor) {
        super(properties);
        this.targetCount = targetCount;
        this.processKey = processKey;
        this.barColor = barColor;
    }

    public int getTargetCount() {
        return targetCount;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        int count = stack.getOrDefault(ModDataComponents.PROCESS_COUNT.get(), 0);
        return count > 0 && count < targetCount;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int count = stack.getOrDefault(ModDataComponents.PROCESS_COUNT.get(), 0);
        return Math.round(13.0F * (float) count / (float) targetCount);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return barColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int count = stack.getOrDefault(ModDataComponents.PROCESS_COUNT.get(), 0);
        double percent = (count * 100.0) / (double) targetCount;
        tooltipComponents.add(Component.translatable(processKey, 
                Component.literal(String.format("%,d", count)).withStyle(ChatFormatting.AQUA),
                Component.literal(String.format("%,d", targetCount)).withStyle(ChatFormatting.YELLOW),
                Component.literal(String.format("%.2f%%", percent)).withStyle(ChatFormatting.GREEN)
        ).withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
