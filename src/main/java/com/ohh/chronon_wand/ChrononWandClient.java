package com.ohh.chronon_wand;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = ChrononWand.MODID, dist = Dist.CLIENT)
public class ChrononWandClient {
    public ChrononWandClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(ChrononWandClient::onClientSetup);
        modEventBus.addListener(ChrononWandClient::registerItemColors);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (net.neoforged.fml.ModList.get().isLoaded("ae2")) {
                com.ohh.chronon_wand.compat.ae2.AE2Compat.initClient();
            }
        });
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (var cellHolder : ModItems.CHRONON_CELLS.values()) {
            event.register((stack, tintIndex) -> {
                if (tintIndex == 1) {
                    return com.ohh.chronon_wand.compat.ae2.AE2Compat.getCellLedColor(stack);
                }
                return -1;
            }, cellHolder.get());
        }
    }
}
