package com.swack.janemoose.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.swack.janemoose.client.model.AttachmentModel;
import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.item.IAttachment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class AttachmentRenderer extends EntityRenderer<JaneMooseAttachmentEntity, AttachmentRenderState> {
    private static final Identifier TEXTURE_WOOD =
            Identifier.fromNamespaceAndPath("janemoose", "textures/entity/attachment_wood.png");
    private static final Identifier TEXTURE_IRON =
            Identifier.fromNamespaceAndPath("janemoose", "textures/entity/attachment_iron.png");
    private static final Identifier TEXTURE_DIAMOND =
            Identifier.fromNamespaceAndPath("janemoose", "textures/entity/attachment_diamond.png");

    private final AttachmentModel model;

    public AttachmentRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.8F;
        this.model = new AttachmentModel(context.bakeLayer(AttachmentModel.LAYER_LOCATION));
    }

    @Override
    public AttachmentRenderState createRenderState() {
        return new AttachmentRenderState();
    }

    @Override
    public void extractRenderState(JaneMooseAttachmentEntity entity, AttachmentRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yRot = entity.getViewYRot(partialTicks);
        state.isLowered = entity.isLowered();
        state.isActive = entity.isActive();
        state.animationTicks = entity.tickCount + partialTicks;

        JaneMooseTractorEntity tractor = entity.getHitchedTractor();
        state.isMoving = tractor != null && tractor.isMoving();

        ItemStack stack = entity.getItem();
        if (stack.getItem() instanceof IAttachment attachment) {
            state.type = attachment.getType();
            state.width = attachment.getWidth();
            if (state.width == 1) {
                state.tier = 0; // Wood
            } else if (state.width == 2) {
                state.tier = 1; // Iron
            } else {
                state.tier = 2; // Diamond
            }
        } else {
            state.type = IAttachment.AttachmentType.CULTIVATOR;
            state.width = 1;
            state.tier = 0;
        }
    }

    @Override
    public void submit(AttachmentRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, 180.0F - state.yRot);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, -1.5F, 0.0F);

        Identifier texture = switch (state.tier) {
            case 1 -> TEXTURE_IRON;
            case 2 -> TEXTURE_DIAMOND;
            default -> TEXTURE_WOOD;
        };

        submitNodeCollector.submitModel(this.model, state, poseStack, texture, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
