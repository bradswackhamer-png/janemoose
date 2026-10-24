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
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class PlanterAttachmentItem extends Item implements IAttachment {
    private final int width;

    public PlanterAttachmentItem(Properties properties, int width) {
        super(properties);
        this.width = width;
    }

    @Override
    public AttachmentType getType() {
        return AttachmentType.PLANTER;
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
            BlockPos groundPos = pos.above(dy);
            BlockPos plantPos = groundPos.above();

            BlockState groundState = level.getBlockState(groundPos);
            BlockState plantState = level.getBlockState(plantPos);

            if (!groundState.is(Blocks.FARMLAND) || !plantState.isAir()) {
                continue;
            }

            // Find seeds in attachment hopper first!
            ItemStack seedStack = (attachment != null) ? findSeedsInContainer(attachment.getInventory()) : ItemStack.EMPTY;
            boolean fromPlayer = false;

            if (seedStack.isEmpty() && rider != null) {
                seedStack = findSeedsInContainer(rider.getInventory());
                fromPlayer = true;
            }

            if (seedStack.isEmpty()) {
                continue;
            }

            BlockState toPlant = null;
            if (seedStack.is(Items.WHEAT_SEEDS)) {
                toPlant = Blocks.WHEAT.defaultBlockState();
            } else if (seedStack.is(Items.CARROT)) {
                toPlant = Blocks.CARROTS.defaultBlockState();
            } else if (seedStack.is(Items.POTATO)) {
                toPlant = Blocks.POTATOES.defaultBlockState();
            } else if (seedStack.is(Items.BEETROOT_SEEDS)) {
                toPlant = Blocks.BEETROOTS.defaultBlockState();
            } else if (seedStack.is(Items.MELON_SEEDS)) {
                toPlant = Blocks.MELON_STEM.defaultBlockState();
            } else if (seedStack.is(Items.PUMPKIN_SEEDS)) {
                toPlant = Blocks.PUMPKIN_STEM.defaultBlockState();
            } else if (seedStack.is(Items.TORCHFLOWER_SEEDS)) {
                toPlant = Blocks.TORCHFLOWER_CROP.defaultBlockState();
            }

            if (toPlant != null) {
                level.setBlock(plantPos, toPlant, 3);
                level.playSound(null, plantPos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (fromPlayer) {
                    if (rider != null && !rider.isCreative()) {
                        seedStack.shrink(1);
                    }
                } else {
                    seedStack.shrink(1);
                }
                return true;
            }
        }

        return false;
    }

    private ItemStack findSeedsInContainer(Container container) {
        if (container == null) return ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.is(Items.WHEAT_SEEDS) || stack.is(Items.CARROT) || stack.is(Items.POTATO) ||
                stack.is(Items.BEETROOT_SEEDS) || stack.is(Items.MELON_SEEDS) || stack.is(Items.PUMPKIN_SEEDS) ||
                stack.is(Items.TORCHFLOWER_SEEDS)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        String tierKey = width == 1 ? "wood" : (width == 2 ? "iron" : "diamond");
        tooltipComponents.accept(Component.translatable("tooltip.janemoose.planter." + tierKey));
        super.appendHoverText(stack, context, display, tooltipComponents, tooltipFlag);
    }
}
