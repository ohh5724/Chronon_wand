package com.ohh.chronon_wand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ohh.chronon_wand.ChrononWand;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChrononWandConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ConfigData DATA = new ConfigData();

    public static class ConfigData {
        public int maxChronons = 10000;
        public List<Integer> speedTiers = new ArrayList<>(Arrays.asList(2, 4, 16, 64, 128, 256));
        public List<Integer> costPerSecond = new ArrayList<>(Arrays.asList(10, 15, 30, 50, 75, 100));
        public int maxDurationTicks = 600; // 30 seconds
    }

    public static File getConfigFile() {
        return FMLPaths.CONFIGDIR.get().resolve("chronon_wand.json").toFile();
    }

    public static void load() {
        File file = getConfigFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
                if (loaded != null) {
                    DATA = loaded;
                }
            } catch (Exception e) {
                ChrononWand.LOGGER.error("Failed to load chronon_wand.json", e);
            }
        } else {
            save();
        }

        if (DATA == null) {
            DATA = new ConfigData();
        }
        if (DATA.speedTiers == null || DATA.speedTiers.isEmpty()) {
            DATA.speedTiers = new ArrayList<>(Arrays.asList(2, 4, 16, 64, 128, 256));
        }
        if (DATA.costPerSecond == null || DATA.costPerSecond.isEmpty()) {
            DATA.costPerSecond = new ArrayList<>(Arrays.asList(10, 15, 30, 50, 75, 100));
        }
    }

    public static void save() {
        File file = getConfigFile();
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(DATA, writer);
            }
        } catch (Exception e) {
            ChrononWand.LOGGER.error("Failed to save chronon_wand.json", e);
        }
    }
}
