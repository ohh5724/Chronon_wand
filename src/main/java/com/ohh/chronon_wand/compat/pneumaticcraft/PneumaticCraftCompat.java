package com.ohh.chronon_wand.compat.pneumaticcraft;

import com.ohh.chronon_wand.ChrononWand;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PneumaticCraftCompat {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ChrononWand.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ProgressiveThermoPlantRecipe>> PROGRESSIVE_THERMO_SERIALIZER =
            RECIPE_SERIALIZERS.register("progressive_thermo_plant", ProgressiveThermoPlantRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }
}
