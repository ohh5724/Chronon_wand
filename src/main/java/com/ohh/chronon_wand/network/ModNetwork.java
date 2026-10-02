package com.ohh.chronon_wand.network;

import com.ohh.chronon_wand.ChrononWand;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ChrononWand.MOD_ID)
public class ModNetwork {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                SetSideModePayload.TYPE,
                SetSideModePayload.STREAM_CODEC,
                SetSideModePayload::handle
        );
        registrar.playToServer(
                AcceleratorSettingsPayload.TYPE,
                AcceleratorSettingsPayload.STREAM_CODEC,
                AcceleratorSettingsPayload::handle
        );
    }
}
