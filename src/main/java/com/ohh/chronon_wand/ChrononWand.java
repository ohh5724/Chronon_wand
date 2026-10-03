package com.ohh.chronon_wand;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("chronon_wand")
public class ChrononWand {
    public static final String MOD_ID = "chronon_wand";
    public static final String MODID = MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();

    public ChrononWand(IEventBus modEventBus, net.neoforged.fml.ModContainer container) {
        com.ohh.chronon_wand.config.ChrononWandConfig.load();
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, com.ohh.chronon_wand.config.ChrononWandServerConfig.SPEC);

        com.ohh.chronon_wand.fluid.ModFluids.register(modEventBus);
        ModBlocks.register(modEventBus);
        com.ohh.chronon_wand.block.entity.ModBlockEntities.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.register(modEventBus);
        com.ohh.chronon_wand.recipe.ModRecipeConditions.register(modEventBus);
        com.ohh.chronon_wand.entity.ModEntities.register(modEventBus);
        com.ohh.chronon_wand.menu.ModMenus.register(modEventBus);

        if (net.neoforged.fml.ModList.get().isLoaded("mekanism")) {
            com.ohh.chronon_wand.compat.mekanism.MekanismCompat.register(modEventBus);
        }
        if (net.neoforged.fml.ModList.get().isLoaded("pneumaticcraft")) {
            com.ohh.chronon_wand.compat.pneumaticcraft.PneumaticCraftCompat.register(modEventBus);
        }

        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::commonSetup);
    }

    private void onRegister(net.neoforged.neoforge.registries.RegisterEvent event) {
        if (net.neoforged.fml.ModList.get().isLoaded("ae2")) {
            com.ohh.chronon_wand.compat.ae2.AE2Compat.onRegister(event);
        }
    }

    private void commonSetup(net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (net.neoforged.fml.ModList.get().isLoaded("ae2")) {
                com.ohh.chronon_wand.compat.ae2.AE2Compat.init();
            }
        });
    }
}
