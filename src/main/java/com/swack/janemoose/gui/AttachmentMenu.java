package com.swack.janemoose.gui;

import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AttachmentMenu extends AbstractContainerMenu {
    private final JaneMooseAttachmentEntity attachment;
    private final int slotCount;

    public AttachmentMenu(int containerId, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInv, findAttachment(playerInv, buf.readInt()));
    }

    public AttachmentMenu(int containerId, Inventory playerInv, JaneMooseAttachmentEntity attachment) {
        super(ModMenus.ATTACHMENT_MENU.get(), containerId);
        this.attachment = attachment;
        this.slotCount = (attachment != null) ? attachment.getSlotCount() : 27;

        int rows = Math.max(1, this.slotCount / 9);

        // Attachment Inventory Slots (1, 2, or 3 rows of 9)
        if (attachment != null) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < 9; c++) {
                    this.addSlot(new Slot(attachment.getInventory(), c + r * 9, 8 + c * 18, 18 + r * 18));
                }
            }
        }

        // Player Inventory
        int playerInvY = 18 + rows * 18 + 14;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                this.addSlot(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, playerInvY + r * 18));
            }
        }

        // Player Hotbar
        for (int c = 0; c < 9; c++) {
            this.addSlot(new Slot(playerInv, c, 8 + c * 18, playerInvY + 58));
        }
    }

    private static JaneMooseAttachmentEntity findAttachment(Inventory inv, int id) {
        Entity entity = inv.player.level().getEntity(id);
        return (entity instanceof JaneMooseAttachmentEntity att) ? att : null;
    }

    public JaneMooseAttachmentEntity getAttachment() {
        return this.attachment;
    }

    public int getSlotCount() {
        return this.slotCount;
    }

    public int getRows() {
        return Math.max(1, this.slotCount / 9);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.slotCount) {
                if (!this.moveItemStackTo(itemstack1, this.slotCount, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.slotCount, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.attachment != null && this.attachment.isAlive() && player.distanceToSqr(this.attachment) < 64.0;
    }
}
