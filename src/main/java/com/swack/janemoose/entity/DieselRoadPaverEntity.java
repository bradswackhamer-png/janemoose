package com.swack.janemoose.entity;

import com.swack.janemoose.gui.DieselRoadPaverMenu;
import com.swack.janemoose.item.DieselCanisterItem;
import com.swack.janemoose.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DieselRoadPaverEntity extends BaseRoadPaverEntity {
    public static final int MAX_DIESEL_FUEL = 10000;

    public static final int SLOT_PATTERN_FAR_LEFT = 0;
    public static final int SLOT_PATTERN_MID_LEFT = 1;
    public static final int SLOT_PATTERN_CENTER = 2;
    public static final int SLOT_PATTERN_MID_RIGHT = 3;
    public static final int SLOT_PATTERN_FAR_RIGHT = 4;
    public static final int SLOT_FUEL = 5;
    public static final int SLOT_MATERIAL_START = 6;

    public DieselRoadPaverEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.setFuel(2000);
        this.setFuelTotal(MAX_DIESEL_FUEL);
    }

    @Override
    public int getPavingWidth() {
        return 5;
    }

    @Override
    public int getFilterSlotCount() {
        return 5;
    }

    @Override
    public int getFuelSlot() {
        return SLOT_FUEL;
    }

    @Override
    public int getMaterialStartSlot() {
        return SLOT_MATERIAL_START;
    }

    @Override
    public boolean isDiesel() {
        return true;
    }

    @Override
    public Item getDropItem() {
        return ModItems.DIESEL_ROAD_PAVER.get();
    }

    @Override
    public boolean isFuelItem(ItemStack stack) {
        return JaneMooseTractorEntity.isFuel(stack);
    }

    @Override
    public void refuelFromItem(ItemStack stack, Player player) {
        if (this.getFuel() >= MAX_DIESEL_FUEL) {
            return;
        }

        int addAmount = (stack.getItem() instanceof DieselCanisterItem) ? DieselCanisterItem.FUEL_VALUE : 1000;
        int newFuel = Math.min(this.getFuel() + addAmount, MAX_DIESEL_FUEL);
        this.setFuel(newFuel);
        this.setFuelTotal(MAX_DIESEL_FUEL);

        if (!player.isCreative()) {
            ItemStack remainder = JaneMooseTractorEntity.getEmptyContainer(stack);
            stack.shrink(1);
            if (!remainder.isEmpty()) {
                if (stack.isEmpty()) {
                    player.setItemInHand(player.getUsedItemHand(), remainder);
                } else if (!player.getInventory().add(remainder)) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        this.spawnAtLocation(serverLevel, remainder);
                    }
                }
            }
        }

        this.level().playSound(null, this.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.sendOverlayMessage(Component.translatable("message.janemoose.fueled", this.getFuel(), MAX_DIESEL_FUEL));
    }

    @Override
    public void tick() {
        super.tick();

        // Process fuel item placed in GUI slot 5 on server (whether running or idle)
        if (!this.level().isClientSide() && this.getFuel() <= MAX_DIESEL_FUEL - 1000) {
            ItemStack fuelStack = this.getItem(SLOT_FUEL);
            if (!fuelStack.isEmpty() && isFuelItem(fuelStack)) {
                int addAmount = (fuelStack.getItem() instanceof DieselCanisterItem) ? DieselCanisterItem.FUEL_VALUE : 1000;
                this.setFuel(Math.min(this.getFuel() + addAmount, MAX_DIESEL_FUEL));
                this.setFuelTotal(MAX_DIESEL_FUEL);

                ItemStack remainder = JaneMooseTractorEntity.getEmptyContainer(fuelStack);
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    this.setItem(SLOT_FUEL, remainder);
                } else if (!remainder.isEmpty()) {
                    ItemStack leftover = this.insertIntoMaterialInventory(remainder);
                    if (!leftover.isEmpty() && this.level() instanceof ServerLevel serverLevel) {
                        this.spawnAtLocation(serverLevel, leftover);
                    }
                }
                this.level().playSound(null, this.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public boolean consumeFuelTick() {
        int curFuel = this.getFuel();
        if (curFuel > 0) {
            this.setFuel(curFuel - 1);
            return true;
        }
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new DieselRoadPaverMenu(containerId, playerInventory, this);
    }
}
