package com.swack.janemoose.gui;

import com.swack.janemoose.entity.JaneMooseBucketTruckEntity;
import com.swack.janemoose.network.BucketTruckActionPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.world.entity.player.Inventory;

public class BucketTruckScreen extends AbstractContainerScreen<BucketTruckMenu> {
    private EditBox nameEditBox;
    private Button speedButton;
    private Button stationButton;
    private Button lightsButton;

    public BucketTruckScreen(BucketTruckMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 224);
        this.inventoryLabelY = 134;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top = this.topPos;

        JaneMooseBucketTruckEntity truck = this.menu.getTruck();
        String currentName = (truck != null && truck.hasCustomName()) ? truck.getCustomName().getString() : "Jane Moose GKZ-14";

        this.nameEditBox = new EditBox(this.font, left + 8, top + 16, 110, 14, Component.literal("Name"));
        this.nameEditBox.setMaxLength(32);
        this.nameEditBox.setValue(currentName);
        this.addRenderableWidget(this.nameEditBox);

        // Rename Button
        this.addRenderableWidget(Button.builder(Component.literal("Set"), btn -> {
            String newName = this.nameEditBox.getValue().trim();
            if (!newName.isEmpty()) {
                sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_RENAME, newName));
            }
        }).bounds(left + 122, top + 15, 46, 16).build());

        // Row 1 (top + 78): 3-Speed Gear Selector (1 m/s, 2 m/s, 3 m/s) & Headlights
        this.speedButton = this.addRenderableWidget(Button.builder(getSpeedButtonText(), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_CYCLE_SPEED, ""));
        }).bounds(left + 8, top + 78, 86, 16).build());

        this.lightsButton = this.addRenderableWidget(Button.builder(getLightsButtonText(), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_TOGGLE_LIGHTS, ""));
        }).bounds(left + 98, top + 78, 70, 16).build());

        // Row 2 (top + 96): Boom Lower / Raise & Turntable Slew Left / Right
        this.addRenderableWidget(Button.builder(Component.literal("§eBoom -§r"), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_LOWER_BOOM, ""));
        }).bounds(left + 8, top + 96, 46, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("§aBoom +§r"), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_RAISE_BOOM, ""));
        }).bounds(left + 56, top + 96, 46, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("<-"), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_SLEW_LEFT, ""));
        }).bounds(left + 106, top + 96, 20, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("0°"), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_SLEW_CENTER, ""));
        }).bounds(left + 127, top + 96, 20, 16).build());

        this.addRenderableWidget(Button.builder(Component.literal("->"), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_SLEW_RIGHT, ""));
        }).bounds(left + 148, top + 96, 20, 16).build());

        // Row 3 (top + 114): Switch Seat (Cab <-> Bucket Platform)
        this.stationButton = this.addRenderableWidget(Button.builder(getStationButtonText(), btn -> {
            sendPacket(new BucketTruckActionPayload(BucketTruckActionPayload.ACTION_TOGGLE_BUCKET_SEAT, ""));
        }).bounds(left + 8, top + 114, 160, 16).build());
    }

    private Component getSpeedButtonText() {
        int gear = this.menu.getSpeedGear();
        return Component.literal("§aGear " + gear + " (" + gear + " m/s)§r");
    }

    private Component getLightsButtonText() {
        boolean on = this.menu.areHeadlightsOn();
        return on ? Component.literal("§eLights: ON§r") : Component.literal("§7Lights: OFF§r");
    }

    private Component getStationButtonText() {
        return this.menu.isInBucket()
                ? Component.literal("§eSeat: Aerial Bucket (Switch to Cab)§r")
                : Component.literal("§fSeat: Driver Cab (Switch to Bucket)§r");
    }

    private void sendPacket(BucketTruckActionPayload payload) {
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.speedButton != null) {
            this.speedButton.setMessage(getSpeedButtonText());
        }
        if (this.lightsButton != null) {
            this.lightsButton.setMessage(getLightsButtonText());
        }
        if (this.stationButton != null) {
            this.stationButton.setMessage(getStationButtonText());
        }
    }

    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int left = this.leftPos;
        int top = this.topPos;

        // Industrial utility truck console background
        extractor.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xFF20252B);
        // Outer border (Safety White/Blue-Gray trim)
        extractor.fill(left, top, left + this.imageWidth, top + 2, 0xFFD93829);
        extractor.fill(left, top + this.imageHeight - 2, left + this.imageWidth, top + this.imageHeight, 0xFFD93829);
        extractor.fill(left, top, left + 2, top + this.imageHeight, 0xFFD93829);
        extractor.fill(left + this.imageWidth - 2, top, left + this.imageWidth, top + this.imageHeight, 0xFFD93829);

        // Header banner
        extractor.fill(left + 2, top + 2, left + this.imageWidth - 2, top + 14, 0xFF1E3A5F);
        extractor.centeredText(this.font, "§lGKZ-14 BUCKET TRUCK§r", left + this.imageWidth / 2, top + 4, 0xFFFFFFFF);

        // Fuel Tank Gauge Section
        int fuel = this.menu.getFuel();
        int maxFuel = this.menu.getMaxFuel();
        float fuelPct = (maxFuel > 0) ? (float) fuel / maxFuel : 0.0F;

        extractor.text(this.font, "Fuel Tank:", left + 8, top + 34, 0xFFCCCCCC);
        extractor.text(this.font, fuel + " / " + maxFuel + " mB", left + 80, top + 34, 0xFFFFDE00);

        int barX = left + 8;
        int barY = top + 44;
        int barW = 68;
        int barH = 8;
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF111111);
        int filledW = (int) (barW * fuelPct);
        int gaugeColor = fuelPct > 0.25F ? 0xFF4CAF50 : 0xFFE53935;
        if (filledW > 0) {
            extractor.fill(barX, barY, barX + filledW, barY + barH, gaugeColor);
        }
        extractor.centeredText(this.font, (int) (fuelPct * 100) + "%", barX + barW / 2, barY, 0xFFFFFFFF);

        // Fuel Input/Output Slots
        extractor.fill(left + 79, top + 51, left + 97, top + 69, 0xFF14181C);
        extractor.text(this.font, "In", left + 83, top + 55, 0xFF666666);
        extractor.centeredText(this.font, "->", left + 115, top + 55, 0xFFFFDE00);
        extractor.fill(left + 133, top + 51, left + 151, top + 69, 0xFF14181C);
        extractor.text(this.font, "Out", left + 135, top + 55, 0xFF666666);

        // Boom Status Readout
        int boomPct = this.menu.getBoomPercent();
        double boomMeters = Math.round(JaneMooseBucketTruckEntity.getBucketHeightOffset(boomPct / 100.0F) * 10.0) / 10.0;
        extractor.text(this.font, "Boom Lift: §b" + boomPct + "% (" + boomMeters + "m)§r", left + 8, top + 67, 0xFFFFFFFF);

        // Player Inventory Label
        extractor.text(this.font, "Inventory", left + 8, top + this.inventoryLabelY, 0xFFCCCCCC);

        int invTop = top + 144;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                extractor.fill(left + 7 + c * 18, invTop - 1 + r * 18, left + 25 + c * 18, invTop + 17 + r * 18, 0xFF161A1E);
            }
        }
        for (int c = 0; c < 9; c++) {
            extractor.fill(left + 7 + c * 18, invTop + 57, left + 25 + c * 18, invTop + 75, 0xFF161A1E);
        }
    }
}
