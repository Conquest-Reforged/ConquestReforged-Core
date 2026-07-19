package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.block.properties.ParallelConnectionShape2;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class ParallelConnecting extends WaterloggedHorizontalDirectionalShape {

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public static final EnumProperty<ParallelConnectionShape2> FORM = EnumProperty.create("shape", ParallelConnectionShape2.class);

    public ParallelConnecting(Props props) {
        super(props
                .customOffsetType(CustomOffsetType.LAYER_XYZ)
                .offset(BlockBehaviour.OffsetType.NONE)
                .dynamicBounds(true)
                .toSettings()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FORM, ParallelConnectionShape2.ONE).setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return SHAPE;
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
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (stateIn.getBlock() == this) {

            //random wall code copy
            BlockState north = level.getBlockState(currentPos.north());
            BlockState east = level.getBlockState(currentPos.east());
            BlockState south = level.getBlockState(currentPos.south());
            BlockState west = level.getBlockState(currentPos.west());

            Direction stateInDirection = stateIn.getValue(DIRECTION);

            switch (stateInDirection) {
                case NORTH:
                    if (attachesTo(east, stateInDirection)) {
                        if (attachesTo(west, stateInDirection)) {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.MIDDLE);
                        } else {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_R);
                        }
                    } else if (attachesTo(west, stateInDirection)) {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_L);
                    } else {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.ONE);
                    }
                case SOUTH:
                    if (attachesTo(west, stateInDirection)) {
                        if (attachesTo(east, stateInDirection)) {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.MIDDLE);
                        } else {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_R);
                        }
                    } else if (attachesTo(east, stateInDirection)) {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_L);
                    } else {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.ONE);
                    }
                case EAST:
                    if (attachesTo(north, stateInDirection)) {
                        if (attachesTo(south, stateInDirection)) {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.MIDDLE);
                        } else {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_L);
                        }
                    } else if (attachesTo(south, stateInDirection)) {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_R);
                    } else {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.ONE);
                    }
                case WEST:
                    if (attachesTo(south, stateInDirection)) {
                        if (attachesTo(north, stateInDirection)) {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.MIDDLE);
                        } else {
                            return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_L);
                        }
                    } else if (attachesTo(north, stateInDirection)) {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.EDGE_R);
                    } else {
                        return stateIn.setValue(FORM, ParallelConnectionShape2.ONE);
                    }
            }
        }
        return stateIn;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        boolean isSlab = PlacementHelper.isFacingSlab(context);
        FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());

        BlockGetter iblockreader = context.getLevel();
        BlockState north = iblockreader.getBlockState(pos.north());
        BlockState east = iblockreader.getBlockState(pos.east());
        BlockState south = iblockreader.getBlockState(pos.south());
        BlockState west = iblockreader.getBlockState(pos.west());

        Direction facing = context.getHorizontalDirection().getOpposite();

        BlockState stateDefault = this.defaultBlockState()
                .setValue(DIRECTION, facing)
                .setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER)
                .setValue(OFFSET_TOGGLE, isSlab);

        BlockState stateOne = stateDefault.setValue(FORM, ParallelConnectionShape2.ONE);
        BlockState stateMiddle = stateDefault.setValue(FORM, ParallelConnectionShape2.MIDDLE);
        BlockState stateRight = stateDefault.setValue(FORM, ParallelConnectionShape2.EDGE_R);
        BlockState stateLeft = stateDefault.setValue(FORM, ParallelConnectionShape2.EDGE_L);

        switch (facing) {
            default:
            case NORTH:
                if (attachesTo(east, facing)) {
                    if (attachesTo(west, facing)) {
                        return stateMiddle;
                    } else {
                        return stateRight;
                    }
                } else if (attachesTo(west, facing)) {
                    return stateLeft;
                } else {
                    return stateOne;
                }
            case SOUTH:
                if (attachesTo(west, facing)) {
                    if (attachesTo(east, facing)) {
                        return stateMiddle;
                    } else {
                        return stateRight;
                    }
                } else if (attachesTo(east, facing)) {
                    return stateLeft;
                } else {
                    return stateOne;
                }
            case EAST:
                if (attachesTo(north, facing)) {
                    if (attachesTo(south, facing)) {
                        return stateMiddle;
                    } else {
                        return stateLeft;
                    }
                } else if (attachesTo(south, facing)) {
                    return stateRight;
                } else {
                    return stateOne;
                }
            case WEST:
                if (attachesTo(south, facing)) {
                    if (attachesTo(north, facing)) {
                        return stateMiddle;
                    } else {
                        return stateLeft;
                    }
                } else if (attachesTo(north, facing)) {
                    return stateRight;
                } else {
                    return stateOne;
                }
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORM, OFFSET_TOGGLE);
    }


    protected boolean attachesTo(BlockState state, Direction facing) {
        Block block = state.getBlock();
        return block instanceof ParallelConnecting && state.getValue(DIRECTION) == facing;
    }

    //================================================================
    public static class Unique extends ParallelConnecting {

        private final List<VoxelShape> hitBox;

        public Unique(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );
            this.registerDefaultState(this.stateDefinition.any().setValue(FORM, ParallelConnectionShape2.ONE).setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false).setValue(OFFSET_TOGGLE, false));
            this.hitBox = props.get("hitBox", List.class);
        }


        @Override
        public VoxelShape getShape(BlockState state) {
            boolean hasFourShapes = hitBox.size() == 4;
            switch (state.getValue(DIRECTION)) {
                default:
                    return hitBox.get(0);
                case EAST:
                    return hitBox.get(hasFourShapes ? 1 : 0);
                case SOUTH:
                    return hitBox.get(hasFourShapes ? 2 : 0);
                case WEST:
                    return hitBox.get(hasFourShapes ? 3 : 0);
            }
        }
    }
}

