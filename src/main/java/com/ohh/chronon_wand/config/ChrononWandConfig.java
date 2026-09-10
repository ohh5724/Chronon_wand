package com.ohh.chronon_wand.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ChrononWandConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "chronon_wand.json");

    public static ConfigData DATA = new ConfigData();

    public static class ConfigData {
        public int maxChronons = 10000;
        public List<Integer> speedTiers = Arrays.asList(2, 4, 16, 64, 128, 256);
        public List<Integer> costPerSecond = Arrays.asList(10, 15, 30, 50, 75, 100);
        public int maxDurationTicks = 600; // 30 seconds
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                DATA = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(DATA, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

