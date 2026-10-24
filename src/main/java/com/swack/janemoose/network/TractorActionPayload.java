package com.swack.janemoose.network;

import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TractorActionPayload(int action, String extraData) implements CustomPacketPayload {
    public static final int ACTION_TOGGLE_LIFT = 0;
    public static final int ACTION_TOGGLE_ACTIVE = 1;
    public static final int ACTION_OPEN_GUI = 2;
    public static final int ACTION_RENAME = 3;
    public static final int ACTION_OPEN_ATTACHMENT_GUI = 4;
    public static final int ACTION_TOGGLE_LIGHTS = 5;

    public static final Type<TractorActionPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("janemoose", "tractor_action"));

    public static final StreamCodec<FriendlyByteBuf, TractorActionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, TractorActionPayload::action,
                    ByteBufCodecs.STRING_UTF8, TractorActionPayload::extraData,
                    TractorActionPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TractorActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            JaneMooseTractorEntity tractor = null;
            if (player.getVehicle() instanceof JaneMooseTractorEntity t) {
                tractor = t;
            } else if (player.containerMenu instanceof com.swack.janemoose.gui.TractorMenu menu) {
                tractor = menu.getTractor();
            }

            if (tractor != null) {
                switch (payload.action()) {
                    case ACTION_TOGGLE_LIFT -> tractor.toggleAttachmentLift(player);
                    case ACTION_TOGGLE_ACTIVE -> tractor.toggleAttachmentActive(player);
                    case ACTION_OPEN_GUI -> {
                        if (player instanceof ServerPlayer serverPlayer) {
                            tractor.openCustomGui(serverPlayer);
                        }
                    }
                    case ACTION_RENAME -> {
                        if (!payload.extraData().isBlank()) {
                            tractor.setCustomName(Component.literal(payload.extraData()));
                            tractor.setCustomNameVisible(true);
                        }
                    }
                    case ACTION_OPEN_ATTACHMENT_GUI -> {
                        JaneMooseAttachmentEntity att = tractor.getHitchedAttachment();
                        if (att != null && att.hasInventory() && player instanceof ServerPlayer serverPlayer) {
                            att.openCustomGui(serverPlayer);
                        }
                    }
                    case ACTION_TOGGLE_LIGHTS -> tractor.toggleHeadlights(player);
                }
            }
        });
    }
}
