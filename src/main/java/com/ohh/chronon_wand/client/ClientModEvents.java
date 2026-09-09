package com.ohh.chronon_wand.client;

import com.ohh.chronon_wand.ChrononWand;
import com.ohh.chronon_wand.entity.ModEntities;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ChrononWand.MOD_ID, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ACCELERATOR.get(), InvisibleRenderer::new);
    }

    private static class InvisibleRenderer extends EntityRenderer<com.ohh.chronon_wand.entity.AcceleratorEntity> {
        public InvisibleRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(com.ohh.chronon_wand.entity.AcceleratorEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ChrononWand.MOD_ID, "textures/entity/invisible.png");
        }
    }
}

