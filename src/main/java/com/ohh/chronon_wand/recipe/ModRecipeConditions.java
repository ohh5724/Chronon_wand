package com.ohh.chronon_wand.recipe;

import com.mojang.serialization.MapCodec;
import com.ohh.chronon_wand.ChrononWand;
import com.ohh.chronon_wand.recipe.condition.RecipesEnabledCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModRecipeConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.CONDITION_SERIALIZERS, ChrononWand.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<RecipesEnabledCondition>> RECIPES_ENABLED =
            CONDITIONS.register("recipes_enabled", () -> RecipesEnabledCondition.CODEC);

    public static void register(IEventBus eventBus) {
        CONDITIONS.register(eventBus);
    }
}
