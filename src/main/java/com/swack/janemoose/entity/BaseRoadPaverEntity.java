package com.swack.janemoose.entity;

import com.swack.janemoose.item.WrenchItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IMenuProviderExtension;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class BaseRoadPaverEntity extends LivingEntity implements Container, MenuProvider, IMenuProviderExtension {
    public static final int MODE_STOPPED = 0;
    public static final int MODE_FORWARD = 1;
    public static final int MODE_REVERSE = 2;

    public static final int LOCK_OFF = 0;
    public static final int LOCK_NORTH = 1;
    public static final int LOCK_SOUTH = 2;
    public static final int LOCK_WEST = 3;
    public static final int LOCK_EAST = 4;

    public static final int MOVE_TICKS_PER_BLOCK = 20;
    public static final int REVERSE_TICKS_PER_BLOCK = 30;
    public static final int FAST_MOVE_TICKS_PER_BLOCK = 10;
    public static final int FAST_REVERSE_TICKS_PER_BLOCK = 10;

    public static final int SLOT_MATERIAL_COUNT = 27;

    protected static final EntityDataAccessor<Direction> DATA_FACING =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.DIRECTION);
    protected static final EntityDataAccessor<Boolean> DATA_ACTIVE =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_PAVING =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DATA_RUN_MODE =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_CARDINAL_LOCK =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Boolean> DATA_WORK_LIGHT =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> DATA_HOLOGRAM =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> DATA_FUEL =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> DATA_FUEL_TOTAL =
            SynchedEntityData.defineId(BaseRoadPaverEntity.class, EntityDataSerializers.INT);

    protected NonNullList<ItemStack> itemStacks;

    protected boolean isMoving = false;
    protected int moveTicks = 0;
    protected @Nullable BlockPos currentStepPos = null;
    protected @Nullable BlockPos targetStepPos = null;
    protected int outOfMaterialNotifyCooldown = 0;
    protected @Nullable BlockPos currentLightPos = null;

    public float treadRotation = 0.0F;

    public BaseRoadPaverEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
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
        builder.define(DATA_FACING, Direction.NORTH);
        builder.define(DATA_ACTIVE, false);
        builder.define(DATA_PAVING, true);
        builder.define(DATA_RUN_MODE, MODE_STOPPED);
        builder.define(DATA_CARDINAL_LOCK, LOCK_OFF);
        builder.define(DATA_WORK_LIGHT, false);
        builder.define(DATA_HOLOGRAM, false);
        builder.define(DATA_FUEL, 0);
        builder.define(DATA_FUEL_TOTAL, 1600);
    }

    public abstract int getPavingWidth();
    public abstract int getFilterSlotCount();
    public abstract int getFuelSlot();
    public abstract int getMaterialStartSlot();
    public abstract boolean isDiesel();
    public abstract Item getDropItem();
    public abstract boolean consumeFuelTick();
    public abstract boolean isFuelItem(ItemStack stack);
    public abstract void refuelFromItem(ItemStack stack, Player player);

    @Override
    public int getContainerSize() {
        return getFilterSlotCount() + 1 + SLOT_MATERIAL_COUNT;
    }

    public int getFuel() {
        return this.entityData.get(DATA_FUEL);
    }

    public void setFuel(int fuel) {
        this.entityData.set(DATA_FUEL, fuel);
    }

    public int getFuelTotal() {
        return this.entityData.get(DATA_FUEL_TOTAL);
    }

    public void setFuelTotal(int total) {
        this.entityData.set(DATA_FUEL_TOTAL, total);
    }

    public boolean isActive() {
        return this.entityData.get(DATA_ACTIVE);
    }

    public void setActive(boolean active) {
        this.entityData.set(DATA_ACTIVE, active);
    }

    public boolean isWorkMode() {
        return this.entityData.get(DATA_PAVING);
    }

    public void setWorkMode(boolean workMode) {
        this.entityData.set(DATA_PAVING, workMode);
        if (workMode && this.getRunMode() != MODE_STOPPED && !this.hasPaverMaterial()) {
            this.setRunMode(MODE_STOPPED);
            this.notifyOutOfMaterial(null);
        }
    }

    public int getRunMode() {
        return this.entityData.get(DATA_RUN_MODE);
    }

    public void setRunMode(int mode) {
        if (mode != MODE_STOPPED && this.isWorkMode() && !this.hasPaverMaterial()) {
            this.entityData.set(DATA_RUN_MODE, MODE_STOPPED);
            this.setActive(false);
            this.isMoving = false;
            this.moveTicks = 0;
            this.targetStepPos = null;
            this.setDeltaMovement(Vec3.ZERO);
            this.notifyOutOfMaterial(null);
            return;
        }
        this.entityData.set(DATA_RUN_MODE, mode);
        if (mode == MODE_STOPPED) {
            this.setActive(false);
            this.isMoving = false;
            this.moveTicks = 0;
            this.targetStepPos = null;
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    public int getCardinalLock() {
        return this.entityData.get(DATA_CARDINAL_LOCK);
    }

    public void setCardinalLock(int lock) {
        this.entityData.set(DATA_CARDINAL_LOCK, Mth.clamp(lock, 0, 4));
        Direction lockedDir = getLockedDirection(lock);
        if (lockedDir != null) {
            this.setFacing(lockedDir);
        }
    }

    public @Nullable Direction getLockedDirection(int lock) {
        return switch (lock) {
            case LOCK_NORTH -> Direction.NORTH;
            case LOCK_SOUTH -> Direction.SOUTH;
            case LOCK_WEST -> Direction.WEST;
            case LOCK_EAST -> Direction.EAST;
            default -> null;
        };
    }

    public static int getLockForDirection(Direction dir) {
        return switch (dir) {
            case NORTH -> LOCK_NORTH;
            case SOUTH -> LOCK_SOUTH;
            case WEST -> LOCK_WEST;
            case EAST -> LOCK_EAST;
            default -> LOCK_OFF;
        };
    }

    public boolean isWorkLightEnabled() {
        return this.entityData.get(DATA_WORK_LIGHT);
    }

    public void setWorkLightEnabled(boolean enabled) {
        this.entityData.set(DATA_WORK_LIGHT, enabled);
        if (!enabled && !this.level().isClientSide()) {
            this.clearWorkLight();
        }
    }

    public boolean isHologramEnabled() {
        return this.entityData.get(DATA_HOLOGRAM);
    }

    public void setHologramEnabled(boolean enabled) {
        this.entityData.set(DATA_HOLOGRAM, enabled);
    }

    public Direction getFacing() {
        return this.entityData.get(DATA_FACING);
    }

    public void setFacing(Direction facing) {
        if (facing.getAxis().isHorizontal()) {
            this.entityData.set(DATA_FACING, facing);
            float yRot = facing.toYRot();
            this.setYRot(yRot);
            this.yRotO = yRot;
            this.yBodyRot = yRot;
            this.yHeadRot = yRot;
            this.reapplyPosition();
        }
    }

    public int getTicksPerBlock(boolean isReversing) {
        if (this.isWorkMode()) {
            return isReversing ? REVERSE_TICKS_PER_BLOCK : MOVE_TICKS_PER_BLOCK;
        } else {
            return isReversing ? FAST_REVERSE_TICKS_PER_BLOCK : FAST_MOVE_TICKS_PER_BLOCK;
        }
    }

    @Override
    public boolean canBeCollidedWith(@Nullable Entity entity) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void travel(Vec3 travelVector) {
        // Road paver uses custom step-based movement to ensure exact block alignment
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    protected AABB makeBoundingBox(Vec3 position) {
        Direction facing = this.entityData != null ? this.getFacing() : Direction.NORTH;
        double forward = 1.50;
        double backward = 1.50;
        double halfWidth = this.getPavingWidth() > 3 ? 2.40 : 1.40;
        double height = 2.00;

        double centerOffset = (forward - backward) * 0.5;
        double halfLength = (forward + backward) * 0.5;

        double cx = position.x + facing.getStepX() * centerOffset;
        double cz = position.z + facing.getStepZ() * centerOffset;

        double halfX = facing.getAxis() == Direction.Axis.X ? halfLength : halfWidth;
        double halfZ = facing.getAxis() == Direction.Axis.Z ? halfLength : halfWidth;

        return new AABB(
                cx - halfX, position.y, cz - halfZ,
                cx + halfX, position.y + height, cz + halfZ
        );
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if (this.hasPassenger(passenger)) {
            Direction facing = this.getFacing();
            Direction right = facing.getClockWise();
            double forwardOffset = 0.15D;
            double sideOffset = 0.55D;
            double seatY = this.getY() + 1.35D;

            double x = this.getX() + facing.getStepX() * forwardOffset + right.getStepX() * sideOffset;
            double z = this.getZ() + facing.getStepZ() * forwardOffset + right.getStepZ() * sideOffset;

            callback.accept(passenger, x, seatY, z);
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        return passenger instanceof LivingEntity living ? living : null;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        ItemStack heldItem = player.getItemInHand(hand);

        // Sneak + Wrench -> dismantle paver into item & drop contents
        if (player.isSecondaryUseActive() && heldItem.getItem() instanceof WrenchItem) {
            if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
                this.clearWorkLight();
                this.dropAllInventory(serverLevel);
                this.spawnAtLocation(serverLevel, new ItemStack(this.getDropItem()));
                this.discard();
            }
            return InteractionResult.SUCCESS;
        }

        // Wrench or sneak -> open GUI
        if (heldItem.getItem() instanceof WrenchItem || player.isSecondaryUseActive()) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.openCustomGui(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }

        // Refuel interaction
        if (this.isFuelItem(heldItem)) {
            this.refuelFromItem(heldItem, player);
            return InteractionResult.SUCCESS;
        }

        // Mount operator station if empty
        if (this.getPassengers().isEmpty() && heldItem.isEmpty()) {
            if (!this.level().isClientSide()) {
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }

        // Riding player right click with empty hand -> open GUI
        if (this.hasPassenger(player) && heldItem.isEmpty()) {
            if (player instanceof ServerPlayer serverPlayer) {
                this.openCustomGui(serverPlayer);
            }
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            this.openCustomGui(serverPlayer);
        }
        return InteractionResult.SUCCESS;
    }

    public void openCustomGui(ServerPlayer player) {
        player.openMenu(this, buf -> buf.writeInt(this.getId()));
    }

    @Override
    public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buf) {
        buf.writeInt(this.getId());
    }

    public void dropAllInventory(ServerLevel serverLevel) {
        for (int i = 0; i < this.getContainerSize(); i++) {
            ItemStack stack = this.getItem(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(serverLevel, this.getX(), this.getY() + 0.5, this.getZ(), stack);
                this.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        }
        if (!this.isRemoved()) {
            this.clearWorkLight();
            if (source.getEntity() instanceof Player player && player.isCreative()) {
                this.dropAllInventory(level);
                this.discard();
                return true;
            }
            this.dropAllInventory(level);
            this.spawnAtLocation(level, new ItemStack(this.getDropItem()));
            this.discard();
            return true;
        }
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            if (this.isActive()) {
                float speedMultiplier = this.isWorkMode() ? 1.0F : 4.0F;
                float treadSpeed = (this.getRunMode() == MODE_REVERSE) ? -0.15F * speedMultiplier : 0.15F * speedMultiplier;
                this.treadRotation += treadSpeed;

                // Vertical exhaust smoke
                Direction facing = this.getFacing();
                Direction right = facing.getClockWise();
                double stackX = this.getX() + facing.getStepX() * 0.20D + right.getStepX() * -0.30D;
                double stackZ = this.getZ() + facing.getStepZ() * 0.20D + right.getStepZ() * -0.30D;
                double stackY = this.getY() + 2.15D;

                this.level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, stackX, stackY, stackZ, 0.0, 0.08, 0.0);
            }
            return;
        }

        // Server tick
        if (this.isWorkLightEnabled() && !this.isRemoved()) {
            this.updateWorkLight();
        } else {
            this.clearWorkLight();
        }

        if (this.outOfMaterialNotifyCooldown > 0) {
            this.outOfMaterialNotifyCooldown--;
        }

        if (this.currentStepPos == null) {
            this.currentStepPos = BlockPos.containing(this.position());
        }

        // Rider controls
        Entity rider = this.getFirstPassenger();
        if (rider instanceof LivingEntity livingRider) {
            this.handleRiderControls(livingRider);
        }

        // Execution if running
        int mode = this.getRunMode();
        if (mode == MODE_FORWARD) {
            this.tickMoveStep(this.getFacing(), false);
        } else if (mode == MODE_REVERSE) {
            this.tickMoveStep(this.getFacing().getOpposite(), true);
        } else {
            this.setActive(false);
        }
    }

    private void handleRiderControls(LivingEntity rider) {
        float forwardInput = rider.zza;
        float strafeInput = rider.xxa;

        if (this.getCardinalLock() == LOCK_OFF && Math.abs(strafeInput) > 0.05F) {
            float newYaw = this.getYRot() + strafeInput * 2.5F;
            this.setYRot(newYaw);
            this.setFacing(Direction.fromYRot(newYaw));
        }

        if (forwardInput > 0.1F) {
            if (this.getRunMode() == MODE_STOPPED) {
                this.setRunMode(MODE_FORWARD);
            }
        } else if (forwardInput < -0.1F) {
            if (this.getRunMode() == MODE_STOPPED) {
                this.setRunMode(MODE_REVERSE);
            }
        }
    }

    protected boolean isBlockPassable(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        return state.isAir() || state.getCollisionShape(this.level(), pos).isEmpty() || state.canBeReplaced();
    }

    protected boolean isBlockSolidGround(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        return !state.isAir() && (state.isSolid() || !state.getCollisionShape(this.level(), pos).isEmpty());
    }

    protected boolean isWallObstacle(BlockPos pos) {
        return isBlockSolidGround(pos) && isBlockSolidGround(pos.above());
    }

    protected void tickMoveStep(Direction travelDir, boolean isReversing) {
        if (this.isWorkMode() && !this.hasPaverMaterial()) {
            this.setRunMode(MODE_STOPPED);
            this.notifyOutOfMaterial(null);
            return;
        }

        int ticksPerBlock = this.getTicksPerBlock(isReversing);

        if (!this.isMoving) {
            if (this.currentStepPos == null) {
                this.currentStepPos = this.blockPosition();
            }

            BlockPos forwardPos = this.currentStepPos.relative(travelDir);
            Direction leftDir = travelDir.getCounterClockWise();
            Direction rightDir = travelDir.getClockWise();

            BlockPos chosenTarget = null;

            // Check if terrain steps UP 1 block
            if (isBlockSolidGround(forwardPos)) {
                BlockPos upPos = forwardPos.above();
                if (isBlockPassable(upPos) && isBlockPassable(upPos.above()) && isBlockPassable(this.currentStepPos.above(2))) {
                    if (this.checkSideClearance(upPos, leftDir, rightDir)) {
                        chosenTarget = upPos;
                    }
                }
            } else if (isBlockPassable(forwardPos)) {
                if (isBlockPassable(forwardPos.above())) {
                    BlockPos groundPos = forwardPos.below();
                    if (isBlockSolidGround(groundPos)) {
                        if (this.checkSideClearance(forwardPos, leftDir, rightDir)) {
                            chosenTarget = forwardPos;
                        }
                    } else {
                        // 1-block descent
                        BlockPos downPos = forwardPos.below();
                        BlockPos downGround = downPos.below();
                        if (isBlockPassable(downPos) && isBlockSolidGround(downGround)) {
                            if (this.checkSideClearance(downPos, leftDir, rightDir)) {
                                chosenTarget = downPos;
                            }
                        }
                    }
                }
            }

            if (chosenTarget == null) {
                this.setRunMode(MODE_STOPPED);
                return;
            }

            // Impassable wall check (bedrock)
            if (this.level().getBlockState(chosenTarget).getDestroySpeed(this.level(), chosenTarget) < 0.0F
                    || this.level().getBlockState(chosenTarget.above()).getDestroySpeed(this.level(), chosenTarget.above()) < 0.0F) {
                this.setRunMode(MODE_STOPPED);
                return;
            }

            this.isMoving = true;
            this.moveTicks = 0;
            this.targetStepPos = chosenTarget;
        }

        if (!this.consumeFuelTick()) {
            this.setRunMode(MODE_STOPPED);
            return;
        }

        this.setActive(true);
        this.moveTicks++;

        double frac = (double) this.moveTicks / (double) ticksPerBlock;
        Vec3 start = Vec3.atBottomCenterOf(this.currentStepPos);
        Vec3 end = Vec3.atBottomCenterOf(this.targetStepPos);
        Vec3 target = start.lerp(end, frac);

        this.setPos(target.x, target.y, target.z);

        if (this.moveTicks == ticksPerBlock / 2 && this.isWorkMode()) {
            this.executePavingStrip(this.targetStepPos, travelDir);
            if (this.getRunMode() == MODE_STOPPED) {
                return;
            }
        }

        if (this.moveTicks >= ticksPerBlock) {
            this.currentStepPos = this.targetStepPos;
            this.targetStepPos = null;
            this.isMoving = false;
            this.moveTicks = 0;
            this.setPos(end.x, end.y, end.z);

            if (this.isWorkMode()) {
                this.executePavingStrip(this.currentStepPos, travelDir);
                if (this.getRunMode() == MODE_STOPPED) {
                    return;
                }
            }
        }
    }

    private boolean checkSideClearance(BlockPos centerPos, Direction leftDir, Direction rightDir) {
        int halfW = this.getPavingWidth() / 2;
        for (int i = 1; i <= halfW; i++) {
            BlockPos leftPos = centerPos.relative(leftDir, i);
            BlockPos rightPos = centerPos.relative(rightDir, i);
            if (!isBlockPassable(leftPos.above()) || isWallObstacle(leftPos)) return false;
            if (!isBlockPassable(rightPos.above()) || isWallObstacle(rightPos)) return false;
        }
        return true;
    }

    public void executePavingStrip(BlockPos centerPos, Direction forwardDir) {
        if (!this.hasPaverMaterial()) {
            this.setRunMode(MODE_STOPPED);
            this.notifyOutOfMaterial(null);
            return;
        }

        Direction leftDir = forwardDir.getCounterClockWise();
        Direction rightDir = forwardDir.getClockWise();
        BlockPos groundCenter = centerPos.below();

        int width = this.getPavingWidth();
        int halfW = width / 2;
        BlockPos[] strip = new BlockPos[width];

        for (int i = 0; i < width; i++) {
            int offset = i - halfW;
            if (offset < 0) {
                strip[i] = groundCenter.relative(leftDir, -offset);
            } else if (offset == 0) {
                strip[i] = groundCenter;
            } else {
                strip[i] = groundCenter.relative(rightDir, offset);
            }
        }

        boolean placedAny = false;
        SoundType soundType = null;

        for (int i = 0; i < width; i++) {
            BlockState targetState = this.getTargetPaverBlockState(i);
            if (targetState == null || targetState.isAir()) {
                continue;
            }

            if (this.paveSingleBlock(strip[i], targetState)) {
                placedAny = true;
                soundType = targetState.getSoundType();
            } else if (this.getRunMode() == MODE_STOPPED) {
                break;
            }
        }

        if (placedAny && soundType != null) {
            this.level().playSound(null, centerPos, soundType.getPlaceSound(), SoundSource.BLOCKS, 0.8F, soundType.getPitch() * 0.9F);
        }
    }

    protected boolean paveSingleBlock(BlockPos pos, BlockState targetState) {
        BlockState currentState = this.level().getBlockState(pos);
        if (currentState.equals(targetState)) {
            return false;
        }

        if (currentState.getDestroySpeed(this.level(), pos) < 0.0F) {
            return false;
        }

        if (!this.consumePaverMaterial(targetState.getBlock())) {
            this.setRunMode(MODE_STOPPED);
            this.notifyOutOfMaterial(null);
            return false;
        }

        if (!currentState.isAir() && !currentState.liquid()) {
            this.harvestReplacedBlock(pos, currentState);
        }

        this.level().setBlock(pos, targetState, Block.UPDATE_ALL);

        BlockPos abovePos = pos.above();
        BlockState aboveState = this.level().getBlockState(abovePos);
        if (aboveState.canBeReplaced() && !aboveState.isAir()) {
            this.level().destroyBlock(abovePos, false);
        }

        return true;
    }

    protected void harvestReplacedBlock(BlockPos pos, BlockState state) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        List<ItemStack> drops = Block.getDrops(state, serverLevel, pos, null);
        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;
            ItemStack remaining = this.insertIntoMaterialInventory(drop);
            if (!remaining.isEmpty()) {
                Containers.dropItemStack(this.level(), pos.getX(), pos.getY() + 0.5, pos.getZ(), remaining);
            }
        }
    }

    protected ItemStack insertIntoMaterialInventory(ItemStack stack) {
        ItemStack toInsert = stack.copy();
        int start = this.getMaterialStartSlot();
        for (int i = start; i < start + SLOT_MATERIAL_COUNT; i++) {
            ItemStack slotStack = this.getItem(i);
            if (slotStack.isEmpty()) {
                this.setItem(i, toInsert);
                return ItemStack.EMPTY;
            } else if (ItemStack.isSameItemSameComponents(slotStack, toInsert)) {
                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                int transfer = Math.min(space, toInsert.getCount());
                if (transfer > 0) {
                    slotStack.grow(transfer);
                    toInsert.shrink(transfer);
                    if (toInsert.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }
        return toInsert;
    }

    public @Nullable BlockState getTargetPaverBlockState(int tileIndex) {
        int filterCount = this.getFilterSlotCount();
        if (tileIndex >= 0 && tileIndex < filterCount) {
            ItemStack filterStack = this.getItem(tileIndex);
            if (!filterStack.isEmpty() && filterStack.getItem() instanceof BlockItem bi) {
                return bi.getBlock().defaultBlockState();
            }
        }

        if (this.areAllPatternSlotsEmpty()) {
            return this.getFirstValidBulkMaterialState();
        }

        return null;
    }

    public boolean areAllPatternSlotsEmpty() {
        int count = this.getFilterSlotCount();
        for (int s = 0; s < count; s++) {
            ItemStack stack = this.getItem(s);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                return false;
            }
        }
        return true;
    }

    public @Nullable BlockState getFirstValidBulkMaterialState() {
        int start = this.getMaterialStartSlot();
        for (int i = start; i < start + SLOT_MATERIAL_COUNT; i++) {
            ItemStack stack = this.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi) {
                return bi.getBlock().defaultBlockState();
            }
        }
        return null;
    }

    public boolean hasPaverMaterial() {
        Entity driver = this.getFirstPassenger();
        if (driver instanceof Player player && player.isCreative()) {
            return true;
        }

        int start = this.getMaterialStartSlot();

        if (this.areAllPatternSlotsEmpty()) {
            for (int i = start; i < start + SLOT_MATERIAL_COUNT; i++) {
                ItemStack stack = this.getItem(i);
                if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
                    return true;
                }
            }
            return false;
        }

        int filterCount = this.getFilterSlotCount();
        for (int s = 0; s < filterCount; s++) {
            ItemStack filterStack = this.getItem(s);
            if (!filterStack.isEmpty() && filterStack.getItem() instanceof BlockItem bi) {
                Block filterBlock = bi.getBlock();
                for (int i = start; i < start + SLOT_MATERIAL_COUNT; i++) {
                    ItemStack stack = this.getItem(i);
                    if (!stack.isEmpty() && stack.getItem() instanceof BlockItem matBi && matBi.getBlock() == filterBlock) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected boolean consumePaverMaterial(Block targetBlock) {
        Entity driver = this.getFirstPassenger();
        if (driver instanceof Player player && player.isCreative()) {
            return true;
        }

        int start = this.getMaterialStartSlot();
        for (int i = start; i < start + SLOT_MATERIAL_COUNT; i++) {
            ItemStack stack = this.getItem(i);
            if (!stack.isEmpty() && stack.getItem() instanceof BlockItem bi && bi.getBlock() == targetBlock) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    this.setItem(i, ItemStack.EMPTY);
                }
                this.setChanged();
                return true;
            }
        }
        return false;
    }

    public void notifyOutOfMaterial(@Nullable Player targetPlayer) {
        Component msg = Component.literal(this.getName().getString() + ": Out of paving material!").withStyle(ChatFormatting.RED);
        if (targetPlayer != null) {
            targetPlayer.sendOverlayMessage(msg);
            return;
        }
        if (this.outOfMaterialNotifyCooldown > 0) {
            return;
        }
        this.outOfMaterialNotifyCooldown = 40;

        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof Player rider) {
            rider.sendOverlayMessage(msg);
        } else {
            for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(8.0D))) {
                player.sendOverlayMessage(msg);
            }
        }
    }

    public void updateWorkLight() {
        if (this.level().isClientSide() || this.isRemoved()) {
            return;
        }
        BlockPos center = this.blockPosition().above(2);
        if (!this.level().hasChunkAt(center)) {
            return;
        }
        BlockState state = this.level().getBlockState(center);
        if (!state.isAir() && !state.is(Blocks.LIGHT)) {
            center = this.blockPosition().above();
            if (!this.level().hasChunkAt(center)) {
                return;
            }
            state = this.level().getBlockState(center);
        }

        if (!java.util.Objects.equals(this.currentLightPos, center)) {
            this.clearWorkLight();
            if (state.isAir() || state.is(Blocks.LIGHT)) {
                this.level().setBlock(center, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
                this.currentLightPos = center;
            }
        } else if (state.isAir()) {
            this.level().setBlock(center, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
        }
    }

    public void clearWorkLight() {
        if (this.currentLightPos != null && !this.level().isClientSide()) {
            if (this.level().hasChunkAt(this.currentLightPos)) {
                BlockState state = this.level().getBlockState(this.currentLightPos);
                if (state.is(Blocks.LIGHT)) {
                    this.level().setBlock(this.currentLightPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
            this.currentLightPos = null;
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide()) {
            this.clearWorkLight();
        }
        super.remove(reason);
    }

    // Container methods
    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.itemStacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return (slot >= 0 && slot < this.itemStacks.size()) ? this.itemStacks.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        return net.minecraft.world.ContainerHelper.removeItem(this.itemStacks, slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return net.minecraft.world.ContainerHelper.takeItem(this.itemStacks, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.itemStacks.size()) {
            this.itemStacks.set(slot, stack);
            this.setChanged();
        }
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return !this.isRemoved() && this.distanceToSqr(player) <= 64.0D;
    }

    @Override
    public void clearContent() {
        this.itemStacks.clear();
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Facing", this.getFacing().get3DDataValue());
        output.putInt("Fuel", this.getFuel());
        output.putInt("FuelTotal", this.getFuelTotal());
        output.putInt("RunMode", this.getRunMode());
        output.putInt("CardinalLock", this.getCardinalLock());
        output.putBoolean("Paving", this.isWorkMode());
        output.putBoolean("WorkLight", this.isWorkLightEnabled());
        output.putBoolean("Hologram", this.isHologramEnabled());

        for (int i = 0; i < this.itemStacks.size(); i++) {
            ItemStack stack = this.itemStacks.get(i);
            if (!stack.isEmpty()) {
                output.store("Slot_" + i, ItemStack.CODEC, stack);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        int facingVal = input.getIntOr("Facing", Direction.NORTH.get3DDataValue());
        this.setFacing(Direction.from3DDataValue(facingVal));
        this.setFuel(input.getIntOr("Fuel", 0));
        this.setFuelTotal(input.getIntOr("FuelTotal", 1600));
        this.setRunMode(input.getIntOr("RunMode", MODE_STOPPED));
        this.setCardinalLock(input.getIntOr("CardinalLock", LOCK_OFF));
        this.setWorkMode(input.getBooleanOr("Paving", true));
        this.setWorkLightEnabled(input.getBooleanOr("WorkLight", false));
        this.setHologramEnabled(input.getBooleanOr("Hologram", false));

        for (int i = 0; i < this.itemStacks.size(); i++) {
            final int slot = i;
            input.read("Slot_" + i, ItemStack.CODEC).ifPresent(s -> this.itemStacks.set(slot, s));
        }
    }
}
