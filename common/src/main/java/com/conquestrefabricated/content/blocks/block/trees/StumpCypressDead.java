package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;


@Assets(
        state = @State(name = "%s_stump", template = "parent_stump"),
        item = @Model(name = "item/%s_stump", parent = "block/%s_stump_4", template = "item/dragon_egg"),
        block = {
                @Model(name = "block/%s_stump_2", template = "block/parent_pillar_2"),
                @Model(name = "block/%s_stump_4", template = "block/parent_pillar_4"),
                @Model(name = "block/%s_stump_6", template = "block/parent_pillar_6"),
                @Model(name = "block/%s_stump_down_2", template = "block/parent_stump_down_2"),
                @Model(name = "block/%s_stump_down_4", template = "block/parent_stump_down_4"),
                @Model(name = "block/%s_stump_down_6", template = "block/parent_stump_down_6"),
        }
)
public class StumpCypressDead extends StumpCypress {

    public static final EnumProperty<Direction> DIRECTION = BlockStateProperties.HORIZONTAL_FACING;

    public static final IntegerProperty LAYERS = IntegerProperty.create("layer", 1, 4);
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    protected static final VoxelShape[] SHAPE_CYPRESS = new VoxelShape[]{Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D), Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D), Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D)};

    private Block fullBlock;

    public StumpCypressDead(Props properties) {
        super(properties);
        this.fullBlock = properties.getParent().getBlock();
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockState down = level.getBlockState(currentPos.below());
        if (down.getBlock() instanceof Layer || (down.getBlock() instanceof Slab && down.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return stateIn.setValue(DOWN, true);
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
        }

    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(DIRECTION, rot.rotate(state.getValue(DIRECTION)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(DIRECTION)));
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return SHAPE_CYPRESS[state.getValue(LAYERS) - 1];
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        // This allows people to place pillars/columns on top of one another
        int i = state.getValue(LAYERS);
        Direction facing = context.getClickedFace();
        if (context.getItemInHand().getItem() == this.asItem() && i <= 5) {
            if (PlacementHelper.replacingClickedOnBlock(context)) {
                return facing != Direction.UP && facing != Direction.DOWN;
            } else {
                return true;
            }
        }
        else {
            return false;
        }
    }



    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        Direction facing = context.getHorizontalDirection().getOpposite();

        BlockPos blockposDown = blockpos.below();
        BlockState blockstateDown = iblockreader.getBlockState(blockposDown);

        boolean isDown = false;

        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());

        if (blockstateDown.getBlock() instanceof Layer || (blockstateDown.getBlock() instanceof Slab && blockstateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            isDown = true;
        }


        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 4) {
                return fullBlock.defaultBlockState().setValue(DIRECTION, facing).setValue(Leaves.PERSISTENT, true);
            }
            return blockstate.setValue(LAYERS, Math.min(4, i + 1));
        } else {
            return super.getStateForPlacement(context).setValue(DIRECTION, facing).setValue(DOWN, isDown);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, DOWN, DIRECTION);
    }

}
