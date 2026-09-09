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

    public ChrononWand(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.register(modEventBus);
        com.ohh.chronon_wand.entity.ModEntities.register(modEventBus);
    }
}
