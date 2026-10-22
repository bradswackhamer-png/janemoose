package com.swack.janemoose.gui;

import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.item.IAttachment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AttachmentScreen extends AbstractContainerScreen<AttachmentMenu> {
    public AttachmentScreen(AttachmentMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 114 + menu.getRows() * 18);
        this.inventoryLabelY = 18 + menu.getRows() * 18 + 2;
    }

    public void extractBackground(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        int left = this.leftPos;
        int top = this.topPos;
        int rows = this.menu.getRows();

        // Background panel
        extractor.fill(left, top, left + this.imageWidth, top + this.imageHeight, 0xFF232822);
        // Green border
        extractor.fill(left, top, left + this.imageWidth, top + 2, 0xFF367C2B);
        extractor.fill(left, top + this.imageHeight - 2, left + this.imageWidth, top + this.imageHeight, 0xFF367C2B);
        extractor.fill(left, top, left + 2, top + this.imageHeight, 0xFF367C2B);
        extractor.fill(left + this.imageWidth - 2, top, left + this.imageWidth, top + this.imageHeight, 0xFF367C2B);

        // Header banner
        extractor.fill(left + 2, top + 2, left + this.imageWidth - 2, top + 15, 0xFF2E7D32);

        JaneMooseAttachmentEntity att = this.menu.getAttachment();
        String headerTitle = "IMPLEMENT STORAGE";
        if (att != null && att.getItem().getItem() instanceof IAttachment item) {
            headerTitle = switch (item.getType()) {
                case PLANTER -> "SEED HOPPERS (Add Seeds Here)";
                case HARVESTER -> "GRAIN TANK (Harvested Crops)";
                case CULTIVATOR -> "TOOL CHEST";
            };
        }
        extractor.centeredText(this.font, "§l" + headerTitle + "§r", left + this.imageWidth / 2, top + 4, 0xFFFFDE00);

        // Draw Attachment Slot Boxes
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 9; c++) {
                int sx = left + 7 + c * 18;
                int sy = top + 17 + r * 18;
                extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF141714);
            }
        }

        // Draw Player Inventory Label
        extractor.text(this.font, "Inventory", left + 8, top + this.inventoryLabelY, 0xFFCCCCCC);

        // Draw Player Inventory Slot Boxes
        int playerInvY = top + 18 + rows * 18 + 14;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                int sx = left + 7 + c * 18;
                int sy = playerInvY - 1 + r * 18;
                extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF1B1F1B);
            }
        }
        for (int c = 0; c < 9; c++) {
            int sx = left + 7 + c * 18;
            int sy = playerInvY + 57;
            extractor.fill(sx, sy, sx + 18, sy + 18, 0xFF1B1F1B);
        }
    }
}
