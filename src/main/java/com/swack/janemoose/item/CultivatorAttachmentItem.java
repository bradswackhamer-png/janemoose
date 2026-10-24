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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Consumer;

public class CultivatorAttachmentItem extends Item implements IAttachment {
    private final int width;

    public CultivatorAttachmentItem(Properties properties, int width) {
        super(properties);
        this.width = width;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.CULTIVATOR;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    private boolean isTillable(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT_PATH) || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT);
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

    private void tillArea(ServerLevel level, BlockPos centerPos, Player player, Direction facing) {
        Direction perp = facing.getClockWise();
        int[] offsets = (this.width == 1) ? new int[]{0} : (this.width == 2 ? new int[]{0, 1} : new int[]{-1, 0, 1});

        boolean anyTilled = false;
        for (int offset : offsets) {
            BlockPos pos = centerPos.relative(perp, offset);
            if (tillSingleBlock(level, pos, player)) {
                anyTilled = true;
            }
        }
        if (anyTilled) {
            level.playSound(null, centerPos, SoundEvents.HOE_TILL.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public boolean tillSingleBlock(ServerLevel level, BlockPos pos, Player player) {
        // Search vertical range from pos.above(2) down to pos.below(3) to reliably find ground surface
        for (int dy = 2; dy >= -3; dy--) {
            BlockPos groundPos = pos.above(dy);
            BlockState state = level.getBlockState(groundPos);
            BlockPos abovePos = groundPos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            // Already farmland
            if (state.is(Blocks.FARMLAND)) {
                return false;
            }

            boolean isGrass = state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT_PATH) || state.is(Blocks.DIRT);
            boolean isCoarse = state.is(Blocks.COARSE_DIRT) || state.is(Blocks.ROOTED_DIRT);

            if (isGrass || isCoarse) {
                // If there is short grass, tall grass, fern, flower, or replaceable plant on top, clear it
                if (!aboveState.isAir()) {
                    if (aboveState.is(BlockTags.REPLACEABLE) || aboveState.is(BlockTags.FLOWERS)
                            || aboveState.is(Blocks.SHORT_GRASS) || aboveState.is(Blocks.TALL_GRASS)
                            || aboveState.is(Blocks.FERN) || aboveState.is(Blocks.LARGE_FERN)
                            || aboveState.canBeReplaced()) {
                        level.destroyBlock(abovePos, true, player);
                    } else {
                        continue;
                    }
                }

                BlockState newState = isCoarse ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
                level.setBlock(groundPos, newState, 3);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, groundPos);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean processBlock(ServerLevel level, BlockPos pos, JaneMooseTractorEntity tractor, JaneMooseAttachmentEntity attachment, Player rider) {
        boolean tilled = tillSingleBlock(level, pos, rider);
        if (tilled) {
            level.playSound(null, pos, SoundEvents.HOE_TILL.value(), SoundSource.BLOCKS, 0.6F, 1.0F);
        }
        return tilled;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        String tierKey = width == 1 ? "wood" : (width == 2 ? "iron" : "diamond");
        tooltipComponents.accept(Component.translatable("tooltip.janemoose.cultivator." + tierKey));
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
