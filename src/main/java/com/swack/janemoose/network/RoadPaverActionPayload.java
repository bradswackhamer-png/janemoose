package com.swack.janemoose.network;

import com.swack.janemoose.entity.BaseRoadPaverEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RoadPaverActionPayload(int entityId, int action, String value) implements CustomPacketPayload {
    public static final Type<RoadPaverActionPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("janemoose", "road_paver_action"));

    public static final StreamCodec<FriendlyByteBuf, RoadPaverActionPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RoadPaverActionPayload::entityId,
            ByteBufCodecs.VAR_INT, RoadPaverActionPayload::action,
            ByteBufCodecs.STRING_UTF8, RoadPaverActionPayload::value,
            RoadPaverActionPayload::new
    );

    public static final int ACTION_RENAME = 1;
    public static final int ACTION_TOGGLE_WORK_MODE = 2;
    public static final int ACTION_TOGGLE_LIGHT = 3;
    public static final int ACTION_OPEN_GUI = 4;
    public static final int ACTION_START = 5;
    public static final int ACTION_STOP = 6;
    public static final int ACTION_REVERSE = 7;
    public static final int ACTION_CYCLE_LOCK = 8;
    public static final int ACTION_TOGGLE_HOLOGRAM = 9;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RoadPaverActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player p = context.player();
            if (p instanceof ServerPlayer player) {
                BaseRoadPaverEntity paver = null;
                if (player.getVehicle() instanceof BaseRoadPaverEntity ridingPaver) {
                    paver = ridingPaver;
                } else if (payload.entityId() != 0) {
                    Entity ent = player.level().getEntity(payload.entityId());
                    if (ent instanceof BaseRoadPaverEntity found) {
                        paver = found;
                    }
                }

                if (paver != null && paver.stillValid(player)) {
                    switch (payload.action()) {
                        case ACTION_RENAME -> {
                            String trimmed = payload.value().trim();
                            if (!trimmed.isEmpty()) {
                                paver.setCustomName(Component.literal(trimmed));
                                paver.setCustomNameVisible(true);
                            }
                        }
                        case ACTION_TOGGLE_WORK_MODE -> {
                            boolean next = !paver.isWorkMode();
                            paver.setWorkMode(next);
                            player.sendOverlayMessage(Component.literal(paver.getName().getString() + ": " +
                                    (next ? "Work Mode (Paving 1.0 m/s)" : "Transport Mode (No Pave 2.0 m/s)"))
                                    .withStyle(next ? ChatFormatting.GREEN : ChatFormatting.GOLD));
                        }
                        case ACTION_TOGGLE_LIGHT -> {
                            boolean next = !paver.isWorkLightEnabled();
                            paver.setWorkLightEnabled(next);
                            player.sendOverlayMessage(Component.literal(paver.getName().getString() + ": Work Light " +
                                    (next ? "ON" : "OFF")).withStyle(next ? ChatFormatting.GOLD : ChatFormatting.GRAY));
                        }
                        case ACTION_OPEN_GUI -> paver.openCustomGui(player);
                        case ACTION_START -> {
                            if (paver.isWorkMode() && !paver.hasPaverMaterial()) {
                                paver.setRunMode(BaseRoadPaverEntity.MODE_STOPPED);
                                paver.notifyOutOfMaterial(player);
                                return;
                            }
                            paver.setRunMode(BaseRoadPaverEntity.MODE_FORWARD);
                            player.sendOverlayMessage(Component.literal(paver.getName().getString() + ": Forward").withStyle(ChatFormatting.GREEN));
                        }
                        case ACTION_STOP -> {
                            paver.setRunMode(BaseRoadPaverEntity.MODE_STOPPED);
                            player.sendOverlayMessage(Component.literal(paver.getName().getString() + ": Stopped").withStyle(ChatFormatting.RED));
                        }
                        case ACTION_REVERSE -> {
                            int next = (paver.getRunMode() == BaseRoadPaverEntity.MODE_REVERSE)
                                    ? BaseRoadPaverEntity.MODE_FORWARD
                                    : BaseRoadPaverEntity.MODE_REVERSE;
                            paver.setRunMode(next);
                            player.sendOverlayMessage(Component.literal(paver.getName().getString() + ": " + (next == BaseRoadPaverEntity.MODE_REVERSE ? "Reverse" : "Forward")).withStyle(ChatFormatting.YELLOW));
                        }
                        case ACTION_CYCLE_LOCK -> {
                            int next = (paver.getCardinalLock() + 1) % 5;
                            paver.setCardinalLock(next);
                            player.sendOverlayMessage(Component.literal(paver.getName().getString() + ": Cardinal Lock " + next).withStyle(ChatFormatting.AQUA));
                        }
                        case ACTION_TOGGLE_HOLOGRAM -> {
                            boolean next = !paver.isHologramEnabled();
                            paver.setHologramEnabled(next);
                            player.sendOverlayMessage(Component.literal(paver.getName().getString() + ": Hologram " + (next ? "ON" : "OFF")).withStyle(next ? ChatFormatting.AQUA : ChatFormatting.GRAY));
                        }
                        default -> {}
                    }
                }
            }
        });
    }
}
