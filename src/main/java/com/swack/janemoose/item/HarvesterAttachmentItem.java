package com.swack.janemoose.item;

import com.swack.janemoose.entity.JaneMooseAttachmentEntity;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class HarvesterAttachmentItem extends Item implements IAttachment {
    private final int width;

    public HarvesterAttachmentItem(Properties properties, int width) {
        super(properties);
        this.width = width;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.HARVESTER;
    }

    @Override
    public int getWidth() {
        return this.width;
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
        Player player = context.getPlayer();

        if (level instanceof ServerLevel serverLevel) {
            JaneMooseAttachmentEntity attachment = ModEntities.ATTACHMENT.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);
            if (attachment != null) {
                ItemStack stackToPlace = context.getItemInHand().copy();
                stackToPlace.setCount(1);
                attachment.setItem(stackToPlace);
                attachment.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                attachment.setYRot(context.getRotation());
                serverLevel.addFreshEntity(attachment);
                serverLevel.playSound(null, spawnPos, SoundEvents.ARMOR_EQUIP_IRON.value(), SoundSource.BLOCKS, 0.8F, 1.0F);

                if (player != null && !player.isCreative()) {
                    context.getItemInHand().shrink(1);
                }
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean processBlock(ServerLevel level, BlockPos pos, JaneMooseTractorEntity tractor, JaneMooseAttachmentEntity attachment, Player rider) {
        for (int dy = 1; dy >= -1; dy--) {
            BlockPos targetPos = pos.above(dy);
            BlockState state = level.getBlockState(targetPos);

            if (state.is(Blocks.FARMLAND) || state.is(Blocks.DIRT)) {
                targetPos = targetPos.above();
                state = level.getBlockState(targetPos);
            }

            if (state.getBlock() instanceof CropBlock cropBlock) {
                if (cropBlock.isMaxAge(state)) {
                    List<ItemStack> drops = Block.getDrops(state, level, targetPos, null);
                    storeOrDrop(level, targetPos, attachment, drops);
                    level.setBlock(targetPos, cropBlock.getStateForAge(0), 3);
                    level.playSound(null, targetPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return true;
                }
            } else if (state.is(Blocks.PUMPKIN) || state.is(Blocks.MELON)) {
                List<ItemStack> drops = Block.getDrops(state, level, targetPos, null);
                level.destroyBlock(targetPos, false);
                storeOrDrop(level, targetPos, attachment, drops);
                return true;
            } else if (state.is(Blocks.SUGAR_CANE) && level.getBlockState(targetPos.below()).is(Blocks.SUGAR_CANE)) {
                List<ItemStack> drops = Block.getDrops(state, level, targetPos, null);
                level.destroyBlock(targetPos, false);
                storeOrDrop(level, targetPos, attachment, drops);
                return true;
            } else if (state.getBlock() instanceof CocoaBlock) {
                if (state.getValue(CocoaBlock.AGE) >= 2) {
                    List<ItemStack> drops = Block.getDrops(state, level, targetPos, null);
                    storeOrDrop(level, targetPos, attachment, drops);
                    level.setBlock(targetPos, state.setValue(CocoaBlock.AGE, 0), 3);
                    level.playSound(null, targetPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return true;
                }
            } else if (state.getBlock() instanceof NetherWartBlock) {
                if (state.getValue(NetherWartBlock.AGE) >= 3) {
                    List<ItemStack> drops = Block.getDrops(state, level, targetPos, null);
                    storeOrDrop(level, targetPos, attachment, drops);
                    level.setBlock(targetPos, state.setValue(NetherWartBlock.AGE, 0), 3);
                    level.playSound(null, targetPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    return true;
                }
            }
        }

        return false;
    }

    private void storeOrDrop(ServerLevel level, BlockPos pos, JaneMooseAttachmentEntity attachment, List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            ItemStack toStore = drop.copy();
            if (attachment != null) {
                toStore = attachment.getInventory().addItem(toStore);
            }
            if (!toStore.isEmpty()) {
                Block.popResource(level, pos, toStore);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        String tierKey = width == 1 ? "wood" : (width == 2 ? "iron" : "diamond");
        tooltipComponents.accept(Component.translatable("tooltip.janemoose.harvester." + tierKey));
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
