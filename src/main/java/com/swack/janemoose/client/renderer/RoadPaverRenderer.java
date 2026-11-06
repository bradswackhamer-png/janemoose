package com.swack.janemoose.client.renderer;

import com.swack.janemoose.client.model.RoadPaverModel;
import com.swack.janemoose.entity.RoadPaverEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RoadPaverRenderer extends EntityRenderer<RoadPaverEntity, RoadPaverRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("janemoose", "textures/entity/road_paver.png");

    private final RoadPaverModel model;

    public RoadPaverRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.25F;
        this.model = new RoadPaverModel(context.bakeLayer(RoadPaverModel.ROAD_PAVER_LAYER));
    }

    @Override
    public RoadPaverRenderState createRenderState() {
        return new RoadPaverRenderState();
    }

    @Override
    public void extractRenderState(RoadPaverEntity entity, RoadPaverRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.facing = entity.getFacing();
        state.yRot = entity.getYRot();
        state.active = entity.isActive();
        state.paving = entity.isWorkMode();
        state.treadRotation = entity.treadRotation;
        state.hurtTime = Math.max(entity.hurtTime - partialTicks, 0.0F);
        state.hurtDir = 1;
        state.damageTime = 0.0F;
        state.workLight = entity.isWorkLightEnabled();
        state.showHologram = entity.isHologramEnabled();
        state.isWorkMode = entity.isWorkMode();
        state.pavingWidth = entity.getPavingWidth();

        if (state.workLight) {
            state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
        }

        if (state.showHologram) {
            extractHologram(entity, state, 3);
        }
    }

    public static void extractHologram(com.swack.janemoose.entity.BaseRoadPaverEntity entity, RoadPaverRenderState state, int width) {
        Direction forward = entity.getFacing();
        Direction left = forward.getCounterClockWise();
        Direction right = forward.getClockWise();
        BlockPos currentPos = entity.blockPosition();
        BlockPos stepPos = currentPos;
        int idx = 0;
        int halfW = width / 2;

        for (int step = 1; step <= 3; step++) {
            BlockPos nextPos = stepPos.relative(forward);
            BlockState nextFeet = entity.level().getBlockState(nextPos);
            BlockState nextHead = entity.level().getBlockState(nextPos.above());
            if (nextFeet.isSolid() && !nextHead.isSolid()) {
                nextPos = nextPos.above();
            } else if (!nextFeet.isSolid() && !entity.level().getBlockState(nextPos.below()).isSolid()) {
                BlockPos downPos = nextPos.below();
                if (entity.level().getBlockState(downPos).isSolid() || entity.level().getBlockState(downPos.below()).isSolid()) {
                    nextPos = downPos;
                }
            }
            stepPos = nextPos;
            BlockPos groundCenter = nextPos.below();

            for (int col = 0; col < width; col++) {
                int offset = col - halfW;
                BlockPos targetPos;
                if (offset < 0) {
                    targetPos = groundCenter.relative(left, -offset);
                } else if (offset == 0) {
                    targetPos = groundCenter;
                } else {
                    targetPos = groundCenter.relative(right, offset);
                }

                if (entity.level().getBlockState(targetPos.above()).isSolid()) {
                    targetPos = targetPos.above();
                } else if (!entity.level().getBlockState(targetPos).isSolid() && entity.level().getBlockState(targetPos.below()).isSolid()) {
                    targetPos = targetPos.below();
                }

                if (idx < state.hologramPositions.length) {
                    state.hologramPositions[idx++] = targetPos;
                }
            }
        }
    }

    @Override
    public void submit(RoadPaverRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        super.submit(state, poseStack, submitNodeCollector, camera);
        poseStack.pushPose();

        poseStack.rotateDegrees(Axis.YP, 180.0F - state.facing.toYRot());

        if (state.hurtTime > 0.0F) {
            poseStack.rotateDegrees(Axis.XP, Mth.sin(state.hurtTime) * state.hurtTime * state.damageTime / 10.0F * state.hurtDir);
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        submitNodeCollector.submitModel(this.model, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        poseStack.popPose();

        // 3x3 Ghost Hologram Grid
        if (state.showHologram) {
            int holoColor = state.isWorkMode ? 0xE000E5FF : 0xE0FFAA00;
            int total = state.pavingWidth * 3;
            for (int i = 0; i < total; i++) {
                BlockPos bp = state.hologramPositions[i];
                if (bp == null) continue;
                poseStack.pushPose();
                double dx = (double) bp.getX() - state.x;
                double dy = (double) bp.getY() - state.y;
                double dz = (double) bp.getZ() - state.z;
                poseStack.translate(dx, dy, dz);
                submitNodeCollector.submitShapeOutline(poseStack, Shapes.block(), RenderTypes.linesTranslucent(), holoColor, 2.5F, false);
                poseStack.popPose();
            }
        }
    }
}
