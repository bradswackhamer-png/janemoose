package com.swack.janemoose.block.entity;

import com.swack.janemoose.block.FermentationBarrelBlock;
import com.swack.janemoose.gui.FermentationBarrelMenu;
import com.swack.janemoose.registry.ModBlockEntities;
import com.swack.janemoose.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class FermentationBarrelBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 3; // 0: Wheat Input, 1: Empty Bucket Input, 2: Ethanol Bucket Output
    public static final int TANK_CAPACITY = 8000; // 8 Buckets (8,000 mB)
    public static final int PROCESS_TIME = 1200; // 1,200 ticks = 60 seconds (1 real-life minute) per 1,000 mB bucket

    private int activeRemaining = 0;
    private int progress = 0;
    private int ethanolAmount = 0;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME;
                case 2 -> ethanolAmount;
                case 3 -> TANK_CAPACITY;
                case 4 -> activeRemaining > 0 ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> ethanolAmount = value;
                case 4 -> activeRemaining = value > 0 ? 1 : 0;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTATION_BARREL_BE.get(), pos, state);
    }

    public int getEthanolAmount() {
        return this.ethanolAmount;
    }

    public void setEthanolAmount(int amount) {
        this.ethanolAmount = Math.clamp(amount, 0, TANK_CAPACITY);
        setChanged();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) return;

        boolean wasActive = state.getValue(FermentationBarrelBlock.ACTIVE);
        boolean dirty = false;

        // 1. Start new 1-minute fermentation cycle if idle and Wheat is available
        if (this.activeRemaining <= 0) {
            ItemStack wheatSlot = this.items.get(0);
            if (!wheatSlot.isEmpty() && wheatSlot.is(Items.WHEAT) && this.ethanolAmount < TANK_CAPACITY) {
                wheatSlot.shrink(1);
                this.activeRemaining = PROCESS_TIME;
                this.progress = 0;
                dirty = true;
            } else {
                this.progress = 0;
            }
        }

        // 2. Process active fermentation: 1,000 mB per 1,200 ticks (+5 mB every 6 ticks = 1,000 mB / 60s)
        if (this.activeRemaining > 0) {
            if (this.ethanolAmount < TANK_CAPACITY) {
                this.activeRemaining--;
                this.progress = PROCESS_TIME - this.activeRemaining;

                if (this.activeRemaining % 6 == 0) {
                    this.ethanolAmount = Math.min(TANK_CAPACITY, this.ethanolAmount + 5);
                }

                if (this.activeRemaining == 0) {
                    this.progress = 0;
                }
                dirty = true;
            }
        }

        // 3. Auto-fill Empty Buckets in Slot 1 -> Ethanol Bucket in Slot 2
        if (this.ethanolAmount >= 1000) {
            ItemStack bucketIn = this.items.get(1);
            ItemStack bucketOut = this.items.get(2);
            if (!bucketIn.isEmpty() && bucketIn.is(Items.BUCKET)) {
                ItemStack ethanolBucket = new ItemStack(ModItems.ETHANOL_BUCKET.get());
                if (bucketOut.isEmpty()) {
                    bucketIn.shrink(1);
                    this.items.set(2, ethanolBucket);
                    this.ethanolAmount -= 1000;
                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.8f, 1.0f);
                    dirty = true;
                } else if (ItemStack.isSameItemSameComponents(bucketOut, ethanolBucket) && bucketOut.getCount() < bucketOut.getMaxStackSize()) {
                    bucketIn.shrink(1);
                    bucketOut.grow(1);
                    this.ethanolAmount -= 1000;
                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 0.8f, 1.0f);
                    dirty = true;
                }
            }
        }

        boolean isActive = this.activeRemaining > 0 && this.ethanolAmount < TANK_CAPACITY;
        if (wasActive != isActive) {
            level.setBlock(pos, state.setValue(FermentationBarrelBlock.ACTIVE, isActive), 3);
            dirty = true;
        }

        if (dirty) {
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("ActiveRemaining", this.activeRemaining);
        output.putInt("Progress", this.progress);
        output.putInt("Ethanol", this.ethanolAmount);
        ContainerHelper.saveAllItems(output, this.items);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.activeRemaining = input.getIntOr("ActiveRemaining", 0);
        this.progress = input.getIntOr("Progress", 0);
        this.ethanolAmount = input.getIntOr("Ethanol", 0);
        ContainerHelper.loadAllItems(input, this.items);
    }

    // === WorldlyContainer Implementation ===
    private static final int[] SLOTS_UP = new int[]{0};
    private static final int[] SLOTS_SIDES = new int[]{0, 1};
    private static final int[] SLOTS_DOWN = new int[]{2};

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.UP) return SLOTS_UP;
        if (side == Direction.DOWN) return SLOTS_DOWN;
        return SLOTS_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (slot == 0) return stack.is(Items.WHEAT);
        if (slot == 1) return stack.is(Items.BUCKET);
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot == 2;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack res = ContainerHelper.removeItem(this.items, slot, amount);
        if (!res.isEmpty()) setChanged();
        return res;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(this.items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.items.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.janemoose.fermentation_barrel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FermentationBarrelMenu(containerId, playerInventory, this, this.dataAccess);
    }
}
