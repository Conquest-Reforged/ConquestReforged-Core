package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class Plough extends HorizontalDirectionalBlock {
    public static final MapCodec<Plough> CODEC = simpleCodec(Plough::new);

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    protected static final VoxelShape BASE_X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.5D, 16.0D, 16.0D, 9.5D);
    protected static final VoxelShape BASE_Z_AXIS_AABB = Block.box(6.5D, 0.0D, 0.0D, 9.5D, 16.0D, 16.0D);
    protected static final VoxelShape TOP_AABB = Block.box(0.0D, 14.0D, 0.0D, 14.0D, 16.0D, 16.0D);

    public Plough(Properties properties) {
        super(((BlockSettingsAccessor)properties).setCustomOffsetter(CustomOffsetType.LAYER_Y).offsetType(OffsetType.NONE).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected MapCodec<? extends Plough> codec() {
        return CODEC;
    }

    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch(direction.getAxis()) {
            case Z:
            default:
                return state.getValue(PART) == BedPart.FOOT ? BASE_Z_AXIS_AABB : TOP_AABB;
            case X:
                return state.getValue(PART) == BedPart.FOOT ? BASE_X_AXIS_AABB : TOP_AABB;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        } else {
            return super.getCollisionShape(state, worldIn, pos, context);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = PlacementHelper.isFacingSlab(context);

        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1) ? this.defaultBlockState().setValue(FACING, direction).setValue(OFFSET_TOGGLE, isSlab) : null;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == getNeighbourDirection(stateIn.getValue(PART), stateIn.getValue(FACING))) {
            return neighbourState.is(this) && neighbourState.getValue(PART) != stateIn.getValue(PART) ? super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
        }
    }

    public PushReaction getPistonBehavior(BlockState blockState) {
        return PushReaction.DESTROY;
    }

    public boolean canPathfindThrough(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType type) {
        return false;
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            BedPart bedpart = state.getValue(PART);
            if (bedpart == BedPart.FOOT) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, entity, itemStack);
        if (!level.isClientSide()) {
            BlockPos blockpos = pos.relative(state.getValue(FACING));
            level.setBlockAndUpdate(blockpos, state.setValue(PART, BedPart.HEAD));
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    public long getSeed(BlockState state, BlockPos pos) {
        BlockPos blockpos = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockpos.getX(), pos.getY(), blockpos.getZ());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OFFSET_TOGGLE);
    }
}
