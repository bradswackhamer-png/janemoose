package com.swack.janemoose.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.swack.janemoose.entity.JaneMooseBucketTruckEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.network.BucketTruckActionPayload;
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

    public static final KeyMapping KEY_RAISE_BOOM = new KeyMapping(
            "key.janemoose.raise_boom",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_R,
            CATEGORY
    );

    public static final KeyMapping KEY_LOWER_BOOM = new KeyMapping(
            "key.janemoose.lower_boom",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_V,
            CATEGORY
    );

    public static final KeyMapping KEY_CYCLE_SPEED = new KeyMapping(
            "key.janemoose.cycle_speed",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_G,
            CATEGORY
    );

    public static final KeyMapping KEY_SWITCH_SEAT = new KeyMapping(
            "key.janemoose.switch_seat",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_B,
            CATEGORY
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(KEY_TOGGLE_LIFT);
        event.register(KEY_TOGGLE_ACTIVE);
        event.register(KEY_TOGGLE_LIGHTS);
        event.register(KEY_OPEN_GUI);
        event.register(KEY_RAISE_BOOM);
        event.register(KEY_LOWER_BOOM);
        event.register(KEY_CYCLE_SPEED);
        event.register(KEY_SWITCH_SEAT);
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
        } else if (mc.player.getVehicle() instanceof JaneMooseBucketTruckEntity) {
            boolean raisedClicked = false;
            while (KEY_RAISE_BOOM.consumeClick()) {
                raisedClicked = true;
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_RAISE_BOOM, "")
                ));
            }
            if (!raisedClicked && KEY_RAISE_BOOM.isDown() && mc.player.tickCount % 3 == 0) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_RAISE_BOOM, "")
                ));
            }

            boolean loweredClicked = false;
            while (KEY_LOWER_BOOM.consumeClick()) {
                loweredClicked = true;
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_LOWER_BOOM, "")
                ));
            }
            if (!loweredClicked && KEY_LOWER_BOOM.isDown() && mc.player.tickCount % 3 == 0) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_LOWER_BOOM, "")
                ));
            }

            while (KEY_TOGGLE_LIGHTS.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_TOGGLE_LIGHTS, "")
                ));
            }

            while (KEY_OPEN_GUI.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_OPEN_GUI, "")
                ));
            }

            while (KEY_CYCLE_SPEED.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_CYCLE_SPEED, "")
                ));
            }

            while (KEY_SWITCH_SEAT.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_TOGGLE_BUCKET_SEAT, "")
                ));
            }
        } else if (mc.player.getVehicle() instanceof com.swack.janemoose.entity.BaseRoadPaverEntity paver) {
            while (KEY_OPEN_GUI.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new com.swack.janemoose.network.RoadPaverActionPayload(paver.getId(), com.swack.janemoose.network.RoadPaverActionPayload.ACTION_OPEN_GUI, "")
                ));
            }
            while (KEY_TOGGLE_LIGHTS.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new com.swack.janemoose.network.RoadPaverActionPayload(paver.getId(), com.swack.janemoose.network.RoadPaverActionPayload.ACTION_TOGGLE_LIGHT, "")
                ));
            }
            while (KEY_TOGGLE_ACTIVE.consumeClick()) {
                mc.getConnection().send(new ServerboundCustomPayloadPacket(
                        new com.swack.janemoose.network.RoadPaverActionPayload(paver.getId(), com.swack.janemoose.network.RoadPaverActionPayload.ACTION_TOGGLE_WORK_MODE, "")
                ));
            }
        }
    }
}
