package com.conquestrefabricated.content.blocks.block.classical;

import com.conquestrefabricated.content.blocks.block.VerticalSlabLessLayers;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

@Assets(
        state = @State(name = "%s_vertical_slab", template = "parent_doric_capital_vertical_slab"),
        item = @Model(name = "item/%s_vertical_slab", parent = "block/%s_vertical_slab_4", template = "item/parent_vertical_slab"),
        block = {
                @Model(name = "block/%s_vertical_slab_2", template = "block/parent_doric_capital_vertical_slab_2"),
                @Model(name = "block/%s_vertical_slab_2_e", template = "block/parent_doric_capital_vertical_slab_2_e"),
                @Model(name = "block/%s_vertical_slab_2_es", template = "block/parent_doric_capital_vertical_slab_2_es"),
                @Model(name = "block/%s_vertical_slab_2_ew", template = "block/parent_doric_capital_vertical_slab_2_ew"),
                @Model(name = "block/%s_vertical_slab_2_esw", template = "block/parent_doric_capital_vertical_slab_2_esw"),
                @Model(name = "block/%s_vertical_slab_2_s", template = "block/parent_doric_capital_vertical_slab_2_s"),
                @Model(name = "block/%s_vertical_slab_2_sw", template = "block/parent_doric_capital_vertical_slab_2_sw"),
                @Model(name = "block/%s_vertical_slab_2_w", template = "block/parent_doric_capital_vertical_slab_2_w"),
                @Model(name = "block/%s_vertical_slab_4", template = "block/parent_doric_capital_vertical_slab_4"),
                @Model(name = "block/%s_vertical_slab_4_e", template = "block/parent_doric_capital_vertical_slab_4_e"),
                @Model(name = "block/%s_vertical_slab_4_es", template = "block/parent_doric_capital_vertical_slab_4_es"),
                @Model(name = "block/%s_vertical_slab_4_ew", template = "block/parent_doric_capital_vertical_slab_4_ew"),
                @Model(name = "block/%s_vertical_slab_4_esw", template = "block/parent_doric_capital_vertical_slab_4_esw"),
                @Model(name = "block/%s_vertical_slab_4_s", template = "block/parent_doric_capital_vertical_slab_4_s"),
                @Model(name = "block/%s_vertical_slab_4_sw", template = "block/parent_doric_capital_vertical_slab_4_sw"),
                @Model(name = "block/%s_vertical_slab_4_w", template = "block/parent_doric_capital_vertical_slab_4_w"),
                @Model(name = "block/%s_vertical_slab_6", template = "block/parent_doric_capital_vertical_slab_6"),
                @Model(name = "block/%s_vertical_slab_6_e", template = "block/parent_doric_capital_vertical_slab_6_e"),
                @Model(name = "block/%s_vertical_slab_6_es", template = "block/parent_doric_capital_vertical_slab_6_es"),
                @Model(name = "block/%s_vertical_slab_6_ew", template = "block/parent_doric_capital_vertical_slab_6_ew"),
                @Model(name = "block/%s_vertical_slab_6_esw", template = "block/parent_doric_capital_vertical_slab_6_esw"),
                @Model(name = "block/%s_vertical_slab_6_s", template = "block/parent_doric_capital_vertical_slab_6_s"),
                @Model(name = "block/%s_vertical_slab_6_sw", template = "block/parent_doric_capital_vertical_slab_6_sw"),
                @Model(name = "block/%s_vertical_slab_6_w", template = "block/parent_doric_capital_vertical_slab_6_w"),
        }
)
public class VerticalSlabCapital extends VerticalSlabLessLayers {

    public static final BooleanProperty EAST = BooleanProperty.create("e");
    public static final BooleanProperty SOUTH = BooleanProperty.create("s");
    public static final BooleanProperty WEST = BooleanProperty.create("w");

    public VerticalSlabCapital(Props properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(DIRECTION, Direction.NORTH).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    public boolean canConnectTo(BlockState state) {
        Block block = state.getBlock();
        boolean flag1 = block instanceof CubeCapital || block instanceof VerticalSlabCapital || block instanceof VerticalCornerCapital;
        return flag1;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos2;
        BlockPos blockpos3;
        BlockPos blockpos4;
        switch (facing) {
            default:
                blockpos2 = blockpos.east();
                blockpos3 = blockpos.south();
                blockpos4 = blockpos.west();
                break;
            case SOUTH:
                blockpos2 = blockpos.west();
                blockpos3 = blockpos.north();
                blockpos4 = blockpos.east();
                break;
            case EAST:
                blockpos2 = blockpos.south();
                blockpos3 = blockpos.west();
                blockpos4 = blockpos.north();
                break;
            case WEST:
                blockpos2 = blockpos.north();
                blockpos3 = blockpos.east();
                blockpos4 = blockpos.south();
                break;
        }
        BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos4);

        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 3) {
                return fullBlock.defaultBlockState();
            }
        }
        return super.getStateForPlacement(context).setValue(EAST, this.canConnectTo(blockstate1)).setValue(SOUTH, this.canConnectTo(blockstate2)).setValue(WEST, this.canConnectTo(blockstate3));
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        Direction stateDirection = stateIn.getValue(DIRECTION);
        if (stateDirection == Direction.NORTH) {
            if (directionToNeighbour == Direction.NORTH) {
                return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
            } else if (directionToNeighbour == Direction.SOUTH) {
                return stateIn.setValue(SOUTH, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.EAST) {
                return stateIn.setValue(EAST, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.WEST) {
                return stateIn.setValue(WEST, canConnectTo(neighbourState));
            }
        }

        if (stateDirection == Direction.EAST) {
            if (directionToNeighbour == Direction.EAST) {
                return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
            } else if (directionToNeighbour == Direction.WEST) {
                return stateIn.setValue(SOUTH, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.SOUTH) {
                return stateIn.setValue(EAST, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.NORTH) {
                return stateIn.setValue(WEST, canConnectTo(neighbourState));
            }
        }

        if (stateDirection == Direction.SOUTH) {
            if (directionToNeighbour == Direction.SOUTH) {
                return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
            } else if (directionToNeighbour == Direction.NORTH) {
                return stateIn.setValue(SOUTH, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.WEST) {
                return stateIn.setValue(EAST, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.EAST) {
                return stateIn.setValue(WEST, canConnectTo(neighbourState));
            }
        }

        if (stateDirection == Direction.WEST) {
            if (directionToNeighbour == Direction.WEST) {
                return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
            } else if (directionToNeighbour == Direction.EAST) {
                return stateIn.setValue(SOUTH, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.NORTH) {
                return stateIn.setValue(EAST, canConnectTo(neighbourState));
            } else if (directionToNeighbour == Direction.SOUTH) {
                return stateIn.setValue(WEST, canConnectTo(neighbourState));
            }
        }
        return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, EAST, WEST, SOUTH);
    }
}
