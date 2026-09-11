package com.ohh.chronon_wand.entity;

import com.ohh.chronon_wand.ChrononWand;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, ChrononWand.MOD_ID);

    public static final Supplier<EntityType<AcceleratorEntity>> ACCELERATOR = ENTITY_TYPES.register("accelerator", () ->
            EntityType.Builder.<AcceleratorEntity>of(AcceleratorEntity::new, MobCategory.MISC)
                    .sized(0.0f, 0.0f)
                    .clientTrackingRange(0)
                    .build("accelerator")
    );

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}

