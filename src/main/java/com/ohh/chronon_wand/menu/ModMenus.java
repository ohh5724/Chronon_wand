package com.ohh.chronon_wand.menu;

import com.ohh.chronon_wand.ChrononWand;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ChrononWand.MOD_ID);

    public static final Supplier<MenuType<MetronomePortMenu>> METRONOME_PORT_MENU = MENUS.register(
            "metronome_port_menu",
            () -> IMenuTypeExtension.create(MetronomePortMenu::new)
    );

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
