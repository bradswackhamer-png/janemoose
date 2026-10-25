package com.swack.janemoose.gui;

import com.swack.janemoose.block.entity.FermentationBarrelBlockEntity;
import com.swack.janemoose.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FermentationBarrelMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    // Client-side constructor
    public FermentationBarrelMenu(int containerId, Inventory playerInv) {
        this(containerId, playerInv, new SimpleContainer(FermentationBarrelBlockEntity.INVENTORY_SIZE), new SimpleContainerData(5));
    }

    // Server-side constructor
    public FermentationBarrelMenu(int containerId, Inventory playerInv, Container container, ContainerData data) {
        super(ModMenus.FERMENTATION_BARREL_MENU.get(), containerId);
        checkContainerSize(container, FermentationBarrelBlockEntity.INVENTORY_SIZE);
        checkContainerDataCount(data, 5);
        this.container = container;
        this.data = data;

        // Slot 0: Wheat Input (Left)
        this.addSlot(new Slot(container, 0, 26, 38) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.WHEAT);
            }
        });

        // Slot 1: Empty Bucket Input (Top-Right)
        this.addSlot(new Slot(container, 1, 134, 22) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.BUCKET);
            }
        });

        // Slot 2: Ethanol Bucket Output (Bottom-Right)
        this.addSlot(new Slot(container, 2, 134, 54) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 94 + row * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 152));
        }

        this.addDataSlots(data);
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getMaxProgress() {
        return this.data.get(1);
    }

    public int getEthanolAmount() {
        return this.data.get(2);
    }

    public int getTankCapacity() {
        return this.data.get(3);
    }

    public boolean isFermenting() {
        return this.data.get(4) == 1;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 3) {
                if (!this.moveItemStackTo(itemstack1, 3, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (itemstack1.is(Items.WHEAT)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemstack1.is(Items.BUCKET)) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
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
        return this.container.stillValid(player);
    }
}
