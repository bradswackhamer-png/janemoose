package com.swack.janemoose.client.renderer;

import com.swack.janemoose.client.model.RoadPaverModel;
import com.swack.janemoose.entity.DieselRoadPaverEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DieselRoadPaverRenderer extends EntityRenderer<DieselRoadPaverEntity, RoadPaverRenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("janemoose", "textures/entity/diesel_road_paver.png");

    private final RoadPaverModel model;

    public DieselRoadPaverRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.50F;
        this.model = new RoadPaverModel(context.bakeLayer(RoadPaverModel.DIESEL_ROAD_PAVER_LAYER));
    }

    @Override
    public RoadPaverRenderState createRenderState() {
        return new RoadPaverRenderState();
    }

    @Override
    public void extractRenderState(DieselRoadPaverEntity entity, RoadPaverRenderState state, float partialTicks) {
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
        state.pavingWidth = 5;

        if (state.workLight) {
            state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
        }

        if (state.showHologram) {
            RoadPaverRenderer.extractHologram(entity, state, 5);
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

        // 5x3 Ghost Hologram Alignment Grid
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
