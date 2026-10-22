package com.swack.janemoose.client;

import com.swack.janemoose.client.model.AttachmentModel;
import com.swack.janemoose.client.model.TractorModel;
import com.swack.janemoose.client.renderer.AttachmentRenderer;
import com.swack.janemoose.client.renderer.TractorRenderer;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.gui.TractorScreen;
import com.swack.janemoose.registry.ModEntities;
import com.swack.janemoose.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

public class JaneMooseClient {
    public static void registerClient(IEventBus modEventBus) {
        modEventBus.addListener(JaneMooseClient::registerLayerDefinitions);
        modEventBus.addListener(JaneMooseClient::registerEntityRenderers);
        modEventBus.addListener(JaneMooseClient::registerKeyMappings);
        modEventBus.addListener(JaneMooseClient::registerMenuScreens);

        NeoForge.EVENT_BUS.register(JaneMooseKeyBinds.class);
        NeoForge.EVENT_BUS.register(JaneMooseClient.class);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TractorModel.LAYER_LOCATION, TractorModel::createBodyLayer);
        event.registerLayerDefinition(AttachmentModel.LAYER_LOCATION, AttachmentModel::createBodyLayer);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TRACTOR.get(), TractorRenderer::new);
        event.registerEntityRenderer(ModEntities.ATTACHMENT.get(), AttachmentRenderer::new);
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        JaneMooseKeyBinds.register(event);
    }

    private static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.TRACTOR_MENU.get(), TractorScreen::new);
        event.register(ModMenus.ATTACHMENT_MENU.get(), com.swack.janemoose.gui.AttachmentScreen::new);
    }

    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getVehicle() instanceof JaneMooseTractorEntity) {
            // Hide horse/animal heart rows and jump charge bar when riding tractor
            if (event.getName().equals(VanillaGuiLayers.VEHICLE_HEALTH) || event.getName().equals(VanillaGuiLayers.CONTEXTUAL_INFO_BAR)) {
                event.setCanceled(true);
            }
        }
    }
}
