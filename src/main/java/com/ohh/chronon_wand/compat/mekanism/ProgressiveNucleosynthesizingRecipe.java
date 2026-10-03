package com.ohh.chronon_wand.compat.mekanism;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ohh.chronon_wand.ModDataComponents;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.NucleosynthesizingRecipe;
import mekanism.api.recipes.basic.IBasicItemStackOutput;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Collections;
import java.util.List;

public class ProgressiveNucleosynthesizingRecipe extends NucleosynthesizingRecipe implements IBasicItemStackOutput {
    private final ItemStackIngredient itemInput;
    private final ChemicalStackIngredient chemicalInput;
    private final ItemStack intermediateOutput;
    private final ItemStack targetOutput;
    private final int duration;
    private final int targetCount;

    public ProgressiveNucleosynthesizingRecipe(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack intermediateOutput, ItemStack targetOutput, int duration, int targetCount) {
        this.itemInput = itemInput;
        this.chemicalInput = chemicalInput;
        this.intermediateOutput = intermediateOutput;
        this.targetOutput = targetOutput;
        this.duration = duration;
        this.targetCount = targetCount;
    }

    public ItemStackIngredient getItemInput() {
        return itemInput;
    }

    public ChemicalStackIngredient getChemicalInput() {
        return chemicalInput;
    }

    public ItemStack getIntermediateOutput() {
        return intermediateOutput;
    }

    public ItemStack getTargetOutput() {
        return targetOutput;
    }

    public int getTargetCount() {
        return targetCount;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public boolean perTickUsage() {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return targetOutput.copy();
    }

    @Override
    public ItemStack getOutputRaw() {
        return intermediateOutput;
    }

    @Override
    public List<ItemStack> getOutputDefinition() {
        return Collections.singletonList(targetOutput);
    }

    @Override
    public boolean test(ItemStack itemStack, ChemicalStack chemicalStack) {
        return itemInput.test(itemStack) && chemicalInput.test(chemicalStack);
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
        return MekanismCompat.PROGRESSIVE_NUCLEOSYNTHESIZING_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<ProgressiveNucleosynthesizingRecipe> {
        public static final MapCodec<ProgressiveNucleosynthesizingRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                ItemStackIngredient.CODEC.fieldOf("item_input").forGetter(ProgressiveNucleosynthesizingRecipe::getItemInput),
                ChemicalStackIngredient.CODEC.fieldOf("chemical_input").forGetter(ProgressiveNucleosynthesizingRecipe::getChemicalInput),
                ItemStack.CODEC.fieldOf("intermediate_output").forGetter(ProgressiveNucleosynthesizingRecipe::getIntermediateOutput),
                ItemStack.CODEC.fieldOf("target_output").forGetter(ProgressiveNucleosynthesizingRecipe::getTargetOutput),
                Codec.INT.optionalFieldOf("duration", 20).forGetter(ProgressiveNucleosynthesizingRecipe::getDuration),
                Codec.INT.optionalFieldOf("target_count", 1001).forGetter(ProgressiveNucleosynthesizingRecipe::getTargetCount)
        ).apply(builder, ProgressiveNucleosynthesizingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProgressiveNucleosynthesizingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    ItemStackIngredient.STREAM_CODEC.encode(buf, recipe.getItemInput());
                    ChemicalStackIngredient.STREAM_CODEC.encode(buf, recipe.getChemicalInput());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.getIntermediateOutput());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.getTargetOutput());
                    ByteBufCodecs.VAR_INT.encode(buf, recipe.getDuration());
                    ByteBufCodecs.VAR_INT.encode(buf, recipe.getTargetCount());
                },
                buf -> new ProgressiveNucleosynthesizingRecipe(
                        ItemStackIngredient.STREAM_CODEC.decode(buf),
                        ChemicalStackIngredient.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.VAR_INT.decode(buf),
                        ByteBufCodecs.VAR_INT.decode(buf)
                )
        );

        @Override
        public MapCodec<ProgressiveNucleosynthesizingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ProgressiveNucleosynthesizingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
