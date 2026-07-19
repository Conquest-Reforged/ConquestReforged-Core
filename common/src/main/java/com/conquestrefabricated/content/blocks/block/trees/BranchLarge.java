package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;
import java.util.function.Function;

@Assets(
        state = @State(name = "%s_large_branch", template = "parent_large_branch"),
        item = @Model(name = "item/%s_large_branch", parent = "block/%s_large_branch_post", template = "item/cobblestone_wall"),
        block = {
                @Model(name = "block/%s_large_branch_post", template = "block/parent_large_branch_post"),
                @Model(name = "block/%s_large_branch_n", template = "block/parent_large_branch_n"),
                @Model(name = "block/%s_large_branch_n_1", template = "block/parent_large_branch_n_1"),
                @Model(name = "block/%s_large_branch_ne", template = "block/parent_large_branch_ne"),
                @Model(name = "block/%s_large_branch_nse", template = "block/parent_large_branch_nse"),
                @Model(name = "block/%s_large_branch_ns", template = "block/parent_large_branch_ns"),
                @Model(name = "block/%s_large_branch_ns_up", template = "block/parent_large_branch_ns_up"),
                @Model(name = "block/%s_large_branch_nsew", template = "block/parent_large_branch_nsew"),
                @Model(name = "block/%s_large_branch_n_up", template = "block/parent_large_branch_n_up"),
                @Model(name = "block/%s_large_branch_n_up", template = "block/parent_large_branch_n_up_1"),
                @Model(name = "block/%s_large_branch_ne_up", template = "block/parent_large_branch_ne_up"),
                @Model(name = "block/%s_large_branch_nse_up", template = "block/parent_large_branch_nse_up"),
                @Model(name = "block/%s_large_branch_nsew_up", template = "block/parent_large_branch_nsew_up"),
        }
)
public class BranchLarge extends CrossCollisionBlock {

    public static final MapCodec<BranchLarge> CODEC = simpleCodec(BranchLarge::new);

    public static final BooleanProperty UP = BlockStateProperties.UP;
    private final Function<BlockState, VoxelShape> wallUpShapes;
    private final Function<BlockState, VoxelShape> wallRegularShapes;

    public BranchLarge(Properties properties) {
        super(0.0F, 3.0F, 8.0F, 16.0F, 16.0F, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(UP, true)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(WATERLOGGED, false));
        this.wallUpShapes = this.makeUpShapes(4.0F, 3.0F, 16.0F, 8.0F, 16.0F);
        this.wallRegularShapes = this.makeRegularShapes(4.0F, 3.0F, 16.0F, 8.0F, 16.0F);
    }

    @Override
    protected MapCodec<? extends CrossCollisionBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(UP) ? this.wallUpShapes.apply(state) : this.wallRegularShapes.apply(state);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(UP) ? this.wallUpShapes.apply(state) : this.wallRegularShapes.apply(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelReader iworldreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockPos blockpos1 = blockpos.north();
        BlockPos blockpos2 = blockpos.east();
        BlockPos blockpos3 = blockpos.south();
        BlockPos blockpos4 = blockpos.west();
        BlockState blockstate = iworldreader.getBlockState(blockpos1);
        BlockState blockstate1 = iworldreader.getBlockState(blockpos2);
        BlockState blockstate2 = iworldreader.getBlockState(blockpos3);
        BlockState blockstate3 = iworldreader.getBlockState(blockpos4);
        BlockState blockstateDown = context.getLevel().getBlockState(context.getClickedPos().below());

        boolean flagDown = false;
        if (blockstateDown.canOcclude()) {
            flagDown = true;
        }

        boolean flag = this.getConnection(blockstate);
        boolean flag1 = this.getConnection(blockstate1);
        boolean flag2 = this.getConnection(blockstate2);
        boolean flag3 = this.getConnection(blockstate3);
        return this.defaultBlockState().setValue(UP, flagDown).setValue(NORTH, flag).setValue(EAST, flag1).setValue(SOUTH, flag2).setValue(WEST, flag3).setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
    }

    public boolean allowsMovement(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        Direction direction = directionToNeighbour.getOpposite();

        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (directionToNeighbour == Direction.DOWN) {
            boolean flag =  ((neighbourState.isFaceSturdy(level, neighbourPos, direction) || (neighbourState.getBlock() instanceof BranchLarge) || (neighbourState.getBlock() instanceof Branch) || (neighbourState.getBlock() instanceof BranchSmall)));
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random).setValue(UP, flag);
        } else {
            boolean flag = directionToNeighbour == Direction.NORTH ? this.getConnection(neighbourState) : stateIn.getValue(NORTH);
            boolean flag1 = directionToNeighbour == Direction.EAST ? this.getConnection(neighbourState) : stateIn.getValue(EAST);
            boolean flag2 = directionToNeighbour == Direction.SOUTH ? this.getConnection(neighbourState) : stateIn.getValue(SOUTH);
            boolean flag3 = directionToNeighbour == Direction.WEST ? this.getConnection(neighbourState) : stateIn.getValue(WEST);
            return stateIn.setValue(NORTH, flag).setValue(EAST, flag1).setValue(SOUTH, flag2).setValue(WEST, flag3);
        }
    }

    private boolean getConnection(BlockState p_220113_1_) {
        Block block = p_220113_1_.getBlock();
        boolean flag = block instanceof BranchLarge || block instanceof Branch || block instanceof BranchSmall;
        return flag;
    }

    /**
     * "Up" variant always includes the central post/trunk column, unconditionally,
     * in addition to whichever horizontal arms are connected.
     */
    protected Function<BlockState, VoxelShape> makeUpShapes(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBeginning, float extensionHeight) {
        VoxelShape post = Block.column((double) nodeWidth, 0.0D, (double) nodeHeight);
        Map<Direction, VoxelShape> arms = Shapes.rotateHorizontal(
                Block.boxZ((double) extensionWidth, (double) extensionBeginning, (double) extensionHeight, 0.0D, 8.0D));

        return this.getShapeForEachState(state -> {
            VoxelShape shape = post;
            for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
                if (state.getValue(entry.getValue())) {
                    shape = Shapes.or(shape, arms.get(entry.getKey()));
                }
            }
            return shape;
        }, new Property[]{UP, WATERLOGGED});
    }

    /**
     * "Regular" variant has no central post — just whichever arms are connected.
     * Yields Shapes.empty() when nothing is connected.
     */
    protected Function<BlockState, VoxelShape> makeRegularShapes(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBeginning, float extensionHeight) {
        Map<Direction, VoxelShape> arms = Shapes.rotateHorizontal(
                Block.boxZ((double) extensionWidth, (double) extensionBeginning, (double) extensionHeight, 0.0D, 8.0D));

        return this.getShapeForEachState(state -> {
            VoxelShape shape = Shapes.empty();
            for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
                if (state.getValue(entry.getValue())) {
                    shape = Shapes.or(shape, arms.get(entry.getKey()));
                }
            }
            return shape;
        }, new Property[]{UP, WATERLOGGED});
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
    }
}
