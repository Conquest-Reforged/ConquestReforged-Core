package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.WaterloggedShape;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;


@Assets(
        state = @State(name = "%s_pillar", template = "parent_pillar"),
        item = @Model(name = "item/%s_pillar", parent = "block/%s_pillar_4", template = "item/dragon_egg"),
        block = {
                @Model(name = "block/%s_pillar_2", template = "block/parent_pillar_2"),
                @Model(name = "block/%s_pillar_4", template = "block/parent_pillar_4"),
                @Model(name = "block/%s_pillar_6", template = "block/parent_pillar_6"),
        }
)
public class LogPillar extends WaterloggedShape {

    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layer", 1, 5);
    protected static final VoxelShape[] SHAPE = new VoxelShape[]{Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D), Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D),Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D),Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};

    private Block fullBlock;

    public LogPillar(Props properties) {
        super(properties.toSettings());
        registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false).setValue(DOWN, false));
        this.fullBlock = properties.getParent().getBlock();
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return SHAPE[state.getValue(LAYERS) - 1];
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
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockState down = level.getBlockState(currentPos.below());
        if (down.getBlock() instanceof Layer || (down.getBlock() instanceof Slab && down.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            return stateIn.setValue(DOWN, true);
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
        }

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();

        BlockPos blockposDown = blockpos.below();
        BlockState blockstateDown = iblockreader.getBlockState(blockposDown);

        boolean isDown = false;

        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());

        if (blockstateDown.getBlock() instanceof Layer || (blockstateDown.getBlock() instanceof Slab && blockstateDown.getValue(TYPE_UPDOWN) == Half.BOTTOM)) {
            isDown = true;
        }

        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 5) {
                return blockstate.setValue(LAYERS, 1);
            }
            if (i == 1) {
                return blockstate.setValue(LAYERS, 4);
            }
            if (i == 2) {
                return blockstate.setValue(LAYERS, 3);
            }
            if (i == 4) {
                return blockstate.setValue(LAYERS, 2);
            }
            if (i == 3) {
                return fullBlock.defaultBlockState();
            }
            return blockstate.setValue(LAYERS, 3);
        } else {
            return super.getStateForPlacement(context).setValue(DOWN, isDown).setValue(LAYERS, 5);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, DOWN);
    }

}
