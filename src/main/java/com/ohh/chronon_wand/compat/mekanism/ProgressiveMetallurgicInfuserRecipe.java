package com.ohh.chronon_wand.compat.mekanism;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ohh.chronon_wand.ModDataComponents;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipeTypes;
import mekanism.api.recipes.basic.BasicItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ProgressiveMetallurgicInfuserRecipe extends BasicItemStackChemicalToItemStackRecipe {
    private final int targetCount;
    private final ItemStack targetOutput;

    public ProgressiveMetallurgicInfuserRecipe(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack intermediateOutput, ItemStack targetOutput, int targetCount) {
        super(itemInput, chemicalInput, intermediateOutput, false, MekanismRecipeTypes.TYPE_METALLURGIC_INFUSING.get());
        this.targetCount = targetCount;
        this.targetOutput = targetOutput;
    }

    public int getTargetCount() {
        return targetCount;
    }

    public ItemStack getTargetOutput() {
        return targetOutput;
    }

    @Override
    public ItemStack getOutput(ItemStack inputItem, ChemicalStack inputChemical) {
        int count = inputItem.getOrDefault(ModDataComponents.PROCESS_COUNT.get(), 0);
        if (count + 1 >= targetCount) {
            return targetOutput.copy();
        } else {
            ItemStack result = inputItem.copyWithCount(1);
            result.set(ModDataComponents.PROCESS_COUNT.get(), count + 1);
            return result;
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MekanismCompat.PROGRESSIVE_INFUSION_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<ProgressiveMetallurgicInfuserRecipe> {
        public static final MapCodec<ProgressiveMetallurgicInfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                ItemStackIngredient.CODEC.fieldOf("item_input").forGetter(BasicItemStackChemicalToItemStackRecipe::getItemInput),
                ChemicalStackIngredient.CODEC.fieldOf("chemical_input").forGetter(BasicItemStackChemicalToItemStackRecipe::getChemicalInput),
                ItemStack.CODEC.fieldOf("intermediate_output").forGetter(BasicItemStackChemicalToItemStackRecipe::getOutputRaw),
                ItemStack.CODEC.fieldOf("target_output").forGetter(ProgressiveMetallurgicInfuserRecipe::getTargetOutput),
                Codec.INT.optionalFieldOf("target_count", 69420).forGetter(ProgressiveMetallurgicInfuserRecipe::getTargetCount)
        ).apply(builder, ProgressiveMetallurgicInfuserRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProgressiveMetallurgicInfuserRecipe> STREAM_CODEC = StreamCodec.composite(
                ItemStackIngredient.STREAM_CODEC, BasicItemStackChemicalToItemStackRecipe::getItemInput,
                ChemicalStackIngredient.STREAM_CODEC, BasicItemStackChemicalToItemStackRecipe::getChemicalInput,
                ItemStack.STREAM_CODEC, BasicItemStackChemicalToItemStackRecipe::getOutputRaw,
                ItemStack.STREAM_CODEC, ProgressiveMetallurgicInfuserRecipe::getTargetOutput,
                ByteBufCodecs.VAR_INT, ProgressiveMetallurgicInfuserRecipe::getTargetCount,
                ProgressiveMetallurgicInfuserRecipe::new
        );

        @Override
        public MapCodec<ProgressiveMetallurgicInfuserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ProgressiveMetallurgicInfuserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
