package com.ohh.chronon_wand.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ChrononWandServerConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_PROGRESSION_RECIPES = BUILDER
            .comment("Enable or disable the complex Chronon Crystal crafting recipes included with the mod.",
                     "WARNING: Changing this setting requires a game or server restart to take effect.")
            .define("enableProgressionRecipes", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean isProgressionRecipesEnabled() {
        try {
            return ENABLE_PROGRESSION_RECIPES.get();
        } catch (Throwable t) {
            return true;
        }
    }
}
