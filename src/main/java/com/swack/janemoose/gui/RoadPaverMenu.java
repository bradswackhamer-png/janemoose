package com.swack.janemoose.gui;

import com.swack.janemoose.entity.BaseRoadPaverEntity;
import com.swack.janemoose.entity.RoadPaverEntity;
import com.swack.janemoose.registry.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class RoadPaverMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 31;
    public static final int DATA_COUNT = 7;

    public static final int SLOT_PATTERN_LEFT = RoadPaverEntity.SLOT_PATTERN_LEFT;
    public static final int SLOT_PATTERN_CENTER = RoadPaverEntity.SLOT_PATTERN_CENTER;
    public static final int SLOT_PATTERN_RIGHT = RoadPaverEntity.SLOT_PATTERN_RIGHT;
    public static final int SLOT_FUEL = RoadPaverEntity.SLOT_FUEL;
    public static final int SLOT_MATERIAL_START = RoadPaverEntity.SLOT_MATERIAL_START;
    public static final int SLOT_MATERIAL_COUNT = RoadPaverEntity.SLOT_MATERIAL_COUNT;

    private final Container container;
    private final ContainerData data;
    private final RoadPaverEntity paver;

    // Client constructor
    public RoadPaverMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        this(containerId, playerInventory, findPaver(playerInventory, buf.readInt()), new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(DATA_COUNT));
    }

    // Server constructor
    public RoadPaverMenu(int containerId, Inventory playerInventory, RoadPaverEntity paver) {
        this(containerId, playerInventory, paver, paver, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> paver.getFuel();
                    case 1 -> paver.getFuelTotal();
                    case 2 -> paver.getRunMode();
                    case 3 -> paver.getCardinalLock();
                    case 4 -> paver.isWorkMode() ? 1 : 0;
                    case 5 -> paver.isWorkLightEnabled() ? 1 : 0;
                    case 6 -> paver.isHologramEnabled() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> paver.setFuel(value);
                    case 1 -> paver.setFuelTotal(value);
                    case 2 -> paver.setRunMode(value);
                    case 3 -> paver.setCardinalLock(value);
                    case 4 -> paver.setWorkMode(value != 0);
                    case 5 -> paver.setWorkLightEnabled(value != 0);
                    case 6 -> paver.setHologramEnabled(value != 0);
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        });
    }

    private RoadPaverMenu(int containerId, Inventory playerInventory, RoadPaverEntity paver, Container container, ContainerData data) {
        super(ModMenus.ROAD_PAVER_MENU.get(), containerId);
        checkContainerSize(container, CONTAINER_SIZE);
        checkContainerDataCount(data, DATA_COUNT);
        this.paver = paver;
        this.container = container;
        this.data = data;
        container.startOpen(playerInventory.player);

        // Pattern slots 0..2
        this.addSlot(new Slot(container, SLOT_PATTERN_LEFT, 30, 21) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BlockItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(container, SLOT_PATTERN_CENTER, 48, 21) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BlockItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(container, SLOT_PATTERN_RIGHT, 66, 21) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BlockItem;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        // Fuel Slot 3
        this.addSlot(new Slot(container, SLOT_FUEL, 8, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return paver != null ? paver.isFuelItem(stack) : true;
            }
        });

        // Slots 4..30: 27 Material Inventory Slots (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = SLOT_MATERIAL_START + col + row * 9;
                this.addSlot(new Slot(container, index, 8 + col * 18, 54 + row * 18));
            }
        }

        // Player Inventory (3 rows of 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 122 + row * 18));
            }
        }

        // Player Hotbar
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 180));
        }

        this.addDataSlots(data);
    }

    private static RoadPaverEntity findPaver(Inventory inv, int id) {
        Entity ent = inv.player.level().getEntity(id);
        return ent instanceof RoadPaverEntity r ? r : null;
    }

    public RoadPaverEntity getPaver() {
        return this.paver;
    }

    public int getFuel() {
        return this.data.get(0);
    }

    public int getFuelTotal() {
        return this.data.get(1);
    }

    public int getRunMode() {
        return this.data.get(2);
    }

    public int getCardinalLock() {
        return this.data.get(3);
    }

    public boolean isWorkMode() {
        return this.data.get(4) != 0;
    }

    public boolean isWorkLightEnabled() {
        return this.data.get(5) != 0;
    }

    public boolean isHologramEnabled() {
        return this.data.get(6) != 0;
    }

    public int getBurnProgressScaled(int pixels) {
        int total = this.getFuelTotal();
        if (total <= 0) total = 1600;
        int fuel = this.getFuel();
        return (fuel > 0) ? (fuel * pixels / total) : 0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            int nextMode = (this.getRunMode() == BaseRoadPaverEntity.MODE_FORWARD)
                    ? BaseRoadPaverEntity.MODE_STOPPED
                    : BaseRoadPaverEntity.MODE_FORWARD;
            if (this.container instanceof BaseRoadPaverEntity p) {
                if (nextMode != BaseRoadPaverEntity.MODE_STOPPED && p.isWorkMode() && !p.hasPaverMaterial()) {
                    p.notifyOutOfMaterial(player);
                    this.data.set(2, BaseRoadPaverEntity.MODE_STOPPED);
                    return true;
                }
            }
            this.data.set(2, nextMode);
            return true;
        } else if (id == 1) {
            int nextMode = (this.getRunMode() == BaseRoadPaverEntity.MODE_REVERSE)
                    ? BaseRoadPaverEntity.MODE_STOPPED
                    : BaseRoadPaverEntity.MODE_REVERSE;
            if (this.container instanceof BaseRoadPaverEntity p) {
                if (nextMode != BaseRoadPaverEntity.MODE_STOPPED && p.isWorkMode() && !p.hasPaverMaterial()) {
                    p.notifyOutOfMaterial(player);
                    this.data.set(2, BaseRoadPaverEntity.MODE_STOPPED);
                    return true;
                }
            }
            this.data.set(2, nextMode);
            return true;
        } else if (id == 2) {
            int current = this.getCardinalLock();
            int next = (current + 1) % 5;
            this.data.set(3, next);
            return true;
        } else if (id == 3) {
            this.data.set(4, this.isWorkMode() ? 0 : 1);
            return true;
        } else if (id == 4) {
            this.data.set(5, this.isWorkLightEnabled() ? 0 : 1);
            return true;
        } else if (id == 5) {
            this.data.set(6, this.isHologramEnabled() ? 0 : 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (index < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(slotStack, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.paver != null && this.paver.isFuelItem(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, SLOT_FUEL, SLOT_FUEL + 1, false)) {
                        if (!this.moveItemStackTo(slotStack, SLOT_MATERIAL_START, CONTAINER_SIZE, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (slotStack.getItem() instanceof BlockItem) {
                    boolean placedInPattern = false;
                    for (int patternSlot = SLOT_PATTERN_LEFT; patternSlot <= SLOT_PATTERN_RIGHT; patternSlot++) {
                        if (this.slots.get(patternSlot).getItem().isEmpty()) {
                            ItemStack single = slotStack.copyWithCount(1);
                            this.slots.get(patternSlot).set(single);
                            slotStack.shrink(1);
                            placedInPattern = true;
                            break;
                        }
                    }
                    if (!placedInPattern && !this.moveItemStackTo(slotStack, SLOT_MATERIAL_START, CONTAINER_SIZE, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(slotStack, SLOT_MATERIAL_START, CONTAINER_SIZE, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }
}
