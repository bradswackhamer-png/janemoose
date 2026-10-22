package com.swack.janemoose.client.renderer;

import com.swack.janemoose.item.IAttachment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class AttachmentRenderState extends EntityRenderState {
    public float yRot;
    public boolean isLowered = true;
    public boolean isActive = true;
    public IAttachment.AttachmentType type = IAttachment.AttachmentType.CULTIVATOR;
    public int width = 1;
    public int tier = 0; // 0 = Wood, 1 = Iron, 2 = Diamond
    public float animationTicks;
    public boolean isMoving = false;
}
