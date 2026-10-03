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

    @SubscribeEvent
    public static void onRegisterScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(com.ohh.chronon_wand.menu.ModMenus.METRONOME_PORT_MENU.get(), com.ohh.chronon_wand.client.screen.MetronomePortScreen::new);
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
        event.registerFluidType(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
            private static final ResourceLocation FLOWING = ResourceLocation.withDefaultNamespace("block/water_flow");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public int getTintColor() {
                return 0xFFFFA020;
            }
        }, com.ohh.chronon_wand.fluid.ModFluids.CHRONON_FLUID_TYPE.get());

        event.registerFluidType(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL = ResourceLocation.withDefaultNamespace("block/water_still");
            private static final ResourceLocation FLOWING = ResourceLocation.withDefaultNamespace("block/water_flow");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING;
            }

            @Override
            public int getTintColor() {
                return 0xFF351052;
            }
        }, com.ohh.chronon_wand.fluid.ModFluids.ANTITIME_FLUID_TYPE.get());
    }

    private static class InvisibleRenderer extends EntityRenderer<com.ohh.chronon_wand.entity.AcceleratorEntity> {
        public InvisibleRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public boolean shouldRender(com.ohh.chronon_wand.entity.AcceleratorEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double camX, double camY, double camZ) {
            return false;
        }

        @Override
        public ResourceLocation getTextureLocation(com.ohh.chronon_wand.entity.AcceleratorEntity entity) {
            return ResourceLocation.fromNamespaceAndPath(ChrononWand.MOD_ID, "textures/entity/invisible.png");
        }
    }
}

