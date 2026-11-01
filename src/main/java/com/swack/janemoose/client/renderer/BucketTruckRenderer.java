package com.swack.janemoose.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.swack.janemoose.client.model.BucketTruckModel;
import com.swack.janemoose.entity.JaneMooseBucketTruckEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class BucketTruckRenderer extends EntityRenderer<JaneMooseBucketTruckEntity, BucketTruckRenderState> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("janemoose", "textures/entity/bucket_truck.png");

    private final BucketTruckModel model;

    public BucketTruckRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.2F;
        this.model = new BucketTruckModel(context.bakeLayer(BucketTruckModel.LAYER_LOCATION));
    }

    @Override
    public BucketTruckRenderState createRenderState() {
        return new BucketTruckRenderState();
    }

    @Override
    public boolean shouldRender(JaneMooseBucketTruckEntity entity, Frustum frustum, double camX, double camY, double camZ, float partialTicks) {
        if (super.shouldRender(entity, frustum, camX, camY, camZ, partialTicks)) {
            return true;
        }
        // Keep rendering the truck even when the camera is looking high up at the extended aerial bucket
        return frustum.isVisible(entity.getBoundingBox().inflate(6.0, 10.0, 6.0));
    }

    @Override
    public void extractRenderState(JaneMooseBucketTruckEntity entity, BucketTruckRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getViewYRot(partialTicks);
        state.wheelAngle = entity.getWheelRotation(partialTicks);
        state.boomExtension = entity.getBoomExtension(partialTicks);
        state.boomYaw = entity.getBoomYaw(partialTicks);
    }

    @Override
    public void submit(BucketTruckRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, 180.0F - state.yRot);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);

        submitNodeCollector.submitModel(this.model, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
