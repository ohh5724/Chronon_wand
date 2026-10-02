package com.ohh.chronon_wand.compat.ae2importexportcard;

import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AE2IECMixinPlugin implements IMixinConfigPlugin {
    private static final String TARGET_MOD_ID = "ae2importexportcard";
    private boolean isLoaded;
    private boolean isModern;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            LoadingModList modList = LoadingModList.get();
            if (modList != null) {
                ModFileInfo info = modList.getModFileById(TARGET_MOD_ID);
                if (info != null) {
                    this.isLoaded = true;
                    this.isModern = isVersionAtLeast1_9(info.versionString());
                }
            }
        } catch (Throwable ignored) {
            this.isLoaded = false;
        }
    }

    private static boolean isVersionAtLeast1_9(String version) {
        if (version == null) return false;
        int dash = version.indexOf('-');
        String ver = dash >= 0 ? version.substring(dash + 1) : version;
        String[] parts = ver.split("\\.");
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = parts.length > 1 ? Integer.parseInt(parts[1].replaceAll("[^0-9]", "")) : 0;
            if (major > 1) return true;
            if (major == 1 && minor >= 9) return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!this.isLoaded) {
            return false;
        }
        if (mixinClassName.endsWith("WirelessTerminalItemModernMixin")) {
            return this.isModern;
        }
        if (mixinClassName.endsWith("WirelessTerminalItemLegacyMixin")) {
            return !this.isModern;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
