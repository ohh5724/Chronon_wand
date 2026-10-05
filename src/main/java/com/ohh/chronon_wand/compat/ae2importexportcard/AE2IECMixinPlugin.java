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
    private boolean isLegacy;
    private boolean isModern;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            LoadingModList modList = LoadingModList.get();
            if (modList != null) {
                ModFileInfo info = modList.getModFileById(TARGET_MOD_ID);
                if (info != null) {
                    this.isLoaded = true;
                    int[] ver = parseVersion(info.versionString());
                    int major = ver[0];
                    int minor = ver[1];
                    if (major > 1 || (major == 1 && minor >= 9)) {
                        this.isModern = true;
                    } else if (major == 1 && minor >= 7) {
                        this.isLegacy = true;
                    }
                    // For versions < 1.7.0 (e.g. 1.4.3), slot methods do not exist, so neither mixin is applied
                }
            }
        } catch (Throwable ignored) {
            this.isLoaded = false;
        }
    }

    private static int[] parseVersion(String version) {
        if (version == null) return new int[]{0, 0, 0};
        int lastDash = version.lastIndexOf('-');
        String ver = lastDash >= 0 ? version.substring(lastDash + 1) : version;
        String[] parts = ver.split("\\.");
        try {
            int major = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
            int minor = parts.length > 1 ? Integer.parseInt(parts[1].replaceAll("[^0-9]", "")) : 0;
            int patch = parts.length > 2 ? Integer.parseInt(parts[2].replaceAll("[^0-9]", "")) : 0;
            return new int[]{major, minor, patch};
        } catch (Exception ignored) {
            return new int[]{0, 0, 0};
        }
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
            return this.isLegacy;
        }
        return false;
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
