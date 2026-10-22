package com.swack.janemoose.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.swack.janemoose.client.model.TractorModel;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class TractorRenderer extends EntityRenderer<JaneMooseTractorEntity, TractorRenderState> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("janemoose", "textures/entity/tractor.png");

    private final TractorModel model;

    public TractorRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 1.0F;
        this.model = new TractorModel(context.bakeLayer(TractorModel.LAYER_LOCATION));
    }

    @Override
    public TractorRenderState createRenderState() {
        return new TractorRenderState();
    }

    @Override
    public void extractRenderState(JaneMooseTractorEntity entity, TractorRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getViewYRot(partialTicks);
        state.wheelAngle = entity.getWheelRotation(partialTicks);
        state.attachment = entity.getAttachment();
    }

    @Override
    public void submit(TractorRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, 180.0F - state.yRot);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);

        submitNodeCollector.submitModel(this.model, state, poseStack, TEXTURE, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
