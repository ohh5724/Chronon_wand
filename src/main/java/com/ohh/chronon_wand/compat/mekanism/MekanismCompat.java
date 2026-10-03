package com.ohh.chronon_wand.compat.mekanism;

import com.ohh.chronon_wand.ChrononWand;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MekanismCompat {
    public static final DeferredRegister<Chemical> CHEMICALS =
            DeferredRegister.create(MekanismAPI.CHEMICAL_REGISTRY_NAME, ChrononWand.MOD_ID);

    public static final DeferredHolder<Chemical, Chemical> CHRONON =
            CHEMICALS.register("chronon", () -> new Chemical(ChemicalBuilder.builder().tint(0x00E5FF)));

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ChrononWand.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ProgressiveMetallurgicInfuserRecipe>> PROGRESSIVE_INFUSION_SERIALIZER =
            RECIPE_SERIALIZERS.register("progressive_metallurgic_infusing", ProgressiveMetallurgicInfuserRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ProgressiveNucleosynthesizingRecipe>> PROGRESSIVE_NUCLEOSYNTHESIZING_SERIALIZER =
            RECIPE_SERIALIZERS.register("progressive_nucleosynthesizing", ProgressiveNucleosynthesizingRecipe.Serializer::new);

    public static void register(IEventBus bus) {
        CHEMICALS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
    }
}
