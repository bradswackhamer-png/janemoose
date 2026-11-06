package com.swack.janemoose.entity;

import com.swack.janemoose.gui.RoadPaverMenu;
import com.swack.janemoose.registry.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class RoadPaverEntity extends BaseRoadPaverEntity {
    public static final int SLOT_PATTERN_LEFT = 0;
    public static final int SLOT_PATTERN_CENTER = 1;
    public static final int SLOT_PATTERN_RIGHT = 2;
    public static final int SLOT_FUEL = 3;
    public static final int SLOT_MATERIAL_START = 4;

    public RoadPaverEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int getPavingWidth() {
        return 3;
    }

    @Override
    public int getFilterSlotCount() {
        return 3;
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
        return false;
    }

    @Override
    public Item getDropItem() {
        return ModItems.ROAD_PAVER.get();
    }

    @Override
    public boolean isFuelItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.is(ItemTags.COALS) || stack.is(Items.COAL_BLOCK)) return true;
        if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.PLANKS)) return true;
        if (stack.is(Items.LAVA_BUCKET) || stack.is(Items.BLAZE_ROD)) return true;
        return stack.has(net.minecraft.core.component.DataComponents.COOKING_FUEL);
    }

    public int getItemBurnDuration(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        if (stack.is(Items.COAL_BLOCK)) return 14400;
        if (stack.is(ItemTags.COALS)) return 1600;
        if (stack.is(Items.BLAZE_ROD)) return 2400;
        if (stack.is(Items.LAVA_BUCKET)) return 20000;
        if (stack.is(ItemTags.LOGS) || stack.is(ItemTags.PLANKS)) return 300;

        var fuelComp = stack.get(net.minecraft.core.component.DataComponents.COOKING_FUEL);
        if (fuelComp != null) {
            try {
                if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    var params = new net.minecraft.world.level.storage.loot.LootParams.Builder(serverLevel)
                            .create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.EMPTY);
                    var ctx = new net.minecraft.world.level.storage.loot.LootContext.Builder(params).create(java.util.Optional.empty());
                    int b = fuelComp.burnTime().get(ctx, 0);
                    if (b > 0) return b;
                }
            } catch (Throwable ignored) {}
            return 1600;
        }
        return 0;
    }

    @Override
    public void refuelFromItem(ItemStack stack, Player player) {
        ItemStack currentFuelSlot = this.getItem(SLOT_FUEL);
        if (currentFuelSlot.isEmpty()) {
            ItemStack toInsert = stack.copy();
            toInsert.setCount(1);
            this.setItem(SLOT_FUEL, toInsert);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this.blockPosition(), SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else if (ItemStack.isSameItemSameComponents(currentFuelSlot, stack) && currentFuelSlot.getCount() < currentFuelSlot.getMaxStackSize()) {
            currentFuelSlot.grow(1);
            if (!player.isCreative()) {
                stack.shrink(1);
            }
            this.level().playSound(null, this.blockPosition(), SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public boolean consumeFuelTick() {
        int curFuel = this.getFuel();
        if (curFuel <= 0) {
            ItemStack fuelStack = this.getItem(SLOT_FUEL);
            if (!fuelStack.isEmpty() && isFuelItem(fuelStack)) {
                int duration = getItemBurnDuration(fuelStack);
                if (duration > 0) {
                    var remainderTemplate = fuelStack.getItem().getCraftingRemainder();
                    ItemStack remainder = remainderTemplate != null ? remainderTemplate.create() : ItemStack.EMPTY;
                    fuelStack.shrink(1);
                    if (fuelStack.isEmpty()) {
                        this.setItem(SLOT_FUEL, remainder);
                    }
                    this.setFuel(duration);
                    this.setFuelTotal(duration);
                    curFuel = duration;
                }
            }
        }

        if (curFuel > 0) {
            this.setFuel(curFuel - 1);
            return true;
        }
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new RoadPaverMenu(containerId, playerInventory, this);
    }
}
