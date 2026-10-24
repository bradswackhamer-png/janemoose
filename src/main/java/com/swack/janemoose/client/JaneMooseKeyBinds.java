package com.swack.janemoose.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.network.TractorActionPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class JaneMooseKeyBinds {
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("janemoose", "machinery"));

    public static final KeyMapping KEY_TOGGLE_LIFT = new KeyMapping(
            "key.janemoose.toggle_lift",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_H,
            CATEGORY
    );

    public static final KeyMapping KEY_TOGGLE_ACTIVE = new KeyMapping(
            "key.janemoose.toggle_active",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_X,
            CATEGORY
    );

    public static final KeyMapping KEY_TOGGLE_LIGHTS = new KeyMapping(
            "key.janemoose.toggle_lights",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_L,
            CATEGORY
    );

    public static final KeyMapping KEY_OPEN_GUI = new KeyMapping(
            "key.janemoose.open_gui",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_C,
            CATEGORY
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(KEY_TOGGLE_LIFT);
        event.register(KEY_TOGGLE_ACTIVE);
        event.register(KEY_TOGGLE_LIGHTS);
        event.register(KEY_OPEN_GUI);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;

        if (mc.player.getVehicle() instanceof JaneMooseTractorEntity) {
            while (KEY_TOGGLE_LIFT.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new TractorActionPayload(TractorActionPayload.ACTION_TOGGLE_LIFT, "")
                ));
            }

            while (KEY_TOGGLE_ACTIVE.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new TractorActionPayload(TractorActionPayload.ACTION_TOGGLE_ACTIVE, "")
                ));
            }

            while (KEY_TOGGLE_LIGHTS.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new TractorActionPayload(TractorActionPayload.ACTION_TOGGLE_LIGHTS, "")
                ));
            }

            while (KEY_OPEN_GUI.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new TractorActionPayload(TractorActionPayload.ACTION_OPEN_GUI, "")
                ));
            }
        }
    }
}
