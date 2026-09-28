package com.ohh.chronon_wand.client;

import com.ohh.chronon_wand.config.ChrononWandConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigScreenFactory {
    public static Screen createScreen(Screen parent) {
        ChrononWandConfig.load();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.chronon_wand.config"));

        builder.setSavingRunnable(ChrononWandConfig::save);

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("category.chronon_wand.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startIntField(Component.translatable("option.chronon_wand.max_chronons"), ChrononWandConfig.DATA.maxChronons)
                .setDefaultValue(10000)
                .setSaveConsumer(newValue -> ChrononWandConfig.DATA.maxChronons = newValue)
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("option.chronon_wand.max_duration_ticks"), ChrononWandConfig.DATA.maxDurationTicks)
                .setDefaultValue(600)
                .setSaveConsumer(newValue -> ChrononWandConfig.DATA.maxDurationTicks = newValue)
                .build());

        general.addEntry(entryBuilder.startIntList(Component.translatable("option.chronon_wand.speed_tiers"), new ArrayList<>(ChrononWandConfig.DATA.speedTiers))
                .setDefaultValue(Arrays.asList(2, 4, 16, 64, 128, 256))
                .setSaveConsumer(newValue -> ChrononWandConfig.DATA.speedTiers = new ArrayList<>(newValue))
                .build());

        general.addEntry(entryBuilder.startIntList(Component.translatable("option.chronon_wand.cost_per_second"), new ArrayList<>(ChrononWandConfig.DATA.costPerSecond))
                .setDefaultValue(Arrays.asList(10, 15, 30, 50, 75, 100))
                .setSaveConsumer(newValue -> ChrononWandConfig.DATA.costPerSecond = new ArrayList<>(newValue))
                .build());

        return builder.build();
    }
}
