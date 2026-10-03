package com.ohh.chronon_wand.util;

import com.ohh.chronon_wand.compat.AE2TickHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTickHelper {

    public static final TagKey<Block> CHRONON_DENY = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("chronon_wand", "acceleration_deny"));
    public static final TagKey<Block> JDT_DENY = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("justdirethings", "tick_speed_deny"));
    public static final TagKey<Block> TIAB_DENY = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("tiab", "un_acceleratable"));

    public static boolean isAcceleratable(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (state.isAir()) {
            return false;
        }

        // Check blacklist tags
        if (state.is(CHRONON_DENY) || state.is(JDT_DENY) || state.is(TIAB_DENY)) {
            return false;
        }

        // Check if random tickable (crops, saplings, etc.)
        if (state.isRandomlyTicking()) {
            return true;
        }

        if (be != null && !be.isRemoved()) {
            // Check standard vanilla/NeoForge BlockEntityTicker
            BlockEntityTicker<?> ticker = state.getTicker(level, be.getType());
            if (ticker != null) {
                return true;
            }

            // Check AE2 machine ticking
            if (AE2TickHelper.canTick(be)) {
                return true;
            }
        }

        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be, int rate) {
        if (state.isAir()) {
            return;
        }

        for (int i = 0; i < rate - 1; i++) {
            if (state.isRandomlyTicking()) {
                int bound = Math.max(1, 4096 / (rate + 1));
                if (level.random.nextInt(bound) == 0) {
                    if (level instanceof ServerLevel sl) {
                        state.randomTick(sl, pos.immutable(), level.random);
                        state = level.getBlockState(pos);
                        if (state.isAir()) break;
                    }
                }
            }

            if (be != null && !be.isRemoved()) {
                // Standard BlockEntity ticker
                @SuppressWarnings("unchecked")
                BlockEntityTicker<BlockEntity> ticker = (BlockEntityTicker<BlockEntity>) state.getTicker(level, be.getType());
                if (ticker != null) {
                    ticker.tick(level, pos.immutable(), state, be);
                }

                // AE2 machine ticking
                AE2TickHelper.tick(level, pos, state, be);
            }
        }
    }
}
