package com.swack.janemoose.client.renderer;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.item.ItemStack;

public class TractorRenderState extends EntityRenderState {
    public float yRot;
    public float wheelAngle;
    public ItemStack attachment = ItemStack.EMPTY;
}
