package com.swack.janemoose.block;

import com.swack.janemoose.block.entity.FermentationBarrelBlockEntity;
import com.swack.janemoose.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class FermentationBarrelBlock extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0),
            Block.box(1.0, 2.0, 1.0, 15.0, 14.0, 15.0),
            Block.box(2.0, 14.0, 2.0, 14.0, 16.0, 14.0)
    );

    public FermentationBarrelBlock(Properties properties) {
        super(properties.strength(2.5f, 3.5f)
                .sound(SoundType.WOOD)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        // Quick draw Ethanol with an Empty Bucket directly from the barrel
        if (stack.is(Items.BUCKET) && level.getBlockEntity(pos) instanceof FermentationBarrelBlockEntity barrelBE) {
            if (barrelBE.getEthanolAmount() >= 1000) {
                if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                    barrelBE.setEthanolAmount(barrelBE.getEthanolAmount() - 1000);
                    ItemStack ethanolBucket = new ItemStack(ModItems.ETHANOL_BUCKET.get());
                    if (!player.isCreative()) {
                        stack.shrink(1);
                        if (stack.isEmpty()) {
                            player.setItemInHand(hand, ethanolBucket);
                        } else if (!player.getInventory().add(ethanolBucket)) {
                            Containers.dropItemStack(serverLevel, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, ethanolBucket);
                        }
                    } else {
                        if (!player.getInventory().add(ethanolBucket)) {
                            Containers.dropItemStack(serverLevel, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, ethanolBucket);
                        }
                    }
                    level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                    player.sendOverlayMessage(Component.translatable("message.janemoose.barrel_filled_bucket", barrelBE.getEthanolAmount(), FermentationBarrelBlockEntity.TANK_CAPACITY));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof FermentationBarrelBlockEntity barrelBE) {
                player.openMenu(barrelBE);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            if (random.nextFloat() < 0.35f) {
                double x = pos.getX() + 0.4 + random.nextDouble() * 0.2;
                double y = pos.getY() + 1.02;
                double z = pos.getZ() + 0.4 + random.nextDouble() * 0.2;
                level.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0.0, 0.03, 0.0);
            }
            if (random.nextInt(25) == 0) {
                level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS, 0.35f, 0.8f + random.nextFloat() * 0.3f, false);
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof FermentationBarrelBlockEntity barrelBE) {
            Containers.dropContents(level, pos, barrelBE);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FermentationBarrelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) return null;
        return (lvl, pos, st, be) -> {
            if (be instanceof FermentationBarrelBlockEntity barrelBE) {
                barrelBE.tick(lvl, pos, st);
            }
        };
    }
}
