package com.swack.janemoose.item;

import com.swack.janemoose.entity.JaneMooseTractorEntity;
import com.swack.janemoose.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class TractorItem extends Item {
    public TractorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos clickedPos = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockPos spawnPos = clickedPos.relative(face);

        if (level instanceof ServerLevel serverLevel) {
            JaneMooseTractorEntity tractor = ModEntities.TRACTOR.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
            if (tractor != null) {
                tractor.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                tractor.setYRot(context.getRotation());
                serverLevel.addFreshEntity(tractor);
                serverLevel.playSound(null, spawnPos, SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);

                if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.accept(Component.translatable("tooltip.janemoose.tractor"));
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
