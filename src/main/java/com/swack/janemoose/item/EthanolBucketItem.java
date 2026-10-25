package com.swack.janemoose.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class EthanolBucketItem extends Item {
    public static final int FUEL_VALUE = 1000;

    public EthanolBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.janemoose.ethanol_bucket"));
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
