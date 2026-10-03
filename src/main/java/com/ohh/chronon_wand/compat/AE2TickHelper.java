package com.ohh.chronon_wand.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

public class AE2TickHelper {
    private static Boolean ae2Loaded = null;

    public static boolean isAE2Loaded() {
        if (ae2Loaded == null) {
            ae2Loaded = ModList.get().isLoaded("ae2");
        }
        return ae2Loaded;
    }

    public static boolean canTick(BlockEntity be) {
        if (!isAE2Loaded() || be == null || be.isRemoved()) {
            return false;
        }
        try {
            return com.ohh.chronon_wand.compat.ae2.AE2TickHandler.canTick(be);
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (!isAE2Loaded() || be == null || be.isRemoved() || !(level instanceof ServerLevel)) {
            return false;
        }
        try {
            return com.ohh.chronon_wand.compat.ae2.AE2TickHandler.tick(level, pos, state, be);
        } catch (Throwable t) {
            return false;
        }
    }
}
