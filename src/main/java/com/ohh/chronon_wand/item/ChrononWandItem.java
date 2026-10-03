package com.ohh.chronon_wand.item;

import com.ohh.chronon_wand.ModDataComponents;
import com.ohh.chronon_wand.config.ChrononWandConfig;
import com.ohh.chronon_wand.entity.AcceleratorEntity;
import earth.terrarium.tempad.client.tooltip.ChrononData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class ChrononWandItem extends Item {

    public ChrononWandItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockState targetState = level.getBlockState(pos);
        if (targetState.is(com.ohh.chronon_wand.ModBlocks.METRONOME_PORT.get()) ||
            targetState.is(com.ohh.chronon_wand.ModBlocks.CHRONON_ACCELERATOR.get())) {
            return InteractionResult.PASS;
        }

        if (!com.ohh.chronon_wand.util.BlockTickHelper.isAcceleratable(level, pos, targetState, level.getBlockEntity(pos))) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        int currentChronons = stack.getOrDefault(ModDataComponents.STORED_CHRONONS, 0);

        List<Integer> speeds = ChrononWandConfig.DATA.speedTiers;
        List<Integer> costs = ChrononWandConfig.DATA.costPerSecond;

        // Try to find an existing accelerator on this block
        AABB searchBox = new AABB(pos);
        List<AcceleratorEntity> existing = level.getEntitiesOfClass(AcceleratorEntity.class, searchBox);

        AcceleratorEntity accelerator = null;
        if (!existing.isEmpty()) {
            accelerator = existing.get(0);
        }

        int currentTierIndex = -1;
        if (accelerator != null) {
            int rate = accelerator.getTimeRate();
            currentTierIndex = speeds.indexOf(rate);
            if (currentTierIndex == -1) {
                for (int i = 0; i < speeds.size(); i++) {
                    if (speeds.get(i) >= rate) {
                        currentTierIndex = i;
                        break;
                    }
                }
            }
            if (currentTierIndex >= speeds.size() - 1) {
                player.displayClientMessage(Component.translatable("message.chronon_wand.max_speed"), true);
                return InteractionResult.FAIL;
            }
        }

        int nextTierIndex = currentTierIndex + 1;
        if (nextTierIndex >= speeds.size()) {
            player.displayClientMessage(Component.translatable("message.chronon_wand.max_speed"), true);
            return InteractionResult.FAIL;
        }

        int nextRate = speeds.get(nextTierIndex);
        int costPerSecond = costs.get(nextTierIndex);
        int durationSeconds = ChrononWandConfig.DATA.maxDurationTicks / 20;

        int totalCost = costPerSecond * durationSeconds;

        if (currentChronons < totalCost) {
            player.displayClientMessage(Component.translatable("message.chronon_wand.not_enough_chronons"), true);
            return InteractionResult.FAIL;
        }

        // Consume chronons
        stack.set(ModDataComponents.STORED_CHRONONS, currentChronons - totalCost);

        if (accelerator == null) {
            accelerator = new AcceleratorEntity(level, pos, nextRate, ChrononWandConfig.DATA.maxDurationTicks);
            level.addFreshEntity(accelerator);
        } else {
            accelerator.setTimeRate(nextRate);
            accelerator.setRemainingTicks(ChrononWandConfig.DATA.maxDurationTicks);
        }

        player.displayClientMessage(Component.translatable("message.chronon_wand.accelerated", nextRate), true);

        return InteractionResult.SUCCESS;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        int current = stack.getOrDefault(ModDataComponents.STORED_CHRONONS, 0);
        int max = ChrononWandConfig.DATA.maxChronons;
        return Optional.of(new ChrononData(current, max));
    }
}

