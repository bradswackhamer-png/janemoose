package com.swack.janemoose.gui;

import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.item.DieselCanisterItem;
import com.swack.janemoose.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TractorMenu extends AbstractContainerMenu {
    private final JaneMooseTractorEntity tractor;
    private final Container container;
    private final ContainerData data;

    // Client-side constructor
    public TractorMenu(int containerId, Inventory playerInv, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInv, findTractor(playerInv, buf.readInt()), new SimpleContainer(2), new SimpleContainerData(6));
    }

    // Server-side constructor
    public TractorMenu(int containerId, Inventory playerInv, JaneMooseTractorEntity tractor) {
        this(containerId, playerInv, tractor, new SimpleContainer(2), new ContainerData() {
            @Override
            public int get(int index) {
                JaneMooseAttachmentEntity att = tractor.getHitchedAttachment();
                return switch (index) {
                    case 0 -> tractor.getFuel();
                    case 1 -> JaneMooseTractorEntity.MAX_FUEL;
                    case 2 -> att != null ? 1 : 0;
                    case 3 -> (att != null && att.isLowered()) ? 1 : 0;
                    case 4 -> (att != null && att.isActive()) ? 1 : 0;
                    case 5 -> (att != null && att.hasInventory()) ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    tractor.setFuel(value);
                }
            }

            @Override
            public int getCount() {
                return 6;
            }
        });
    }

    private TractorMenu(int containerId, Inventory playerInv, JaneMooseTractorEntity tractor, Container container, ContainerData data) {
        super(ModMenus.TRACTOR_MENU.get(), containerId);
        this.tractor = tractor;
        this.container = container;
        this.data = data;

        checkContainerSize(container, 2);
        checkContainerDataCount(data, 6);

        // Slot 0: Fuel Input (Diesel, Ethanol, Biofuel)
        this.addSlot(new Slot(container, 0, 80, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return JaneMooseTractorEntity.isFuel(stack);
            }
        });

        // Slot 1: Output / Empty container
        this.addSlot(new Slot(container, 1, 134, 52) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Player Inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 126 + row * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 184));
        }

        this.addDataSlots(data);
    }

    private static JaneMooseTractorEntity findTractor(Inventory inv, int id) {
        Entity entity = inv.player.level().getEntity(id);
        if (entity instanceof JaneMooseTractorEntity t) {
            return t;
        }
        return null;
    }

    public JaneMooseTractorEntity getTractor() {
        return this.tractor;
    }

    public int getFuel() {
        return this.data.get(0);
    }

    public int getMaxFuel() {
        return this.data.get(1);
    }

    public boolean isHitched() {
        return this.data.get(2) == 1;
    }

    public boolean isLowered() {
        return this.data.get(3) == 1;
    }

    public boolean isActive() {
        return this.data.get(4) == 1;
    }

    public boolean hasInventory() {
        return this.data.get(5) == 1;
    }

    public boolean areHeadlightsOn() {
        return this.tractor != null && this.tractor.areHeadlightsOn();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // Process fueling on server
        if (this.tractor != null && !this.tractor.level().isClientSide()) {
            ItemStack input = this.container.getItem(0);
            if (!input.isEmpty() && this.tractor.getFuel() < JaneMooseTractorEntity.MAX_FUEL) {
                ItemStack output = this.container.getItem(1);

                if (input.getItem() instanceof DieselCanisterItem) {
                    if (output.isEmpty() || (output.is(input.getItem()) && output.getCount() < output.getMaxStackSize())) {
                        this.tractor.setFuel(this.tractor.getFuel() + DieselCanisterItem.FUEL_VALUE);
                        input.shrink(1);
                        this.tractor.level().playSound(null, this.tractor.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                } else if (JaneMooseTractorEntity.isFuel(input)) {
                    ItemStack returnItem = JaneMooseTractorEntity.getEmptyContainer(input);

                    if (returnItem.isEmpty() || output.isEmpty() || (ItemStack.isSameItemSameComponents(output, returnItem) && output.getCount() < output.getMaxStackSize())) {
                        this.tractor.setFuel(this.tractor.getFuel() + 1000);
                        input.shrink(1);
                        if (!returnItem.isEmpty()) {
                            if (output.isEmpty()) {
                                this.container.setItem(1, returnItem);
                            } else {
                                output.grow(1);
                            }
                        }
                        this.tractor.level().playSound(null, this.tractor.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < 2) {
                if (!this.moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
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
        return this.tractor != null && this.tractor.isAlive() && player.distanceToSqr(this.tractor) < 64.0;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.container);
    }
}
