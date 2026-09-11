package com.ohh.chronon_wand.item;

import com.ohh.chronon_wand.ChrononWand;
import com.ohh.chronon_wand.ModItems;
import earth.terrarium.tempad.api.tva_device.ChrononHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import com.ohh.chronon_wand.block.entity.ModBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;

@EventBusSubscriber(modid = ChrononWand.MOD_ID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(ChrononHandler.Capabilities.getItem(), (stack, ctx) -> new ItemChrononHandler(stack), ModItems.CHRONON_WAND.get());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.METRONOME_PORT.get(), (be, side) -> be.getFluidHandler(side));
        event.registerBlockEntity(ChrononHandler.Capabilities.getBlock(), ModBlockEntities.METRONOME_PORT.get(), (be, side) -> be.getChrononHandler(side));
    }
}
