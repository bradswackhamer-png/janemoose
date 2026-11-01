package com.swack.janemoose.network;

import com.swack.janemoose.entity.JaneMooseBucketTruckEntity;
import com.swack.janemoose.gui.BucketTruckMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BucketTruckActionPayload(int action, String extraData) implements CustomPacketPayload {
    public static final int ACTION_RAISE_BOOM = 0;
    public static final int ACTION_LOWER_BOOM = 1;
    public static final int ACTION_CYCLE_SPEED = 2;
    public static final int ACTION_TOGGLE_BUCKET_SEAT = 3;
    public static final int ACTION_TOGGLE_LIGHTS = 4;
    public static final int ACTION_OPEN_GUI = 5;
    public static final int ACTION_RENAME = 6;
    public static final int ACTION_SLEW_LEFT = 7;
    public static final int ACTION_SLEW_RIGHT = 8;
    public static final int ACTION_SLEW_CENTER = 9;

    public static final Type<BucketTruckActionPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("janemoose", "bucket_truck_action"));

    public static final StreamCodec<FriendlyByteBuf, BucketTruckActionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, BucketTruckActionPayload::action,
                    ByteBufCodecs.STRING_UTF8, BucketTruckActionPayload::extraData,
                    BucketTruckActionPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BucketTruckActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            JaneMooseBucketTruckEntity truck = null;
            if (player.getVehicle() instanceof JaneMooseBucketTruckEntity t) {
                truck = t;
            } else if (player.containerMenu instanceof BucketTruckMenu menu) {
                truck = menu.getTruck();
            }

            if (truck != null) {
                switch (payload.action()) {
                    case ACTION_RAISE_BOOM -> truck.adjustBoomTarget(0.05F, player);
                    case ACTION_LOWER_BOOM -> truck.adjustBoomTarget(-0.05F, player);
                    case ACTION_CYCLE_SPEED -> truck.cycleSpeedGear(player);
                    case ACTION_TOGGLE_BUCKET_SEAT -> truck.toggleCabAndBucket(player);
                    case ACTION_TOGGLE_LIGHTS -> truck.toggleHeadlights(player);
                    case ACTION_OPEN_GUI -> {
                        if (player instanceof ServerPlayer serverPlayer) {
                            truck.openCustomGui(serverPlayer);
                        }
                    }
                    case ACTION_RENAME -> {
                        if (!payload.extraData().isBlank()) {
                            truck.setCustomName(Component.literal(payload.extraData()));
                            truck.setCustomNameVisible(true);
                        }
                    }
                    case ACTION_SLEW_LEFT -> truck.adjustBoomYaw(-15.0F, player);
                    case ACTION_SLEW_RIGHT -> truck.adjustBoomYaw(15.0F, player);
                    case ACTION_SLEW_CENTER -> truck.setBoomYaw(0.0F);
                }
            }
        });
    }
}
