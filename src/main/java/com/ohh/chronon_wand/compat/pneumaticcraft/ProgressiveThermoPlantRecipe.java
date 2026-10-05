package com.ohh.chronon_wand.compat.pneumaticcraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ohh.chronon_wand.ModDataComponents;
import me.desht.pneumaticcraft.api.crafting.TemperatureRange;
import me.desht.pneumaticcraft.common.recipes.machine.ThermoPlantRecipeImpl;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class ProgressiveThermoPlantRecipe extends ThermoPlantRecipeImpl {
    private final int targetCount;
    private final ItemStack targetOutput;
    private final ItemStack intermediateOutput;
    private ItemStack lastTestedItem = ItemStack.EMPTY;

    public ProgressiveThermoPlantRecipe(Inputs inputs, Outputs outputs, TemperatureRange operatingTemperature, float requiredPressure, float recipeSpeed, float airUseMultiplier, boolean isExothermic, int targetCount, ItemStack targetOutput) {
        super(inputs, outputs, operatingTemperature, requiredPressure, recipeSpeed, airUseMultiplier, isExothermic);
        this.targetCount = targetCount;
        this.targetOutput = targetOutput;
        this.intermediateOutput = outputs.outputItem();
    }

    public int getTargetCount() {
        return targetCount;
    }

    public ItemStack getTargetOutput() {
        return targetOutput;
    }

    @Override
    public boolean matches(FluidStack fluid, ItemStack item) {
        boolean match = super.matches(fluid, item);
        if (match) {
            this.lastTestedItem = item.copy();
        } else {
            this.lastTestedItem = ItemStack.EMPTY;
        }
        return match;
    }

    @Override
    public ItemStack getOutputItem() {
        if (!lastTestedItem.isEmpty()) {
            int count = lastTestedItem.getOrDefault(ModDataComponents.PROCESS_COUNT.get(), 0);
            if (count + 1 >= targetCount) {
                return targetOutput.copy();
            } else {
                ItemStack result = lastTestedItem.copyWithCount(1);
                result.set(ModDataComponents.PROCESS_COUNT.get(), count + 1);
                return result;
            }
        }
        return targetOutput.copy();
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registries) {
        return targetOutput.copy();
    }

    @Override
    public ItemStack assemble(net.minecraft.world.item.crafting.CraftingInput input, net.minecraft.core.HolderLookup.Provider registries) {
        return targetOutput.copy();
    }

    @Override
    public String getGroup() {
        return "pneumaticcraft:thermo_plant";
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PneumaticCraftCompat.PROGRESSIVE_THERMO_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<ProgressiveThermoPlantRecipe> {
        public static final MapCodec<ProgressiveThermoPlantRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                Inputs.CODEC.fieldOf("inputs").forGetter(ThermoPlantRecipeImpl::inputs),
                Outputs.CODEC.fieldOf("outputs").forGetter(ThermoPlantRecipeImpl::outputs),
                TemperatureRange.CODEC.optionalFieldOf("temperature", TemperatureRange.any()).forGetter(ThermoPlantRecipeImpl::getOperatingTemperature),
                Codec.FLOAT.optionalFieldOf("pressure", 0.0f).forGetter(ThermoPlantRecipeImpl::getRequiredPressure),
                Codec.FLOAT.optionalFieldOf("speed", 1.0f).forGetter(ThermoPlantRecipeImpl::getRecipeSpeed),
                Codec.FLOAT.optionalFieldOf("air_use_multiplier", 1.0f).forGetter(ThermoPlantRecipeImpl::getAirUseMultiplier),
                Codec.BOOL.optionalFieldOf("exothermic", false).forGetter(ThermoPlantRecipeImpl::isExothermic),
                Codec.INT.optionalFieldOf("target_count", 70).forGetter(ProgressiveThermoPlantRecipe::getTargetCount),
                ItemStack.CODEC.fieldOf("target_output").forGetter(ProgressiveThermoPlantRecipe::getTargetOutput)
        ).apply(builder, ProgressiveThermoPlantRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProgressiveThermoPlantRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    Inputs.STREAM_CODEC.encode(buf, recipe.inputs());
                    Outputs.STREAM_CODEC.encode(buf, recipe.outputs());
                    TemperatureRange.STREAM_CODEC.encode(buf, recipe.getOperatingTemperature());
                    ByteBufCodecs.FLOAT.encode(buf, recipe.getRequiredPressure());
                    ByteBufCodecs.FLOAT.encode(buf, recipe.getRecipeSpeed());
                    ByteBufCodecs.FLOAT.encode(buf, recipe.getAirUseMultiplier());
                    ByteBufCodecs.BOOL.encode(buf, recipe.isExothermic());
                    ByteBufCodecs.VAR_INT.encode(buf, recipe.getTargetCount());
                    ItemStack.STREAM_CODEC.encode(buf, recipe.getTargetOutput());
                },
                buf -> new ProgressiveThermoPlantRecipe(
                        Inputs.STREAM_CODEC.decode(buf),
                        Outputs.STREAM_CODEC.decode(buf),
                        TemperatureRange.STREAM_CODEC.decode(buf),
                        ByteBufCodecs.FLOAT.decode(buf),
                        ByteBufCodecs.FLOAT.decode(buf),
                        ByteBufCodecs.FLOAT.decode(buf),
                        ByteBufCodecs.BOOL.decode(buf),
                        ByteBufCodecs.VAR_INT.decode(buf),
                        ItemStack.STREAM_CODEC.decode(buf)
                )
        );

        @Override
        public MapCodec<ProgressiveThermoPlantRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ProgressiveThermoPlantRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
