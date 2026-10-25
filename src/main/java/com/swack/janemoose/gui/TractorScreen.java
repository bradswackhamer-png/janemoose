package com.swack.janemoose.gui;

import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.network.TractorActionPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.world.entity.player.Inventory;

public class TractorScreen extends AbstractContainerScreen<TractorMenu> {
    private EditBox nameEditBox;
    private Button liftButton;
    private Button activeButton;
    private Button lightsButton;
    private Button hopperButton;

    public TractorScreen(TractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 206);
        this.inventoryLabelY = 116;
    }

    @Override
    protected void init() {
        super.init();

        int left = this.leftPos;
        int top = this.topPos;

        // Name edit box at top
        JaneMooseTractorEntity tractor = this.menu.getTractor();
        String currentName = (tractor != null && tractor.hasCustomName()) ? tractor.getCustomName().getString() : "Jane Moose 4020";

        this.nameEditBox = new EditBox(this.font, left + 8, top + 16, 110, 14, Component.literal("Name"));
        this.nameEditBox.setMaxLength(32);
        this.nameEditBox.setValue(currentName);
        this.addRenderableWidget(this.nameEditBox);

        // Rename Button
        this.addRenderableWidget(Button.builder(Component.literal("Set"), btn -> {
            String newName = this.nameEditBox.getValue().trim();
            if (!newName.isEmpty()) {
                sendPacket(new TractorActionPayload(TractorActionPayload.ACTION_RENAME, newName));
            }
        }).bounds(left + 122, top + 15, 46, 16).build());

        // Lift / Lower Button
        this.liftButton = this.addRenderableWidget(Button.builder(getLiftButtonText(), btn -> {
            sendPacket(new TractorActionPayload(TractorActionPayload.ACTION_TOGGLE_LIFT, ""));
        }).bounds(left + 8, top + 78, 78, 16).build());

        // Active Button (On / Off)
        this.activeButton = this.addRenderableWidget(Button.builder(getActiveButtonText(), btn -> {
            sendPacket(new TractorActionPayload(TractorActionPayload.ACTION_TOGGLE_ACTIVE, ""));
        }).bounds(left + 90, top + 78, 78, 16).build());

        // Headlights Button (On / Off)
        this.lightsButton = this.addRenderableWidget(Button.builder(getLightsButtonText(), btn -> {
            sendPacket(new TractorActionPayload(TractorActionPayload.ACTION_TOGGLE_LIGHTS, ""));
        }).bounds(left + 8, top + 96, 78, 16).build());

        // Open Hopper / Grain Tank Button
        this.hopperButton = this.addRenderableWidget(Button.builder(getHopperButtonText(), btn -> {
            sendPacket(new TractorActionPayload(TractorActionPayload.ACTION_OPEN_ATTACHMENT_GUI, ""));
        }).bounds(left + 90, top + 96, 78, 16).build());
    }

    private Component getLightsButtonText() {
        boolean on = this.menu.areHeadlightsOn();
        return on ? Component.literal("§eLights: ON§r") : Component.literal("§7Lights: OFF§r");
    }

    private Component getLiftButtonText() {
        if (!this.menu.isHitched()) {
            return Component.literal("No Implement");
        }
        return this.menu.isLowered() ? Component.literal("§aLowered§r (Work)") : Component.literal("§eLifted§r (Trans)");
    }

    private Component getActiveButtonText() {
        if (!this.menu.isHitched()) {
            return Component.literal("No Implement");
        }
        return this.menu.isActive() ? Component.literal("§aEngine: ON§r") : Component.literal("§cEngine: OFF§r");
    }

    private Component getHopperButtonText() {
        if (!this.menu.isHitched()) {
            return Component.literal("No Implement");
        }
        if (!this.menu.hasInventory()) {
            return Component.literal("No Hopper");
        }
        JaneMooseTractorEntity tractor = this.menu.getTractor();
        JaneMooseAttachmentEntity att = (tractor != null) ? tractor.getHitchedAttachment() : null;
        if (att != null && att.getItem().getItem() instanceof com.swack.janemoose.item.IAttachment iAtt) {
            return (iAtt.getType() == com.swack.janemoose.item.IAttachment.AttachmentType.PLANTER) ?
                    Component.literal("Seed Hopper") : Component.literal("Grain Tank");
        }
        return Component.literal("Open Hopper");
    }

    private void sendPacket(TractorActionPayload payload) {
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().send(new ServerboundCustomPayloadPacket(payload));
        }
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (this.liftButton != null) {
            this.liftButton.setMessage(getLiftButtonText());
            this.liftButton.active = this.menu.isHitched();
        }
        if (this.activeButton != null) {
            this.activeButton.setMessage(getActiveButtonText());
            this.activeButton.active = this.menu.isHitched();
        }
        if (this.lightsButton != null) {
            this.lightsButton.setMessage(getLightsButtonText());
        }
        if (this.hopperButton != null) {
            this.hopperButton.setMessage(getHopperButtonText());
            this.hopperButton.active = this.menu.isHitched() && this.menu.hasInventory();
        }
    }

    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int left = this.leftPos;
        int top = this.topPos;

        // Background panel (dark steel agricultural console)
        extractor.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xFF232822);
        // Outer border
        extractor.fill(left, top, left + this.imageWidth, top + 2, 0xFF367C2B);
        extractor.fill(left, top + this.imageHeight - 2, left + this.imageWidth, top + this.imageHeight, 0xFF367C2B);
        extractor.fill(left, top, left + 2, top + this.imageHeight, 0xFF367C2B);
        extractor.fill(left + this.imageWidth - 2, top, left + this.imageWidth, top + this.imageHeight, 0xFF367C2B);

        // Header banner (John Deere Green)
        extractor.fill(left + 2, top + 2, left + this.imageWidth - 2, top + 14, 0xFF2E7D32);
        extractor.centeredText(this.font, "§lJANE MOOSE DASHBOARD§r", left + this.imageWidth / 2, top + 4, 0xFFFFDE00);

        // Fuel Tank Gauge Section (top + 34)
        int fuel = this.menu.getFuel();
        int maxFuel = this.menu.getMaxFuel();
        float fuelPct = (maxFuel > 0) ? (float) fuel / maxFuel : 0.0F;

        extractor.text(this.font, "Fuel Tank:", left + 8, top + 34, 0xFFCCCCCC);
        extractor.text(this.font, fuel + " / " + maxFuel + " mB", left + 80, top + 34, 0xFFFFDE00);

        // Fuel gauge bar
        int barX = left + 8;
        int barY = top + 44;
        int barW = 68;
        int barH = 8;
        // Gauge background
        extractor.fill(barX, barY, barX + barW, barY + barH, 0xFF111111);
        // Gauge fill
        int filledW = (int) (barW * fuelPct);
        int gaugeColor = fuelPct > 0.25F ? 0xFF4CAF50 : 0xFFE53935;
        if (filledW > 0) {
            extractor.fill(barX, barY, barX + filledW, barY + barH, gaugeColor);
        }
        // Percentage text
        extractor.centeredText(this.font, (int) (fuelPct * 100) + "%", barX + barW / 2, barY, 0xFFFFFFFF);

        // Fuel Slot Box (Slot 0)
        extractor.fill(left + 79, top + 51, left + 97, top + 69, 0xFF1A1A1A);
        extractor.text(this.font, "In", left + 83, top + 55, 0xFF666666);

        // Arrow
        extractor.centeredText(this.font, "->", left + 115, top + 55, 0xFFFFDE00);

        // Empty Return Slot Box (Slot 1)
        extractor.fill(left + 133, top + 51, left + 151, top + 69, 0xFF1A1A1A);
        extractor.text(this.font, "Out", left + 135, top + 55, 0xFF666666);

        // Implement Status Card (top + 68)
        JaneMooseTractorEntity tractor = this.menu.getTractor();
        JaneMooseAttachmentEntity att = (tractor != null) ? tractor.getHitchedAttachment() : null;
        String attachName = (att != null) ? att.getItem().getHoverName().getString() : "None (Unhitched)";
        extractor.text(this.font, "Implement: §b" + attachName + "§r", left + 8, top + 68, 0xFFFFFFFF);

        // Player Inventory Label
        extractor.text(this.font, "Inventory", left + 8, top + this.inventoryLabelY, 0xFFCCCCCC);

        // Slot backgrounds for Player inventory
        int invTop = top + 126;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                extractor.fill(left + 7 + c * 18, invTop - 1 + r * 18, left + 25 + c * 18, invTop + 17 + r * 18, 0xFF1B1F1B);
            }
        }
        for (int c = 0; c < 9; c++) {
            extractor.fill(left + 7 + c * 18, invTop + 57, left + 25 + c * 18, invTop + 75, 0xFF1B1F1B);
        }
    }
}
