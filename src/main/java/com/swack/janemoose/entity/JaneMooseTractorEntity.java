package com.swack.janemoose.entity;

import com.swack.janemoose.gui.TractorMenu;
import com.swack.janemoose.item.DieselCanisterItem;
import com.swack.janemoose.item.IAttachment;
import com.swack.janemoose.item.WrenchItem;
import com.swack.janemoose.registry.ModEntities;
import com.swack.janemoose.registry.ModItems;
import net.minecraft.core.BlockPos;
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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IMenuProviderExtension;

import java.util.List;

public class JaneMooseTractorEntity extends LivingEntity implements PlayerRideableJumping {
    public static final int MAX_FUEL = 10000;

    private static final EntityDataAccessor<Integer> DATA_FUEL =
            SynchedEntityData.defineId(JaneMooseTractorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> DATA_ATTACHMENT =
            SynchedEntityData.defineId(JaneMooseTractorEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Boolean> DATA_ENGINE_RUNNING =
            SynchedEntityData.defineId(JaneMooseTractorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ATTACHMENT_ID =
            SynchedEntityData.defineId(JaneMooseTractorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HEADLIGHTS =
            SynchedEntityData.defineId(JaneMooseTractorEntity.class, EntityDataSerializers.BOOLEAN);

    private int operationCooldown = 0;
    private BlockPos lastLightPos = null;

    // Movement tracking on both client and server
    private double prevCoordX = Double.NaN;
    private double prevCoordZ = Double.NaN;
    private boolean isPhysicallyMoving = false;

    // Wheel rotation tracking - accumulated only when physically moving
    private float wheelAngle = 0.0F;
    private float prevWheelAngle = 0.0F;

    public JaneMooseTractorEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.11)
                .add(Attributes.STEP_HEIGHT, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FUEL, 2000);
        builder.define(DATA_ATTACHMENT, ItemStack.EMPTY);
        builder.define(DATA_ENGINE_RUNNING, false);
        builder.define(DATA_ATTACHMENT_ID, -1);
        builder.define(DATA_HEADLIGHTS, false);
    }

    public boolean areHeadlightsOn() {
        return this.entityData.get(DATA_HEADLIGHTS);
    }

    public void setHeadlightsOn(boolean on) {
        this.entityData.set(DATA_HEADLIGHTS, on);
        if (!on && !this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            removeLightBlock(serverLevel);
        }
    }

    public void toggleHeadlights(Player player) {
        boolean newState = !areHeadlightsOn();
        setHeadlightsOn(newState);
        this.level().playSound(null, this.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.8F, newState ? 1.4F : 0.9F);
        if (player != null) {
            player.sendOverlayMessage(Component.translatable(newState ? "message.janemoose.lights_on" : "message.janemoose.lights_off"));
        }
    }

    public int getFuel() {
        return this.entityData.get(DATA_FUEL);
    }

    public void setFuel(int fuel) {
        this.entityData.set(DATA_FUEL, Mth.clamp(fuel, 0, MAX_FUEL));
    }

    public ItemStack getAttachment() {
        return this.entityData.get(DATA_ATTACHMENT);
    }

    public void setAttachment(ItemStack stack) {
        this.entityData.set(DATA_ATTACHMENT, stack);
    }

    public boolean isEngineRunning() {
        return this.entityData.get(DATA_ENGINE_RUNNING);
    }

    public int getAttachmentEntityId() {
        return this.entityData.get(DATA_ATTACHMENT_ID);
    }

    public void setAttachmentEntityId(int id) {
        this.entityData.set(DATA_ATTACHMENT_ID, id);
    }

    public JaneMooseAttachmentEntity getHitchedAttachment() {
        int id = getAttachmentEntityId();
        if (id == -1) return null;
        Entity ent = this.level().getEntity(id);
        if (ent instanceof JaneMooseAttachmentEntity att && att.isAlive()) {
            return att;
        }
        return null;
    }

    public boolean hasHitchedAttachment() {
        return getHitchedAttachment() != null;
    }

    public void hitchAttachment(JaneMooseAttachmentEntity att) {
        setAttachmentEntityId(att.getId());
        att.setTractorId(this.getId());
        this.level().playSound(null, this.blockPosition(), SoundEvents.ANVIL_PLACE, SoundSource.PLAYERS, 0.7F, 1.3F);
        LivingEntity rider = this.getControllingPassenger();
        if (rider instanceof Player player) {
            player.sendOverlayMessage(Component.translatable("message.janemoose.hitched", att.getItem().getHoverName()));
        }
    }

    public void unhitchAttachment() {
        JaneMooseAttachmentEntity att = getHitchedAttachment();
        if (att != null) {
            att.unhitch();
        }
        setAttachmentEntityId(-1);
    }

    public void toggleAttachmentLift(Player player) {
        JaneMooseAttachmentEntity att = getHitchedAttachment();
        if (att != null) {
            att.setLowered(!att.isLowered());
            this.level().playSound(null, this.blockPosition(), att.isLowered() ? SoundEvents.PISTON_CONTRACT : SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.8F, 1.2F);
            player.sendOverlayMessage(Component.translatable(att.isLowered() ? "message.janemoose.lowered" : "message.janemoose.lifted"));
        } else {
            player.sendOverlayMessage(Component.translatable("message.janemoose.none_hitched"));
        }
    }

    public void toggleAttachmentActive(Player player) {
        JaneMooseAttachmentEntity att = getHitchedAttachment();
        if (att != null) {
            att.setActive(!att.isActive());
            this.level().playSound(null, this.blockPosition(), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.8F, att.isActive() ? 1.2F : 0.8F);
            player.sendOverlayMessage(Component.translatable(att.isActive() ? "message.janemoose.active" : "message.janemoose.inactive"));
        } else {
            player.sendOverlayMessage(Component.translatable("message.janemoose.none_hitched"));
        }
    }

    public void openCustomGui(ServerPlayer player) {
        player.openMenu(new TractorMenuProvider(this));
    }

    private record TractorMenuProvider(JaneMooseTractorEntity tractor) implements MenuProvider, IMenuProviderExtension {
        @Override
        public Component getDisplayName() {
            return tractor.hasCustomName() ? tractor.getCustomName() : Component.translatable("gui.janemoose.tractor_dashboard");
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new TractorMenu(containerId, playerInventory, tractor);
        }

        @Override
        public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buf) {
            buf.writeInt(tractor.getId());
        }
    }

    public float getWheelRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevWheelAngle, this.wheelAngle);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        LivingEntity rider = this.getControllingPassenger();
        if (rider instanceof Player player) {
            toggleAttachmentLift(player);
        }
    }

    @Override
    public boolean canJump() {
        return hasHitchedAttachment();
    }

    @Override
    public void handleStartJump(int jumpPower) {}

    @Override
    public void handleStopJump() {}

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack heldItem = player.getItemInHand(hand);

        // Sneaking interaction
        if (player.isSecondaryUseActive()) {
            // Wrench on sneak -> pick up tractor
            if (heldItem.getItem() instanceof WrenchItem) {
                if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                    unhitchAttachment();
                    dropAttachment(serverLevel);
                    ItemStack tractorStack = new ItemStack(ModItems.TRACTOR.get());
                    this.spawnAtLocation(serverLevel, tractorStack);
                    this.discard();
                }
                return InteractionResult.SUCCESS;
            }

            // Unhitch attachment if hitched
            if (hasHitchedAttachment()) {
                if (!this.level().isClientSide()) {
                    unhitchAttachment();
                    this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.sendOverlayMessage(Component.translatable("message.janemoose.unhitched"));
                }
                return InteractionResult.SUCCESS;
            }

            // Sneak with empty hand or other tool -> open Dashboard GUI
            if (player instanceof ServerPlayer serverPlayer) {
                openCustomGui(serverPlayer);
                return InteractionResult.SUCCESS;
            }
        }

        // If player is already riding this tractor: right click opens GUI or toggles ON/OFF
        if (this.hasPassenger(player)) {
            if (player instanceof ServerPlayer serverPlayer && heldItem.isEmpty()) {
                openCustomGui(serverPlayer);
                return InteractionResult.SUCCESS;
            }
        }

        // Refueling with Diesel Canister
        if (heldItem.getItem() instanceof DieselCanisterItem) {
            if (getFuel() < MAX_FUEL) {
                if (!this.level().isClientSide()) {
                    setFuel(getFuel() + DieselCanisterItem.FUEL_VALUE);
                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                    this.level().playSound(null, this.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.sendOverlayMessage(Component.translatable("message.janemoose.fueled", getFuel(), MAX_FUEL));
                }
                return InteractionResult.SUCCESS;
            }
        }

        // Generic Diesel Fuel Bucket / item from another mod
        if (isDieselFuel(heldItem)) {
            if (getFuel() < MAX_FUEL) {
                if (!this.level().isClientSide()) {
                    setFuel(getFuel() + 1000);
                    if (!player.isCreative()) {
                        if (heldItem.is(Items.BUCKET) || heldItem.getItem().toString().toLowerCase().contains("bucket")) {
                            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                        } else {
                            heldItem.shrink(1);
                        }
                    }
                    this.level().playSound(null, this.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.sendOverlayMessage(Component.translatable("message.janemoose.fueled", getFuel(), MAX_FUEL));
                }
                return InteractionResult.SUCCESS;
            }
        }

        // Equipping an attachment by right clicking tractor with attachment item
        if (heldItem.getItem() instanceof IAttachment) {
            if (!hasHitchedAttachment()) {
                if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                    JaneMooseAttachmentEntity attEntity = new JaneMooseAttachmentEntity(ModEntities.ATTACHMENT.get(), serverLevel);
                    ItemStack toAttach = heldItem.copy();
                    toAttach.setCount(1);
                    attEntity.setItem(toAttach);

                    float yawRad = (float) Math.toRadians(this.getYRot());
                    double rearDist = 1.45;
                    attEntity.setPos(this.getX() + Math.sin(yawRad) * rearDist, this.getY(), this.getZ() - Math.cos(yawRad) * rearDist);
                    attEntity.setYRot(this.getYRot());

                    serverLevel.addFreshEntity(attEntity);
                    hitchAttachment(attEntity);

                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }

        // Inspecting with Wrench -> Open GUI or inspect status
        if (heldItem.getItem() instanceof WrenchItem) {
            if (player instanceof ServerPlayer serverPlayer) {
                openCustomGui(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }

        // Mount the tractor
        if (!this.level().isClientSide()) {
            player.startRiding(this);
        }
        return InteractionResult.SUCCESS;
    }

    private boolean isDieselFuel(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String name = stack.getItem().toString().toLowerCase();
        return name.contains("diesel");
    }

    private void dropAttachment(ServerLevel serverLevel) {
        ItemStack att = getAttachment();
        if (!att.isEmpty()) {
            this.spawnAtLocation(serverLevel, att);
            setAttachment(ItemStack.EMPTY);
        }
    }

    public boolean isMoving() {
        return this.isPhysicallyMoving;
    }

    @Override
    public void tick() {
        super.tick();

        // Calculate actual displacement per tick (works for client-steered entities on server)
        double dx = Double.isNaN(this.prevCoordX) ? 0.0 : this.getX() - this.prevCoordX;
        double dz = Double.isNaN(this.prevCoordZ) ? 0.0 : this.getZ() - this.prevCoordZ;
        double distMovedSq = dx * dx + dz * dz;
        this.prevCoordX = this.getX();
        this.prevCoordZ = this.getZ();

        LivingEntity rider = this.getControllingPassenger();
        boolean hasRiderInput = (rider instanceof Player p) && (Math.abs(p.zza) > 0.001F || Math.abs(p.xxa) > 0.001F);
        this.isPhysicallyMoving = (distMovedSq > 0.0001) || (hasRiderInput && distMovedSq > 0.000001);

        // Update wheel rotation strictly based on actual movement along heading
        this.prevWheelAngle = this.wheelAngle;
        if (distMovedSq > 0.0001) {
            float yawRad = (float) Math.toRadians(this.getYRot());
            double headingX = -Math.sin(yawRad);
            double headingZ = Math.cos(yawRad);
            double forwardDist = dx * headingX + dz * headingZ;
            this.wheelAngle += (float) (forwardDist * 2.8);
        }

        // Smoke particles when engine is running
        if (this.level().isClientSide() && isEngineRunning() && this.random.nextFloat() < 0.35F) {
            float yawRad = (float) Math.toRadians(this.getYRot());
            double exhaustX = this.getX() + Math.cos(yawRad) * 0.22 - Math.sin(yawRad) * 0.9;
            double exhaustZ = this.getZ() + Math.sin(yawRad) * 0.22 + Math.cos(yawRad) * 0.9;
            this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, exhaustX, this.getY() + 1.5, exhaustZ, 0.0, 0.06, 0.0);
        }

        // Server-side attachment, hitching & fuel handling
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            this.entityData.set(DATA_ENGINE_RUNNING, rider != null && getFuel() > 0);

            // Headlight real-world illumination handling
            if (areHeadlightsOn()) {
                updateHeadlightIllumination(serverLevel);
            } else if (this.lastLightPos != null) {
                removeLightBlock(serverLevel);
            }

            JaneMooseAttachmentEntity hitched = getHitchedAttachment();

            // Auto-hitch when backing up near an unhitched attachment entity
            if (hitched == null) {
                setAttachmentEntityId(-1);
                float yawRad = (float) Math.toRadians(this.getYRot());
                double rearDist = 1.45;
                double hitchX = this.getX() + Math.sin(yawRad) * rearDist;
                double hitchY = this.getY();
                double hitchZ = this.getZ() - Math.cos(yawRad) * rearDist;

                AABB searchBox = new AABB(hitchX - 1.2, hitchY - 0.8, hitchZ - 1.2, hitchX + 1.2, hitchY + 1.2, hitchZ + 1.2);
                List<JaneMooseAttachmentEntity> nearby = this.level().getEntitiesOfClass(JaneMooseAttachmentEntity.class, searchBox, att -> !att.isHitched() && att.isAlive());
                if (!nearby.isEmpty()) {
                    JaneMooseAttachmentEntity toHitch = nearby.get(0);
                    hitchAttachment(toHitch);
                    hitched = toHitch;
                }
            }

            if (this.isPhysicallyMoving && rider instanceof Player player) {
                // Consume fuel
                if (this.tickCount % 12 == 0) {
                    boolean working = (hitched != null && hitched.isActive() && hitched.isLowered());
                    int fuelConsumption = working ? 2 : 1;
                    int newFuel = Math.max(0, getFuel() - fuelConsumption);
                    setFuel(newFuel);

                    if (newFuel <= 0) {
                        player.sendOverlayMessage(Component.translatable("message.janemoose.out_of_fuel"));
                    }
                }

                // Process rear attachment operations ONLY when lowered (working mode), active, and fueled
                if (getFuel() > 0 && hitched != null && hitched.isActive() && hitched.isLowered()) {
                    ItemStack attachmentStack = hitched.getItem();
                    if (!attachmentStack.isEmpty() && attachmentStack.getItem() instanceof IAttachment attachment) {
                        runAttachmentFarming(serverLevel, attachment, hitched, player);
                    }
                }
            }
        }
    }

    private void updateHeadlightIllumination(ServerLevel level) {
        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardX = -Math.sin(yawRad);
        double forwardZ = Math.cos(yawRad);

        BlockPos bestPos = findBestHeadlightPos(level, forwardX, forwardZ);
        if (bestPos != null) {
            if (!bestPos.equals(this.lastLightPos)) {
                removeLightBlock(level);
                placeLightAt(level, bestPos);
            } else {
                BlockState current = level.getBlockState(bestPos);
                if (!current.is(Blocks.LIGHT)) {
                    placeLightAt(level, bestPos);
                }
            }
        } else {
            removeLightBlock(level);
        }
    }

    private BlockPos findBestHeadlightPos(ServerLevel level, double forwardX, double forwardZ) {
        // 1. Primary beam point: 1.85 blocks in front of tractor, at headlight height
        BlockPos primary = BlockPos.containing(this.getX() + forwardX * 1.85, this.getY() + 0.85, this.getZ() + forwardZ * 1.85);
        if (canPlaceLightAt(level, primary)) {
            return primary;
        }

        // 2. Upward offset if ground slopes up or crops are below
        BlockPos primaryUp = primary.above();
        if (canPlaceLightAt(level, primaryUp)) {
            return primaryUp;
        }

        // 3. Closer in front of grille (1.3 blocks forward)
        BlockPos closer = BlockPos.containing(this.getX() + forwardX * 1.3, this.getY() + 0.9, this.getZ() + forwardZ * 1.3);
        if (canPlaceLightAt(level, closer)) {
            return closer;
        }

        // 4. Above hood
        BlockPos aboveHood = BlockPos.containing(this.getX() + forwardX * 0.8, this.getY() + 1.2, this.getZ() + forwardZ * 0.8);
        if (canPlaceLightAt(level, aboveHood)) {
            return aboveHood;
        }

        return null;
    }

    private boolean canPlaceLightAt(ServerLevel level, BlockPos pos) {
        if (pos.equals(this.lastLightPos)) {
            return true;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.LIGHT)) {
            return true;
        }
        FluidState fluid = level.getFluidState(pos);
        return fluid.is(Fluids.WATER) && fluid.isSource() && state.is(Blocks.WATER);
    }

    private void placeLightAt(ServerLevel level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        boolean isWater = fluid.is(Fluids.WATER);
        BlockState lightState = Blocks.LIGHT.defaultBlockState()
                .setValue(LightBlock.LEVEL, 15)
                .setValue(LightBlock.WATERLOGGED, isWater);
        level.setBlock(pos, lightState, 3);
        this.lastLightPos = pos;
    }

    private void removeLightBlock(ServerLevel level) {
        if (this.lastLightPos != null) {
            BlockState state = level.getBlockState(this.lastLightPos);
            if (state.is(Blocks.LIGHT)) {
                if (state.hasProperty(LightBlock.WATERLOGGED) && state.getValue(LightBlock.WATERLOGGED)) {
                    level.setBlock(this.lastLightPos, Blocks.WATER.defaultBlockState(), 3);
                } else {
                    level.setBlock(this.lastLightPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
            this.lastLightPos = null;
        }
    }

    private void runAttachmentFarming(ServerLevel level, IAttachment attachment, JaneMooseAttachmentEntity attachmentEntity, Player player) {
        float yawRad = (float) Math.toRadians(this.getYRot());
        double perpX = Math.cos(yawRad);
        double perpZ = Math.sin(yawRad);

        int width = attachment.getWidth();
        double[] offsets;
        if (width == 1) {
            offsets = new double[]{0.0};
        } else if (width == 2) {
            offsets = new double[]{-0.5, 0.5};
        } else {
            offsets = new double[]{-1.0, 0.0, 1.0};
        }

        // Dual rear depth sampling ensures no skipped blocks when moving at speed or turning
        double[] rearDistances = new double[]{1.45, 1.85};

        for (double rearDist : rearDistances) {
            double rearX = this.getX() + Math.sin(yawRad) * rearDist;
            double rearZ = this.getZ() - Math.cos(yawRad) * rearDist;

            for (double offset : offsets) {
                double posX = rearX + perpX * offset;
                double posZ = rearZ + perpZ * offset;
                BlockPos groundPos = BlockPos.containing(posX, this.getY(), posZ);

                attachment.processBlock(level, groundPos, this, attachmentEntity, player);
            }
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        if (getFuel() <= 0) {
            return Vec3.ZERO;
        }
        // ONLY forward/backward - NO strafing sideways (x = 0)
        double forward = player.zza;
        if (forward < 0.0) {
            forward *= 0.55; // Reverse is controlled
        }
        return new Vec3(0.0, 0.0, forward);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        if (getFuel() <= 0) return 0.0F;
        // Travel at 50% normal speed ONLY when implement is lowered & active in working mode!
        // In transport mode (lifted) or unhitched, full 100% normal speed (0.11F)!
        JaneMooseAttachmentEntity att = getHitchedAttachment();
        boolean working = att != null && att.isLowered() && att.isActive();
        return working ? 0.055F : 0.11F;
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);

        // Turn tractor using A and D keys
        if (player.xxa != 0.0F && getFuel() > 0) {
            float turnSpeed = 3.2F;
            float steerDir = (player.zza < 0.0F) ? -1.0F : 1.0F;
            float newYaw = this.getYRot() - (player.xxa * turnSpeed * steerDir);
            this.setYRot(newYaw);
            this.yRotO = newYaw;
            this.yBodyRot = newYaw;
            this.yHeadRot = newYaw;
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            double seatY = this.getY() + 0.88;
            double yawRad = Math.toRadians(this.getYRot());
            double seatOffset = 0.25;
            double seatX = this.getX() + Math.sin(yawRad) * seatOffset;
            double seatZ = this.getZ() - Math.cos(yawRad) * seatOffset;
            callback.accept(passenger, seatX, seatY, seatZ);
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity living ? living : null;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        }
        if (!this.isRemoved()) {
            removeLightBlock(level);
            unhitchAttachment();
            if (source.getEntity() instanceof Player player && player.isCreative()) {
                this.dropAttachment(level);
                this.discard();
                return true;
            }

            this.dropAttachment(level);
            this.spawnAtLocation(level, new ItemStack(ModItems.TRACTOR.get()));
            this.discard();
            return true;
        }
        return true;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            removeLightBlock(serverLevel);
        }
        super.remove(reason);
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setFuel(input.getIntOr("Fuel", 2000));
        input.read("Attachment", ItemStack.CODEC).ifPresent(this::setAttachment);
        setAttachmentEntityId(input.getIntOr("AttachmentEntityId", -1));
        setHeadlightsOn(input.getBooleanOr("Headlights", false));
        if (input.getIntOr("LastLightY", -999) != -999) {
            int lx = input.getIntOr("LastLightX", 0);
            int ly = input.getIntOr("LastLightY", 0);
            int lz = input.getIntOr("LastLightZ", 0);
            this.lastLightPos = new BlockPos(lx, ly, lz);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Fuel", getFuel());
        ItemStack attachment = getAttachment();
        if (!attachment.isEmpty()) {
            output.store("Attachment", ItemStack.CODEC, attachment);
        }
        output.putInt("AttachmentEntityId", getAttachmentEntityId());
        output.putBoolean("Headlights", areHeadlightsOn());
        if (this.lastLightPos != null) {
            output.putInt("LastLightX", this.lastLightPos.getX());
            output.putInt("LastLightY", this.lastLightPos.getY());
            output.putInt("LastLightZ", this.lastLightPos.getZ());
        }
    }
}
