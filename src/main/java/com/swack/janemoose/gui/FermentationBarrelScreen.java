package com.swack.janemoose.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FermentationBarrelScreen extends AbstractContainerScreen<FermentationBarrelMenu> {
    public FermentationBarrelScreen(FermentationBarrelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 176);
        this.inventoryLabelY = 83;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor extractor, int mouseX, int mouseY) {
        // Labels are drawn in extractBackground for custom layout control
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int left = this.leftPos;
        int top = this.topPos;

        // Main oak barrel console background
        extractor.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xFF261F19);

        // Outer oak & brass frame border
        extractor.fill(left, top, left + this.imageWidth, top + 2, 0xFF8B5A2B);
        extractor.fill(left, top + this.imageHeight - 2, left + this.imageWidth, top + this.imageHeight, 0xFF8B5A2B);
        extractor.fill(left, top, left + 2, top + this.imageHeight, 0xFF8B5A2B);
        extractor.fill(left + this.imageWidth - 2, top, left + this.imageWidth, top + this.imageHeight, 0xFF8B5A2B);

        // Header banner (Jane Moose Green & Gold)
        extractor.fill(left + 2, top + 2, left + this.imageWidth - 2, top + 14, 0xFF2E7D32);
        extractor.centeredText(this.font, "§lFERMENTATION BARREL§r", left + this.imageWidth / 2, top + 4, 0xFFFFDE00);

        // Slot 0: Wheat Input Box (26, 38)
        extractor.text(this.font, "Wheat", left + 20, top + 26, 0xFFE6C280);
        extractor.fill(left + 25, top + 37, left + 43, top + 55, 0xFF14110E);

        // Fermentation Progress Bar (x: 48..90, y: 41..51)
        int progX = left + 48;
        int progY = top + 41;
        int progW = 42;
        int progH = 10;
        extractor.fill(progX, progY, progX + progW, progY + progH, 0xFF14110E);

        int progress = this.menu.getProgress();
        int maxProgress = this.menu.getMaxProgress();
        boolean fermenting = this.menu.isFermenting();
        if (fermenting && maxProgress > 0) {
            int filledW = Math.min(progW, (int) (((float) progress / maxProgress) * progW));
            if (filledW > 0) {
                extractor.fill(progX, progY, progX + filledW, progY + progH, 0xFFE6A817);
            }
            int pct = (int) (((float) progress / maxProgress) * 100);
            extractor.centeredText(this.font, pct + "%", progX + progW / 2, progY + 1, 0xFFFFFFFF);

            int remainingSeconds = Math.max(0, (maxProgress - progress + 19) / 20);
            extractor.centeredText(this.font, "§e" + remainingSeconds + "s left§r", progX + progW / 2, top + 55, 0xFFFFDE00);
        } else {
            extractor.centeredText(this.font, "->", progX + progW / 2, progY + 1, 0xFF777777);
            extractor.centeredText(this.font, "§71B / 60s§r", progX + progW / 2, top + 55, 0xFFAAAAAA);
        }
        extractor.centeredText(this.font, "Ferment", progX + progW / 2, top + 29, 0xFFCCCCCC);

        // Ethanol Tank Gauge (x: 96, y: 20, w: 24, h: 52)
        int tankX = left + 96;
        int tankY = top + 20;
        int tankW = 24;
        int tankH = 52;
        extractor.fill(tankX - 1, tankY - 1, tankX + tankW + 1, tankY + tankH + 1, 0xFF5C4028);
        extractor.fill(tankX, tankY, tankX + tankW, tankY + tankH, 0xFF12100E);

        int ethanol = this.menu.getEthanolAmount();
        int capacity = this.menu.getTankCapacity();
        if (ethanol > 0 && capacity > 0) {
            int fillH = Math.min(tankH, (int) (((float) ethanol / capacity) * tankH));
            if (fillH > 0) {
                // Golden-amber Ethanol biofuel fluid
                extractor.fill(tankX, tankY + tankH - fillH, tankX + tankW, tankY + tankH, 0xFFE5A93B);
                extractor.fill(tankX + 2, tankY + tankH - fillH, tankX + 6, tankY + tankH, 0xFFFFCC66);
            }
        }

        // Tank gauge graduation lines
        for (int i = 1; i < 4; i++) {
            int lineY = tankY + (tankH * i) / 4;
            extractor.fill(tankX, lineY, tankX + 6, lineY + 1, 0x88FFFFFF);
        }

        // Slot 1: Empty Bucket Input (134, 22)
        extractor.fill(left + 133, top + 21, left + 151, top + 39, 0xFF14110E);
        extractor.text(this.font, "In", left + 154, top + 26, 0xFFAAAAAA);

        // Downward arrow between Bucket In and Bucket Out
        extractor.centeredText(this.font, "v", left + 142, top + 42, 0xFFFFDE00);

        // Slot 2: Ethanol Bucket Output (134, 54)
        extractor.fill(left + 133, top + 53, left + 151, top + 71, 0xFF14110E);
        extractor.text(this.font, "Out", left + 154, top + 58, 0xFFFFDE00);

        // Ethanol mB readout above inventory
        extractor.text(this.font, "Inventory", left + 8, top + 83, 0xFFCCCCCC);
        String tankText = "Ethanol: " + ethanol + " / " + capacity + " mB";
        extractor.text(this.font, tankText, left + 58, top + 75, 0xFFFFD54F);

        // Player Inventory Slot Boxes (94) & Hotbar (152)
        int invTop = top + 94;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                extractor.fill(left + 7 + c * 18, invTop - 1 + r * 18, left + 25 + c * 18, invTop + 17 + r * 18, 0xFF181410);
            }
        }
        for (int c = 0; c < 9; c++) {
            extractor.fill(left + 7 + c * 18, top + 151, left + 25 + c * 18, top + 169, 0xFF181410);
        }
    }
}
