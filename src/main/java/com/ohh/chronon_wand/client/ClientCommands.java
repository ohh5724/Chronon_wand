package com.ohh.chronon_wand.client;

import com.ohh.chronon_wand.ChrononWand;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.minecraft.commands.Commands;

@EventBusSubscriber(modid = ChrononWand.MOD_ID, value = Dist.CLIENT)
public class ClientCommands {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("chrononwand")
                        .then(Commands.literal("serverconfig")
                                .executes(context -> {
                                    Minecraft.getInstance().tell(() -> {
                                        Minecraft.getInstance().setScreen(ConfigScreenFactory.createScreen(Minecraft.getInstance().screen));
                                    });
                                    return 1;
                                })
                        )
        );
    }
}
