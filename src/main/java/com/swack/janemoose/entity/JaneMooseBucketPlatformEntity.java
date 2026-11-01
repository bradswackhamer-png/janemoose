package com.swack.janemoose.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Companion aerial platform collider & interaction entity that tracks the tip of the Bucket Truck's boom.
 * Provides a solid walkable floor at the aerial bucket so players who teleport to or dismount in the bucket
 * can stand freely on the platform, or right-click the bucket directly to sit in it.
 */
public class JaneMooseBucketPlatformEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_TRUCK_ID =
            SynchedEntityData.defineId(JaneMooseBucketPlatformEntity.class, EntityDataSerializers.INT);

    public JaneMooseBucketPlatformEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TRUCK_ID, -1);
    }

    public int getTruckId() {
        return this.entityData.get(DATA_TRUCK_ID);
    }

    public void setTruckId(int id) {
        this.entityData.set(DATA_TRUCK_ID, id);
    }

    public JaneMooseBucketTruckEntity getTruck() {
        int id = getTruckId();
        if (id == -1) return null;
        Entity ent = this.level().getEntity(id);
        return ent instanceof JaneMooseBucketTruckEntity truck && truck.isAlive() ? truck : null;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPickable() {
        return this.isAlive();
    }

    @Override
    public boolean canBeCollidedWith(Entity entity) {
        if (entity instanceof JaneMooseBucketTruckEntity) {
            return false;
        }
        if (entity != null && entity.getVehicle() instanceof JaneMooseBucketTruckEntity) {
            return false;
        }
        return this.isAlive();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        JaneMooseBucketTruckEntity truck = getTruck();
        if (truck == null) {
            return InteractionResult.PASS;
        }
        // Right-clicking the aerial bucket platform mounts the player into the bucket seat
        if (!this.level().isClientSide()) {
            truck.mountInBucket(player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);

        JaneMooseBucketTruckEntity truck = getTruck();
        if (truck == null) {
            if (!this.level().isClientSide() && this.tickCount > 10) {
                this.discard();
            }
            return;
        }

        Vec3 targetPos = truck.getBucketWorldPos(1.0F);
        double dx = targetPos.x - this.getX();
        double dy = targetPos.y - this.getY();
        double dz = targetPos.z - this.getZ();

        // Carry any standing non-riding entities smoothly along with the aerial platform
        if (Math.abs(dx) > 0.0001 || Math.abs(dy) > 0.0001 || Math.abs(dz) > 0.0001) {
            AABB standBox = this.getBoundingBox().inflate(0.25, 0.65, 0.25).move(0.0, 0.35, 0.0);
            List<Entity> standingEntities = this.level().getEntities(this, standBox,
                    e -> e != truck && e.getVehicle() != truck && !(e instanceof JaneMooseBucketPlatformEntity));
            for (Entity standing : standingEntities) {
                standing.move(MoverType.SHULKER_BOX, new Vec3(dx, Math.max(0.0, dy), dz));
                if (dy < 0.0) {
                    standing.setPos(standing.getX(), standing.getY() + dy, standing.getZ());
                }
                standing.fallDistance = 0.0F;
            }
        }

        this.setPos(targetPos.x, targetPos.y, targetPos.z);
        this.setYRot(truck.getYRot() + truck.getBoomYaw());
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        JaneMooseBucketTruckEntity truck = getTruck();
        if (truck != null) {
            return truck.hurtServer(level, source, amount);
        }
        this.discard();
        return true;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        setTruckId(input.getIntOr("TruckId", -1));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("TruckId", getTruckId());
    }
}
