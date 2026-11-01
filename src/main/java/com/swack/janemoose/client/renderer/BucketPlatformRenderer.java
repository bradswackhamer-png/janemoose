package com.swack.janemoose.client.renderer;

import com.swack.janemoose.entity.JaneMooseBucketPlatformEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class BucketPlatformRenderer extends EntityRenderer<JaneMooseBucketPlatformEntity, EntityRenderState> {
    public BucketPlatformRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
