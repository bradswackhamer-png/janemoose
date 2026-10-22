package com.swack.janemoose.entity;

import com.swack.janemoose.gui.AttachmentMenu;
import com.swack.janemoose.item.IAttachment;
import com.swack.janemoose.item.WrenchItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IMenuProviderExtension;

public class JaneMooseAttachmentEntity extends Entity {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM =
            SynchedEntityData.defineId(JaneMooseAttachmentEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_LOWERED =
            SynchedEntityData.defineId(JaneMooseAttachmentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ACTIVE =
            SynchedEntityData.defineId(JaneMooseAttachmentEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_TRACTOR_ID =
            SynchedEntityData.defineId(JaneMooseAttachmentEntity.class, EntityDataSerializers.INT);

    // Internal inventory for seeds (planter) or harvested crops (harvester)
    private final SimpleContainer inventory = new SimpleContainer(27);

    public JaneMooseAttachmentEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ITEM, ItemStack.EMPTY);
        builder.define(DATA_LOWERED, true);
        builder.define(DATA_ACTIVE, true);
        builder.define(DATA_TRACTOR_ID, -1);
    }

    public ItemStack getItem() {
        return this.entityData.get(DATA_ITEM);
    }

    public void setItem(ItemStack stack) {
        this.entityData.set(DATA_ITEM, stack);
    }

    public boolean isLowered() {
        return this.entityData.get(DATA_LOWERED);
    }

    public void setLowered(boolean lowered) {
        this.entityData.set(DATA_LOWERED, lowered);
    }

    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    public void setActive(boolean active) {
        this.entityData.set(DATA_ACTIVE, active);
    }

    public int getTractorId() {
        return this.entityData.get(DATA_TRACTOR_ID);
    }

    public void setTractorId(int id) {
        this.entityData.set(DATA_TRACTOR_ID, id);
    }

    public boolean isHitched() {
        return getTractorId() != -1;
    }

    public boolean hasInventory() {
        ItemStack item = getItem();
        if (item.getItem() instanceof IAttachment att) {
            return att.getType() == IAttachment.AttachmentType.PLANTER 
                || att.getType() == IAttachment.AttachmentType.HARVESTER;
        }
        return false;
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public int getSlotCount() {
        ItemStack item = getItem();
        if (item.getItem() instanceof IAttachment att) {
            int width = att.getWidth();
            if (width == 1) return 9;
            if (width == 2) return 18;
            return 27;
        }
        return 27;
    }

    public JaneMooseTractorEntity getHitchedTractor() {
        if (!isHitched()) return null;
        Entity entity = this.level().getEntity(getTractorId());
        return entity instanceof JaneMooseTractorEntity tractor ? tractor : null;
    }

    public void unhitch() {
        JaneMooseTractorEntity tractor = getHitchedTractor();
        if (tractor != null && tractor.getAttachmentEntityId() == this.getId()) {
            tractor.setAttachmentEntityId(-1);
        }
        setTractorId(-1);
        this.setNoGravity(false);
    }

    @Override
    public boolean isPushable() {
        return !isHitched();
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public boolean canBeCollidedWith(Entity entity) {
        return this.isAlive();
    }

    public void openCustomGui(ServerPlayer player) {
        if (hasInventory()) {
            player.openMenu(new AttachmentMenuProvider(this));
        }
    }

    private record AttachmentMenuProvider(JaneMooseAttachmentEntity attachment) implements MenuProvider, IMenuProviderExtension {
        @Override
        public Component getDisplayName() {
            ItemStack stack = attachment.getItem();
            if (stack.getItem() instanceof IAttachment att) {
                return switch (att.getType()) {
                    case PLANTER -> Component.translatable("gui.janemoose.seed_hopper");
                    case HARVESTER -> Component.translatable("gui.janemoose.grain_tank");
                    case CULTIVATOR -> Component.translatable("gui.janemoose.tool_chest");
                };
            }
            return Component.translatable("gui.janemoose.attachment_inventory");
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new AttachmentMenu(containerId, playerInventory, attachment);
        }

        @Override
        public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buf) {
            buf.writeInt(attachment.getId());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack held = player.getItemInHand(hand);

        // Sneak interaction
        if (player.isSecondaryUseActive()) {
            // Wrench on sneak -> unhitch if hitched, or retrieve item if unhitched
            if (held.getItem() instanceof WrenchItem) {
                if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                    if (isHitched()) {
                        unhitch();
                        player.sendOverlayMessage(Component.translatable("message.janemoose.unhitched"));
                        this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    } else {
                        dropAsItem(serverLevel);
                    }
                }
                return InteractionResult.SUCCESS;
            }

            // Sneak without wrench -> toggle LIFT / LOWER (Hydraulics)
            if (!this.level().isClientSide()) {
                setLowered(!isLowered());
                this.level().playSound(null, this.blockPosition(), isLowered() ? SoundEvents.PISTON_CONTRACT : SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.7F, 1.2F);
                player.sendOverlayMessage(Component.translatable(isLowered() ? "message.janemoose.lowered" : "message.janemoose.lifted"));
            }
            return InteractionResult.SUCCESS;
        }

        // Regular right-click on foot:
        // If Planter or Harvester -> Open Inventory
        if (hasInventory()) {
            if (player instanceof ServerPlayer serverPlayer) {
                openCustomGui(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }

        // Cultivator (no inventory) -> Toggle ON / OFF
        if (!this.level().isClientSide()) {
            setActive(!isActive());
            this.level().playSound(null, this.blockPosition(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.8F, isActive() ? 1.2F : 0.8F);
            player.sendOverlayMessage(Component.translatable(isActive() ? "message.janemoose.active" : "message.janemoose.inactive"));
        }
        return InteractionResult.SUCCESS;
    }

    public void dropAsItem(ServerLevel serverLevel) {
        unhitch();
        // Drop stored crops/seeds onto ground if it had any
        if (hasInventory()) {
            Containers.dropContents(serverLevel, this, this.inventory);
        }
        ItemStack stack = getItem();
        if (!stack.isEmpty()) {
            this.spawnAtLocation(serverLevel, stack);
        }
        this.discard();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (!this.isRemoved()) {
            if (source.getEntity() instanceof Player player && player.isCreative()) {
                unhitch();
                this.discard();
                return true;
            }
            dropAsItem(level);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        JaneMooseTractorEntity tractor = getHitchedTractor();
        if (tractor != null && tractor.isAlive()) {
            // Disable gravity and vertical bouncing: stay locked to tractor hitch height
            this.setNoGravity(true);

            float yawRad = (float) Math.toRadians(tractor.getYRot());
            double rearDist = 1.45;
            double targetX = tractor.getX() + Math.sin(yawRad) * rearDist;
            double targetZ = tractor.getZ() - Math.cos(yawRad) * rearDist;
            double targetY = tractor.getY(); // Exact ground level as tractor - no mid-air floating!

            double distToTargetSq = (this.getX() - targetX) * (this.getX() - targetX) + (this.getZ() - targetZ) * (this.getZ() - targetZ);
            this.setPos(targetX, targetY, targetZ);
            if (distToTargetSq > 4.0 || this.tickCount <= 2) {
                this.xo = targetX;
                this.yo = targetY;
                this.zo = targetZ;
            }
            this.setYRot(tractor.getYRot());
            this.yRotO = tractor.getYRot();
            this.setDeltaMovement(Vec3.ZERO);

            // Active operating particles ONLY when lowered, active, and moving
            if (this.level().isClientSide() && isLowered() && isActive() && tractor.isMoving()) {
                if (this.random.nextFloat() < 0.25F) {
                    this.level().addParticle(ParticleTypes.CRIT, this.getX(), this.getY() + 0.2, this.getZ(), 0.0, 0.05, 0.0);
                }
            }
        } else {
            this.setNoGravity(false);
            if (isHitched()) {
                unhitch();
            }
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.98, 0.8));
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        input.read("Item", ItemStack.CODEC).ifPresent(this::setItem);
        setLowered(input.getBooleanOr("Lowered", true));
        setActive(input.getBooleanOr("Active", true));
        setTractorId(input.getIntOr("TractorId", -1));
        if (hasInventory()) {
            ContainerHelper.loadAllItems(input, this.inventory.getItems());
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        ItemStack item = getItem();
        if (!item.isEmpty()) {
            output.store("Item", ItemStack.CODEC, item);
        }
        output.putBoolean("Lowered", isLowered());
        output.putBoolean("Active", isActive());
        output.putInt("TractorId", getTractorId());
        if (hasInventory()) {
            ContainerHelper.saveAllItems(output, this.inventory.getItems());
        }
    }
}
