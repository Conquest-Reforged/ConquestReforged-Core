package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.effects.Effects;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.api.tags.ModTags.PLANT_SLOWNESS;

@Render(RenderLayer.CUTOUT_MIPPED)
public class LeavesFruit extends CropBlock {

    private static final VoxelShape EMPTY_SHAPE = Shapes.empty();
    public static final IntegerProperty DISTANCE = BlockStateProperties.DISTANCE;
    public static final BooleanProperty PERSISTENT = BlockStateProperties.PERSISTENT;
    private final ItemLike fruit;

    public LeavesFruit(Props props) {
        super(props.toSettings());
        this.fruit = props.get("fruit", ItemLike.class);
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(AGE, 0));
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity && state.is(PLANT_SLOWNESS) && ConquestConfig.INSTANCE.plantSlowness.get()) {
            Holder<MobEffect> slowness = level.registryAccess()
                    .lookupOrThrow(Registries.MOB_EFFECT)
                    .wrapAsHolder(Effects.CUSTOM_SLOWNESS);

            if (livingEntity instanceof Player) {
                livingEntity.addEffect(new MobEffectInstance(slowness, 15, 12, false, false));
            } else {
                livingEntity.addEffect(new MobEffectInstance(slowness, 15, 6, false, false));
            }
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getRawBrightness(pos, 0) >= 9) {
            int i = this.getAge(state);
            if (i < this.getMaxAge()) {
                if (random.nextInt((int)(25.0F / 3) + 1) == 0) {
                    world.setBlock(pos, state.setValue(AGE, i + 1), 2);
                }
            }
        }

    }

    //Override to prevent use of #withAge (uses #getDefaultState, resetting Layers state)
    @Override
    public void growCrops(Level worldIn, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(worldIn);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        worldIn.setBlock(pos, state.setValue(AGE, i), 2);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return EMPTY_SHAPE;
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        int i = getDistance(neighbourState) + 1;
        if (i != 1 || stateIn.getValue(DISTANCE) != i) {
            ticks.scheduleTick(currentPos, this, 1);
        }

        return stateIn;
    }

    private static BlockState updateDistance(BlockState state, LevelAccessor worldIn, BlockPos pos) {
        int i = 7;
        BlockPos.MutableBlockPos blockPosMutable = new BlockPos.MutableBlockPos();
        for(Direction direction : Direction.values()) {
            blockPosMutable.setWithOffset(pos, direction);
            i = Math.min(i, getDistance(worldIn.getBlockState(blockPosMutable)) + 1);
            if (i == 1) {
                break;
            }
        }
        return state.setValue(DISTANCE, Integer.valueOf(i));
    }

    private static int getDistance(BlockState neighbor) {
        if (neighbor.is(BlockTags.LOGS)) {
            return 0;
        } else {
            return (neighbor.getBlock() instanceof LeavesBlock || neighbor.getBlock() instanceof LeavesFruit) ? neighbor.getValue(DISTANCE) : 7;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateList) {
        stateList.add(DISTANCE, PERSISTENT, AGE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return updateDistance(this.defaultBlockState().setValue(PERSISTENT, true), context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public boolean mayPlaceOn(BlockState p_200014_1_, BlockGetter p_200014_2_, BlockPos p_200014_3_) {
        return true;
    }


    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(this, 1);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (this.isMaxAge(state)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            } else {
                state = state.setValue(AGE, 0);
                level.setBlock(blockPos, state, 3);
                this.dropFruit(level, blockPos, state);
                return InteractionResult.SUCCESS;

            }
        }
        return InteractionResult.FAIL;
    }

    private void dropFruit(Level world, BlockPos pos, BlockState state) {
        if (!world.isClientSide()) {
            float f = 0.7F;
            double d0 = (double) (world.getRandom().nextFloat() * 0.7F) + 0.15000000596046448D;
            double d1 = (double) (world.getRandom().nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double d2 = (double) (world.getRandom().nextFloat() * 0.7F) + 0.15000000596046448D;
            ItemStack itemstack1 = new ItemStack(fruit, 1);
            ItemEntity entityitem = new ItemEntity(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, itemstack1);
            entityitem.setDefaultPickUpDelay();
            world.addFreshEntity(entityitem);
        }
    }


    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }
}
