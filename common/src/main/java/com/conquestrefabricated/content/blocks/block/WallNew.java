package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.block.decor.FenceLayered;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.properties.ModdedWallShape;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

@Assets(
        state = @State(name = "%s_wall", template = "parent_wall"),
        item = @Model(name = "item/%s_wall", parent = "block/%s_wall_inventory", template = "item/cobblestone_wall"),
        block = {
                @Model(name = "block/%s_wall_post", template = "block/parent_wall_post"),
                @Model(name = "block/%s_wall_side", template = "block/parent_wall_side"),
                @Model(name = "block/%s_wall_side_tall", template = "block/parent_wall_side_tall"),
                @Model(name = "block/%s_wall_inventory", template = "block/parent_wall_inventory"),
        }
)
public class WallNew extends Block {

    public static final VoxelShape TALL_POST_SHAPE = Block.box(7.0F, 0.0F, 7.0F, 9.0F, 16.0F, 9.0F);
    public static final VoxelShape TALL_NORTH_SHAPE = Block.box(7.0F, 0.0F, 0.0F, 9.0F, 16.0F, 9.0F);
    public static final VoxelShape TALL_SOUTH_SHAPE = Block.box(7.0F, 0.0F, 7.0F, 9.0F, 16.0F, 16.0F);
    public static final VoxelShape TALL_WEST_SHAPE = Block.box(0.0F, 0.0F, 7.0F, 9.0F, 16.0F, 9.0F);
    public static final VoxelShape TALL_EAST_SHAPE = Block.box(7.0F, 0.0F, 7.0F, 16.0F, 16.0F, 9.0F);

    public static final BooleanProperty UP =  BlockStateProperties.UP;;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final Map<BlockState, VoxelShape> shapeMap;
    private final Map<BlockState, VoxelShape> collisionShapeMap;

    public static final EnumProperty<ModdedWallShape> EAST_SHAPE_CUSTOM = EnumProperty.create("east", ModdedWallShape.class);
    public static final EnumProperty<ModdedWallShape> NORTH_SHAPE_CUSTOM = EnumProperty.create("north", ModdedWallShape.class);
    public static final EnumProperty<ModdedWallShape> SOUTH_SHAPE_CUSTOM = EnumProperty.create("south", ModdedWallShape.class);
    public static final EnumProperty<ModdedWallShape> WEST_SHAPE_CUSTOM = EnumProperty.create("west", ModdedWallShape.class);

    public WallNew(Properties properties) {
        super(properties);
        this.registerDefaultState(((((((this.stateDefinition.any()).setValue(UP, true)).setValue(NORTH_SHAPE_CUSTOM, ModdedWallShape.NONE)).setValue(EAST_SHAPE_CUSTOM, ModdedWallShape.NONE)).setValue(SOUTH_SHAPE_CUSTOM, ModdedWallShape.NONE)).setValue(WEST_SHAPE_CUSTOM, ModdedWallShape.NONE)).setValue(WATERLOGGED, false));
        this.shapeMap = this.getShapeMap(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.collisionShapeMap = this.getShapeMap(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    private static VoxelShape getVoxelShape(VoxelShape base, ModdedWallShape wallShape, VoxelShape tall, VoxelShape low) {
        if (wallShape == ModdedWallShape.TALL) {
            return Shapes.or(base, low);
        } else {
            return wallShape == ModdedWallShape.LOW ? Shapes.or(base, tall) : base;
        }
    }

    private Map<BlockState, VoxelShape> getShapeMap(float f, float g, float h, float i, float j, float k) {
        float l = 8.0F - f;
        float m = 8.0F + f;
        float n = 8.0F - g;
        float o = 8.0F + g;
        VoxelShape voxelShape = Block.box(l, 0.0F, l, m, h, m);
        VoxelShape voxelShape2 = Block.box(n, i, 0.0F, o, j, o);
        VoxelShape voxelShape3 = Block.box(n, i, n, o, j, 16.0F);
        VoxelShape voxelShape4 = Block.box(0.0F, i, n, o, j, o);
        VoxelShape voxelShape5 = Block.box(n, i, n, 16.0F, j, o);
        VoxelShape voxelShape6 = Block.box(n, i, 0.0F, o, k, o);
        VoxelShape voxelShape7 = Block.box(n, i, n, o, k, 16.0F);
        VoxelShape voxelShape8 = Block.box(0.0F, i, n, o, k, o);
        VoxelShape voxelShape9 = Block.box(n, i, n, 16.0F, k, o);
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        for(Boolean boolean_ : UP.getPossibleValues()) {
            for(ModdedWallShape wallShape : EAST_SHAPE_CUSTOM.getPossibleValues()) {
                for(ModdedWallShape wallShape2 : NORTH_SHAPE_CUSTOM.getPossibleValues()) {
                    for(ModdedWallShape wallShape3 : WEST_SHAPE_CUSTOM.getPossibleValues()) {
                        for(ModdedWallShape wallShape4 : SOUTH_SHAPE_CUSTOM.getPossibleValues()) {
                            VoxelShape voxelShape10 = Shapes.empty();
                            voxelShape10 = getVoxelShape(voxelShape10, wallShape, voxelShape5, voxelShape9);
                            voxelShape10 = getVoxelShape(voxelShape10, wallShape3, voxelShape4, voxelShape8);
                            voxelShape10 = getVoxelShape(voxelShape10, wallShape2, voxelShape2, voxelShape6);
                            voxelShape10 = getVoxelShape(voxelShape10, wallShape4, voxelShape3, voxelShape7);
                            if (boolean_) {
                                voxelShape10 = Shapes.or(voxelShape10, voxelShape);
                            }

                            BlockState blockState = ( (this.defaultBlockState().setValue(UP, boolean_)).setValue(EAST_SHAPE_CUSTOM, wallShape).setValue(WEST_SHAPE_CUSTOM, wallShape3)).setValue(NORTH_SHAPE_CUSTOM, wallShape2).setValue(SOUTH_SHAPE_CUSTOM, wallShape4);
                            builder.put(blockState.setValue(WATERLOGGED, false), voxelShape10);
                            builder.put(blockState.setValue(WATERLOGGED, true), voxelShape10);
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.shapeMap.get(state);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.collisionShapeMap.get(state);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    private boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side) {
        Block block = state.getBlock();
        boolean bl = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, side);
        return state.is(BlockTags.WALLS) || !isExceptionForConnection(state) && faceFullSquare || block instanceof IronBarsBlock || state.getBlock() instanceof FenceLayered || state.getBlock() instanceof FenceLayered.Half || bl;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelReader worldView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();
        BlockPos blockPos6 = blockPos.above();
        BlockState blockState = worldView.getBlockState(blockPos2);
        BlockState blockState2 = worldView.getBlockState(blockPos3);
        BlockState blockState3 = worldView.getBlockState(blockPos4);
        BlockState blockState4 = worldView.getBlockState(blockPos5);
        BlockState blockState5 = worldView.getBlockState(blockPos6);
        boolean bl = this.shouldConnectTo(blockState, blockState.isFaceSturdy(worldView, blockPos2, Direction.SOUTH), Direction.SOUTH);
        boolean bl2 = this.shouldConnectTo(blockState2, blockState2.isFaceSturdy(worldView, blockPos3, Direction.WEST), Direction.WEST);
        boolean bl3 = this.shouldConnectTo(blockState3, blockState3.isFaceSturdy(worldView, blockPos4, Direction.NORTH), Direction.NORTH);
        boolean bl4 = this.shouldConnectTo(blockState4, blockState4.isFaceSturdy(worldView, blockPos5, Direction.EAST), Direction.EAST);
        BlockState blockState6 = this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        return this.getStateWith(worldView, blockState6, blockPos6, blockState5, bl, bl2, bl3, bl4);
    }

    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (directionToNeighbour == Direction.DOWN) {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
        } else {
            return directionToNeighbour == Direction.UP ? this.getStateAt(level, stateIn, neighbourPos, neighbourState) : this.getStateWithNeighbor(level, currentPos, stateIn, neighbourPos, neighbourState, directionToNeighbour);
        }
    }

    private static boolean isConnected(BlockState state, Property<ModdedWallShape> property) {
        return state.getValue(property) != ModdedWallShape.NONE;
    }

    private static boolean shouldUseTallShape(VoxelShape aboveShape, VoxelShape tallShape) {
        return !Shapes.joinIsNotEmpty(tallShape, aboveShape, BooleanOp.ONLY_FIRST);
    }

    private BlockState getStateAt(LevelReader world, BlockState state, BlockPos pos, BlockState aboveState) {
        boolean bl = isConnected(state, NORTH_SHAPE_CUSTOM);
        boolean bl2 = isConnected(state, EAST_SHAPE_CUSTOM);
        boolean bl3 = isConnected(state, SOUTH_SHAPE_CUSTOM);
        boolean bl4 = isConnected(state, WEST_SHAPE_CUSTOM);
        return this.getStateWith(world, state, pos, aboveState, bl, bl2, bl3, bl4);
    }

    private BlockState getStateWithNeighbor(LevelReader world, BlockPos pos, BlockState state, BlockPos neighborPos, BlockState neighborState, Direction direction) {
        Direction direction2 = direction.getOpposite();
        boolean bl = direction == Direction.NORTH ? this.shouldConnectTo(neighborState, neighborState.isFaceSturdy(world, neighborPos, direction2), direction2) : isConnected(state, NORTH_SHAPE_CUSTOM);
        boolean bl2 = direction == Direction.EAST ? this.shouldConnectTo(neighborState, neighborState.isFaceSturdy(world, neighborPos, direction2), direction2) : isConnected(state, EAST_SHAPE_CUSTOM);
        boolean bl3 = direction == Direction.SOUTH ? this.shouldConnectTo(neighborState, neighborState.isFaceSturdy(world, neighborPos, direction2), direction2) : isConnected(state, SOUTH_SHAPE_CUSTOM);
        boolean bl4 = direction == Direction.WEST ? this.shouldConnectTo(neighborState, neighborState.isFaceSturdy(world, neighborPos, direction2), direction2) : isConnected(state, WEST_SHAPE_CUSTOM);
        BlockPos blockPos = pos.above();
        BlockState blockState = world.getBlockState(blockPos);
        return this.getStateWith(world, state, blockPos, blockState, bl, bl2, bl3, bl4);
    }

    private BlockState getStateWith(LevelReader world, BlockState state, BlockPos pos, BlockState aboveState, boolean north, boolean east, boolean south, boolean west) {
        VoxelShape voxelShape = aboveState.getCollisionShape(world, pos).getFaceShape(Direction.DOWN);
        BlockState blockState = this.getStateWith(state, north, east, south, west, voxelShape);
        return blockState.setValue(UP, this.shouldHavePost(blockState, aboveState, voxelShape));
    }

    private boolean shouldHavePost(BlockState state, BlockState aboveState, VoxelShape aboveShape) {
        boolean bl = aboveState.getBlock() instanceof WallBlock && aboveState.getValue(UP);
        if (bl) {
            return true;
        } else {
            ModdedWallShape wallShape = state.getValue(NORTH_SHAPE_CUSTOM);
            ModdedWallShape wallShape2 = state.getValue(SOUTH_SHAPE_CUSTOM);
            ModdedWallShape wallShape3 = state.getValue(EAST_SHAPE_CUSTOM);
            ModdedWallShape wallShape4 = state.getValue(WEST_SHAPE_CUSTOM);
            boolean bl2 = wallShape2 == ModdedWallShape.NONE;
            boolean bl3 = wallShape4 == ModdedWallShape.NONE;
            boolean bl4 = wallShape3 == ModdedWallShape.NONE;
            boolean bl5 = wallShape == ModdedWallShape.NONE;
            boolean bl6 = bl5 && bl2 && bl3 && bl4 || bl5 != bl2 || bl3 != bl4;
            if (bl6) {
                return true;
            } else {
                boolean bl7 = wallShape == ModdedWallShape.TALL && wallShape2 == ModdedWallShape.TALL || wallShape3 == ModdedWallShape.TALL && wallShape4 == ModdedWallShape.TALL;
                if (bl7) {
                    return false;
                } else {
                    return aboveState.is(BlockTags.WALL_POST_OVERRIDE) || shouldUseTallShape(aboveShape, TALL_POST_SHAPE);
                }
            }
        }
    }

    private BlockState getStateWith(BlockState state, boolean north, boolean east, boolean south, boolean west, VoxelShape aboveShape) {
        return ( state.setValue(NORTH_SHAPE_CUSTOM, this.getModdedWallShape(north, aboveShape, TALL_NORTH_SHAPE)).setValue(EAST_SHAPE_CUSTOM, this.getModdedWallShape(east, aboveShape, TALL_EAST_SHAPE))).setValue(SOUTH_SHAPE_CUSTOM, this.getModdedWallShape(south, aboveShape, TALL_SOUTH_SHAPE)).setValue(WEST_SHAPE_CUSTOM, this.getModdedWallShape(west, aboveShape, TALL_WEST_SHAPE));
    }

    private ModdedWallShape getModdedWallShape(boolean connected, VoxelShape aboveShape, VoxelShape tallShape) {
        if (connected) {
            return shouldUseTallShape(aboveShape, tallShape) ? ModdedWallShape.TALL : ModdedWallShape.LOW;
        } else {
            return ModdedWallShape.NONE;
        }
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return !(Boolean)state.getValue(WATERLOGGED);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH_SHAPE_CUSTOM, EAST_SHAPE_CUSTOM, WEST_SHAPE_CUSTOM, SOUTH_SHAPE_CUSTOM, WATERLOGGED);
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180 -> {
                return ( state.setValue(NORTH_SHAPE_CUSTOM, state.getValue(SOUTH_SHAPE_CUSTOM)).setValue(EAST_SHAPE_CUSTOM, state.getValue(WEST_SHAPE_CUSTOM))).setValue(SOUTH_SHAPE_CUSTOM, state.getValue(NORTH_SHAPE_CUSTOM)).setValue(WEST_SHAPE_CUSTOM, state.getValue(EAST_SHAPE_CUSTOM));
            }
            case COUNTERCLOCKWISE_90 -> {
                return ( state.setValue(NORTH_SHAPE_CUSTOM, state.getValue(EAST_SHAPE_CUSTOM)).setValue(EAST_SHAPE_CUSTOM, state.getValue(SOUTH_SHAPE_CUSTOM))).setValue(SOUTH_SHAPE_CUSTOM, state.getValue(WEST_SHAPE_CUSTOM)).setValue(WEST_SHAPE_CUSTOM, state.getValue(NORTH_SHAPE_CUSTOM));
            }
            case CLOCKWISE_90 -> {
                return (((state.setValue(NORTH_SHAPE_CUSTOM, state.getValue(WEST_SHAPE_CUSTOM))).setValue(EAST_SHAPE_CUSTOM, state.getValue(NORTH_SHAPE_CUSTOM))).setValue(SOUTH_SHAPE_CUSTOM, state.getValue(EAST_SHAPE_CUSTOM))).setValue(WEST_SHAPE_CUSTOM, state.getValue(SOUTH_SHAPE_CUSTOM));
            }
            default -> {
                return state;
            }
        }
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT -> {
                return (state.setValue(NORTH_SHAPE_CUSTOM, state.getValue(SOUTH_SHAPE_CUSTOM))).setValue(SOUTH_SHAPE_CUSTOM, state.getValue(NORTH_SHAPE_CUSTOM));
            }
            case FRONT_BACK -> {
                return (state.setValue(EAST_SHAPE_CUSTOM, state.getValue(WEST_SHAPE_CUSTOM))).setValue(WEST_SHAPE_CUSTOM, state.getValue(EAST_SHAPE_CUSTOM));
            }
            default -> {
                return super.mirror(state, mirror);
            }
        }
    }
}
