package com.ohh.chronon_wand.recipe.condition;

import com.mojang.serialization.MapCodec;
import com.ohh.chronon_wand.config.ChrononWandServerConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public class RecipesEnabledCondition implements ICondition {
    public static final MapCodec<RecipesEnabledCondition> CODEC = MapCodec.unit(new RecipesEnabledCondition());

    @Override
    public boolean test(IContext context) {
        return ChrononWandServerConfig.isProgressionRecipesEnabled();
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
