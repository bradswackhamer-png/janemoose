package com.swack.janemoose.entity;

import com.swack.janemoose.gui.BucketTruckMenu;
import com.swack.janemoose.item.DieselCanisterItem;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IMenuProviderExtension;

public class JaneMooseBucketTruckEntity extends LivingEntity implements PlayerRideableJumping {
    public static final int MAX_FUEL = 10000;

    // 3-Speed Transmission: Gear 1 = 1 m/s (0.05 blocks/tick), Gear 2 = 2 m/s (0.10 blocks/tick), Gear 3 = 3 m/s (0.15 blocks/tick)
    private static final float RIDDEN_SPEED_1MS = 0.0227F;
    private static final float RIDDEN_SPEED_2MS = 0.0454F;
    private static final float RIDDEN_SPEED_3MS = 0.0681F;

    private static final EntityDataAccessor<Integer> DATA_FUEL =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_ENGINE_RUNNING =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HEADLIGHTS =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SPEED_GEAR =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_BOOM_TARGET =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BOOM_EXTENSION =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_BOOM_YAW =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_IN_BUCKET =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_PLATFORM_ID =
            SynchedEntityData.defineId(JaneMooseBucketTruckEntity.class, EntityDataSerializers.INT);

    private BlockPos lastLightPos = null;

    // Movement & animation tracking
    private double prevCoordX = Double.NaN;
    private double prevCoordZ = Double.NaN;
    private boolean isPhysicallyMoving = false;

    private float wheelAngle = 0.0F;
    private float prevWheelAngle = 0.0F;
    private float prevBoomExtension = 0.0F;
    private float prevBoomYaw = 0.0F;

    public JaneMooseBucketTruckEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 120.0)
                .add(Attributes.MOVEMENT_SPEED, 0.10)
                .add(Attributes.STEP_HEIGHT, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FUEL, 2000);
        builder.define(DATA_ENGINE_RUNNING, false);
        builder.define(DATA_HEADLIGHTS, false);
        builder.define(DATA_SPEED_GEAR, 2); // Default Gear 2 (2 m/s)
        builder.define(DATA_BOOM_TARGET, 0.0F);
        builder.define(DATA_BOOM_EXTENSION, 0.0F);
        builder.define(DATA_BOOM_YAW, 0.0F);
        builder.define(DATA_IN_BUCKET, false);
        builder.define(DATA_PLATFORM_ID, -1);
    }

    public int getFuel() {
        return this.entityData.get(DATA_FUEL);
    }

    public void setFuel(int fuel) {
        this.entityData.set(DATA_FUEL, Mth.clamp(fuel, 0, MAX_FUEL));
    }

    public boolean isEngineRunning() {
        return this.entityData.get(DATA_ENGINE_RUNNING);
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

    public int getSpeedGear() {
        return Mth.clamp(this.entityData.get(DATA_SPEED_GEAR), 1, 3);
    }

    public void setSpeedGear(int gear) {
        this.entityData.set(DATA_SPEED_GEAR, Mth.clamp(gear, 1, 3));
    }

    public int getSpeedMetersPerSecond() {
        return getSpeedGear(); // Gear 1 = 1 m/s, Gear 2 = 2 m/s, Gear 3 = 3 m/s
    }

    public double getMaxBlocksPerTick() {
        return switch (getSpeedGear()) {
            case 1 -> 0.05; // 1.0 m/s (20 ticks/s * 0.05 = 1.0 m/s)
            case 2 -> 0.10; // 2.0 m/s (20 ticks/s * 0.10 = 2.0 m/s)
            case 3 -> 0.15; // 3.0 m/s (20 ticks/s * 0.15 = 3.0 m/s)
            default -> 0.10;
        };
    }

    public void cycleSpeedGear(Player player) {
        int nextGear = (getSpeedGear() % 3) + 1;
        setSpeedGear(nextGear);
        float pitch = 0.8F + (nextGear * 0.2F);
        this.level().playSound(null, this.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.8F, pitch);
        if (player != null) {
            player.sendOverlayMessage(Component.translatable("message.janemoose.bucket_truck_speed", nextGear, nextGear));
        }
    }

    public float getBoomTarget() {
        return this.entityData.get(DATA_BOOM_TARGET);
    }

    public void setBoomTarget(float target) {
        this.entityData.set(DATA_BOOM_TARGET, Mth.clamp(target, 0.0F, 1.0F));
    }

    public float getBoomExtension() {
        return this.entityData.get(DATA_BOOM_EXTENSION);
    }

    public void setBoomExtension(float ext) {
        this.entityData.set(DATA_BOOM_EXTENSION, Mth.clamp(ext, 0.0F, 1.0F));
    }

    public float getBoomExtension(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevBoomExtension, getBoomExtension());
    }

    public float getBoomYaw() {
        return this.entityData.get(DATA_BOOM_YAW);
    }

    public void setBoomYaw(float yaw) {
        this.entityData.set(DATA_BOOM_YAW, Mth.wrapDegrees(yaw));
    }

    public float getBoomYaw(float partialTicks) {
        return Mth.rotLerp(partialTicks, this.prevBoomYaw, getBoomYaw());
    }

    public boolean isInBucket() {
        return this.entityData.get(DATA_IN_BUCKET);
    }

    public void setInBucket(boolean inBucket) {
        this.entityData.set(DATA_IN_BUCKET, inBucket);
    }

    public int getPlatformEntityId() {
        return this.entityData.get(DATA_PLATFORM_ID);
    }

    public void setPlatformEntityId(int id) {
        this.entityData.set(DATA_PLATFORM_ID, id);
    }

    public JaneMooseBucketPlatformEntity getPlatformEntity() {
        int id = getPlatformEntityId();
        if (id == -1) return null;
        Entity ent = this.level().getEntity(id);
        if (ent instanceof JaneMooseBucketPlatformEntity plat && plat.isAlive()) {
            return plat;
        }
        return null;
    }

    public void adjustBoomTarget(float delta, Player player) {
        if (getFuel() <= 0) {
            if (player != null) {
                player.sendOverlayMessage(Component.translatable("message.janemoose.out_of_fuel"));
            }
            return;
        }
        float oldTarget = getBoomTarget();
        float newTarget = Mth.clamp(oldTarget + delta, 0.0F, 1.0F);
        setBoomTarget(newTarget);
        if (Math.abs(newTarget - oldTarget) > 0.001F) {
            this.level().playSound(null, this.blockPosition(),
                    delta > 0 ? SoundEvents.PISTON_EXTEND : SoundEvents.PISTON_CONTRACT,
                    SoundSource.BLOCKS, 0.55F, 1.15F);
            if (player != null) {
                int pct = Math.round(newTarget * 100.0F);
                double heightMeters = Math.round(getBucketHeightOffset(newTarget) * 10.0) / 10.0;
                player.sendOverlayMessage(Component.translatable("message.janemoose.boom_height", pct, heightMeters));
            }
        }
    }

    public void adjustBoomYaw(float deltaDegrees, Player player) {
        if (getFuel() <= 0) return;
        setBoomYaw(getBoomYaw() + deltaDegrees);
        this.level().playSound(null, this.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 0.4F, 1.3F);
    }

    public void mountInCab(Player player) {
        setInBucket(false);
        if (!this.level().isClientSide()) {
            player.startRiding(this, true, true);
            player.sendOverlayMessage(Component.translatable("message.janemoose.station_cab"));
        }
    }

    public void mountInBucket(Player player) {
        setInBucket(true);
        if (!this.level().isClientSide()) {
            player.startRiding(this, true, true);
            this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 0.9F, 1.2F);
            player.sendOverlayMessage(Component.translatable("message.janemoose.station_bucket"));
        }
    }

    public void toggleCabAndBucket(Player player) {
        if (player == null) return;
        if (this.hasPassenger(player)) {
            boolean newState = !isInBucket();
            setInBucket(newState);
            Vec3 targetPos = newState ? getBucketWorldPos(1.0F) : getCabSeatWorldPos();
            this.level().playSound(null, BlockPos.containing(targetPos), SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.PLAYERS, 0.8F, 1.1F);
            player.sendOverlayMessage(Component.translatable(newState ? "message.janemoose.station_bucket" : "message.janemoose.station_cab"));
        }
    }

    public static double getBucketHeightOffset( float boomExt) {
        float theta1 = Mth.lerp(boomExt, 0.30F, 1.25F);
        float theta2 = Mth.lerp(boomExt, 0.05F, 1.30F);
        double l1 = 2.375; // 38.0F / 16.0
        double l2 = (40.0 + 32.0 * boomExt) / 16.0;
        double pivotY = 1.0625; // (24.0 - 7.0) / 16.0
        return pivotY + l1 * Math.sin(theta1) + l2 * Math.sin(theta2) - 0.45;
    }

    public Vec3 getBucketWorldPos(float partialTicks) {
        float boomExt = getBoomExtension(partialTicks);
        float theta1 = Mth.lerp(boomExt, 0.30F, 1.25F);
        float theta2 = Mth.lerp(boomExt, 0.05F, 1.30F);

        double l1 = 2.375; // 38.0F / 16.0 blocks
        double l2 = (40.0 + 32.0 * boomExt) / 16.0; // 2.5 to 4.5 blocks

        double pivotY = 1.0625;
        double pivotRearDist = 0.75; // Turret is 12.0F pixels (0.75 blocks) toward the rear of the truck

        double radialFromTurret = -l1 * Math.cos(theta1) + l2 * Math.cos(theta2) + 0.3125;
        double bucketFloorY = pivotY + l1 * Math.sin(theta1) + l2 * Math.sin(theta2) - 0.45;

        float truckYawRad = (float) Math.toRadians(this.getViewYRot(partialTicks));
        float totalBoomYawRad = (float) Math.toRadians(this.getViewYRot(partialTicks) + getBoomYaw(partialTicks));

        double baseX = Mth.lerp(partialTicks, this.xo, this.getX());
        double baseY = Mth.lerp(partialTicks, this.yo, this.getY());
        double baseZ = Mth.lerp(partialTicks, this.zo, this.getZ());

        // Turret world position (rearward along truck axis)
        double turretX = baseX + Math.sin(truckYawRad) * pivotRearDist;
        double turretZ = baseZ - Math.cos(truckYawRad) * pivotRearDist;

        // Bucket world position (along turret boom heading)
        double worldX = turretX + Math.sin(totalBoomYawRad) * radialFromTurret;
        double worldY = baseY + bucketFloorY;
        double worldZ = turretZ - Math.cos(totalBoomYawRad) * radialFromTurret;

        return new Vec3(worldX, worldY, worldZ);
    }

    public Vec3 getCabSeatWorldPos() {
        float yawRad = (float) Math.toRadians(this.getYRot());
        double forwardDist = 0.92; // Front cab driver's seat
        double seatX = this.getX() - Math.sin(yawRad) * forwardDist;
        double seatY = this.getY() + 0.78;
        double seatZ = this.getZ() + Math.cos(yawRad) * forwardDist;
        return new Vec3(seatX, seatY, seatZ);
    }

    public float getWheelRotation(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevWheelAngle, this.wheelAngle);
    }

    public void openCustomGui(ServerPlayer player) {
        player.openMenu(new BucketTruckMenuProvider(this));
    }

    private record BucketTruckMenuProvider(JaneMooseBucketTruckEntity truck) implements MenuProvider, IMenuProviderExtension {
        @Override
        public Component getDisplayName() {
            return truck.hasCustomName() ? truck.getCustomName() : Component.translatable("gui.janemoose.bucket_truck_dashboard");
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
            return new BucketTruckMenu(containerId, playerInventory, truck);
        }

        @Override
        public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buf) {
            buf.writeInt(truck.getId());
        }
    }

    @Override
    public boolean isPushable() {
        return getBoomExtension() < 0.05F;
    }

    @Override
    public void onPlayerJump(int jumpPower) {}

    @Override
    public boolean canJump() {
        return false;
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
            // Wrench on sneak -> pack up bucket truck into item
            if (heldItem.getItem() instanceof WrenchItem) {
                if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                    removePlatformEntity();
                    removeLightBlock(serverLevel);
                    this.spawnAtLocation(serverLevel, new ItemStack(ModItems.BUCKET_TRUCK.get()));
                    this.discard();
                }
                return InteractionResult.SUCCESS;
            }

            // Sneak with empty hand or tool -> open Bucket Truck Dashboard GUI
            if (player instanceof ServerPlayer serverPlayer) {
                openCustomGui(serverPlayer);
                return InteractionResult.SUCCESS;
            }
        }

        // If player is already riding this truck: right-click with empty hand opens Dashboard GUI
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

        // Generic Fuel (Diesel, Ethanol, Biofuel)
        if (JaneMooseTractorEntity.isFuel(heldItem)) {
            if (getFuel() < MAX_FUEL) {
                if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                    setFuel(getFuel() + 1000);
                    if (!player.isCreative()) {
                        ItemStack remainder = JaneMooseTractorEntity.getEmptyContainer(heldItem);
                        heldItem.shrink(1);
                        if (!remainder.isEmpty()) {
                            if (heldItem.isEmpty()) {
                                player.setItemInHand(hand, remainder);
                            } else if (!player.getInventory().add(remainder)) {
                                this.spawnAtLocation(serverLevel, remainder);
                            }
                        }
                    }
                    this.level().playSound(null, this.blockPosition(), SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.sendOverlayMessage(Component.translatable("message.janemoose.fueled", getFuel(), MAX_FUEL));
                }
                return InteractionResult.SUCCESS;
            }
        }

        // Wrench right-click -> Open Dashboard GUI
        if (heldItem.getItem() instanceof WrenchItem) {
            if (player instanceof ServerPlayer serverPlayer) {
                openCustomGui(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }

        // Check if player clicked near the rear/bucket area vs the front cab
        if (!this.level().isClientSide()) {
            float yawRad = (float) Math.toRadians(this.getYRot());
            double forwardClick = -(location.x * Math.sin(yawRad)) + (location.z * Math.cos(yawRad));
            if (forwardClick < -0.35 || location.y > 1.35) {
                mountInBucket(player);
            } else {
                mountInCab(player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();

        double dx = Double.isNaN(this.prevCoordX) ? 0.0 : this.getX() - this.prevCoordX;
        double dz = Double.isNaN(this.prevCoordZ) ? 0.0 : this.getZ() - this.prevCoordZ;
        double distMovedSq = dx * dx + dz * dz;
        this.prevCoordX = this.getX();
        this.prevCoordZ = this.getZ();

        LivingEntity rider = this.getControllingPassenger();
        boolean hasRiderInput = (rider instanceof Player p) && (Math.abs(p.zza) > 0.001F || Math.abs(p.xxa) > 0.001F);
        this.isPhysicallyMoving = (distMovedSq > 0.0001) || (hasRiderInput && distMovedSq > 0.000001);

        // Wheel rotation
        this.prevWheelAngle = this.wheelAngle;
        if (distMovedSq > 0.0001) {
            float yawRad = (float) Math.toRadians(this.getYRot());
            double headingX = -Math.sin(yawRad);
            double headingZ = Math.cos(yawRad);
            double forwardDist = dx * headingX + dz * headingZ;
            this.wheelAngle += (float) (forwardDist * 2.8);
        }

        // Smooth hydraulic boom articulation
        this.prevBoomExtension = getBoomExtension();
        this.prevBoomYaw = getBoomYaw();
        float currentExt = getBoomExtension();
        float targetExt = getBoomTarget();
        if (Math.abs(targetExt - currentExt) > 0.0005F) {
            float step = 0.025F;
            float nextExt = currentExt + Mth.clamp(targetExt - currentExt, -step, step);
            setBoomExtension(nextExt);
        }

        // Exhaust smoke particles when engine is running
        if (this.level().isClientSide() && isEngineRunning() && this.random.nextFloat() < 0.25F) {
            float yawRad = (float) Math.toRadians(this.getYRot());
            double exhaustX = this.getX() - Math.cos(yawRad) * 0.55 - Math.sin(yawRad) * 0.35;
            double exhaustZ = this.getZ() - Math.sin(yawRad) * 0.55 + Math.cos(yawRad) * 0.35;
            this.level().addParticle(ParticleTypes.SMOKE, exhaustX, this.getY() + 0.45, exhaustZ, 0.0, 0.03, 0.0);
        }

        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            boolean boomMoving = Math.abs(targetExt - currentExt) > 0.001F;
            this.entityData.set(DATA_ENGINE_RUNNING, (rider != null || getBoomExtension() > 0.01F) && getFuel() > 0);

            // Ensure companion solid platform entity exists and tracks the aerial bucket
            ensurePlatformEntity(serverLevel);

            // Headlight illumination
            if (areHeadlightsOn()) {
                updateHeadlightIllumination(serverLevel);
            } else if (this.lastLightPos != null) {
                removeLightBlock(serverLevel);
            }

            // Fuel consumption while driving or operating hydraulics
            if ((this.isPhysicallyMoving || boomMoving) && getFuel() > 0) {
                if (this.tickCount % 15 == 0) {
                    int newFuel = Math.max(0, getFuel() - 1);
                    setFuel(newFuel);
                    if (newFuel <= 0 && rider instanceof Player player) {
                        player.sendOverlayMessage(Component.translatable("message.janemoose.out_of_fuel"));
                    }
                }
            }
        }
    }

    private void ensurePlatformEntity(ServerLevel serverLevel) {
        JaneMooseBucketPlatformEntity platform = getPlatformEntity();
        if (platform == null) {
            platform = new JaneMooseBucketPlatformEntity(ModEntities.BUCKET_PLATFORM.get(), serverLevel);
            Vec3 bucketPos = getBucketWorldPos(1.0F);
            platform.setPos(bucketPos.x, bucketPos.y, bucketPos.z);
            platform.setTruckId(this.getId());
            serverLevel.addFreshEntity(platform);
            setPlatformEntityId(platform.getId());
        }
    }

    private void removePlatformEntity() {
        JaneMooseBucketPlatformEntity platform = getPlatformEntity();
        if (platform != null) {
            platform.discard();
        }
        setPlatformEntityId(-1);
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
        BlockPos primary = BlockPos.containing(this.getX() + forwardX * 2.1, this.getY() + 0.85, this.getZ() + forwardZ * 2.1);
        if (canPlaceLightAt(level, primary)) return primary;

        BlockPos primaryUp = primary.above();
        if (canPlaceLightAt(level, primaryUp)) return primaryUp;

        BlockPos closer = BlockPos.containing(this.getX() + forwardX * 1.5, this.getY() + 1.0, this.getZ() + forwardZ * 1.5);
        if (canPlaceLightAt(level, closer)) return closer;

        return null;
    }

    private boolean canPlaceLightAt(ServerLevel level, BlockPos pos) {
        if (pos.equals(this.lastLightPos)) return true;
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.LIGHT)) return true;
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

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        if (getFuel() <= 0) {
            return Vec3.ZERO;
        }
        double forward = player.zza;
        if (forward < 0.0) {
            forward *= 0.65; // Controlled reverse speed
        }
        return new Vec3(0.0, 0.0, forward);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        if (getFuel() <= 0) return 0.0F;
        return switch (getSpeedGear()) {
            case 1 -> RIDDEN_SPEED_1MS; // 1 m/s
            case 2 -> RIDDEN_SPEED_2MS; // 2 m/s
            case 3 -> RIDDEN_SPEED_3MS; // 3 m/s
            default -> RIDDEN_SPEED_2MS;
        };
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);

        // Steer truck using A and D keys
        if (player.xxa != 0.0F && getFuel() > 0) {
            float turnSpeed = 2.8F;
            float steerDir = (player.zza < 0.0F) ? -1.0F : 1.0F;
            float newYaw = this.getYRot() - (player.xxa * turnSpeed * steerDir);
            this.setYRot(newYaw);
            this.yRotO = newYaw;
            this.yBodyRot = newYaw;
            this.yHeadRot = newYaw;
        }

        // Enforce exact 1 m/s (0.05/t), 2 m/s (0.10/t), or 3 m/s (0.15/t) cap
        Vec3 motion = this.getDeltaMovement();
        double horizSpeed = Math.hypot(motion.x, motion.z);
        double maxBlocksPerTick = getMaxBlocksPerTick();
        if (horizSpeed > maxBlocksPerTick && horizSpeed > 0.0001) {
            double scale = maxBlocksPerTick / horizSpeed;
            this.setDeltaMovement(motion.x * scale, motion.y, motion.z * scale);
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            if (isInBucket()) {
                Vec3 bucketPos = getBucketWorldPos(1.0F);
                callback.accept(passenger, bucketPos.x, bucketPos.y + 0.15, bucketPos.z);
            } else {
                Vec3 cabPos = getCabSeatWorldPos();
                callback.accept(passenger, cabPos.x, cabPos.y, cabPos.z);
            }
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        if (isInBucket()) {
            // Dismount directly onto the solid aerial bucket platform floor so the player can stand on the platform!
            Vec3 bucketPos = getBucketWorldPos(1.0F);
            passenger.fallDistance = 0.0F;
            return new Vec3(bucketPos.x, bucketPos.y + 0.36, bucketPos.z);
        }
        float yawRad = (float) Math.toRadians(this.getYRot());
        double sideX = this.getX() + Math.cos(yawRad) * 1.45 - Math.sin(yawRad) * 0.8;
        double sideZ = this.getZ() + Math.sin(yawRad) * 1.45 + Math.cos(yawRad) * 0.8;
        return new Vec3(sideX, this.getY() + 0.1, sideZ);
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
            removePlatformEntity();
            removeLightBlock(level);
            if (source.getEntity() instanceof Player player && player.isCreative()) {
                this.discard();
                return true;
            }
            this.spawnAtLocation(level, new ItemStack(ModItems.BUCKET_TRUCK.get()));
            this.discard();
            return true;
        }
        return true;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            removePlatformEntity();
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
        setHeadlightsOn(input.getBooleanOr("Headlights", false));
        setSpeedGear(input.getIntOr("SpeedGear", 2));
        float boomExt = input.getFloatOr("BoomExtension", 0.0F);
        setBoomExtension(boomExt);
        setBoomTarget(input.getFloatOr("BoomTarget", boomExt));
        setBoomYaw(input.getFloatOr("BoomYaw", 0.0F));
        setInBucket(input.getBooleanOr("InBucket", false));
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
        output.putBoolean("Headlights", areHeadlightsOn());
        output.putInt("SpeedGear", getSpeedGear());
        output.putFloat("BoomExtension", getBoomExtension());
        output.putFloat("BoomTarget", getBoomTarget());
        output.putFloat("BoomYaw", getBoomYaw());
        output.putBoolean("InBucket", isInBucket());
        if (this.lastLightPos != null) {
            output.putInt("LastLightX", this.lastLightPos.getX());
            output.putInt("LastLightY", this.lastLightPos.getY());
            output.putInt("LastLightZ", this.lastLightPos.getZ());
        }
    }
}
