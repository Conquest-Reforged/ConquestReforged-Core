package com.conquestrefabricated.content.blocks.block.overlay_wall;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Render(RenderLayer.CUTOUT)
public class OverlayVerticalCornerThin extends WaterloggedHorizontalDirectionalShape {

    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;

    private static final VoxelShape EAST = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 16.0D);
    private static final VoxelShape QTR_EAST = Block.box(2.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_SHAPE = Shapes.or(EAST, QTR_EAST);

    private static final VoxelShape WEST = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape QTR_WEST = Block.box(0.0D, 0.0D, 0.0D, 14.0D, 16.0D, 2.0D);
    private static final VoxelShape WEST_SHAPE = Shapes.or(WEST, QTR_WEST);

    private static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape QTR_NORTH = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 14.0D);
    private static final VoxelShape NORTH_SHAPE = Shapes.or(NORTH, QTR_NORTH);

    private static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
    private static final VoxelShape QTR_SOUTH = Block.box(0.0D, 0.0D, 2.0D, 2.0D, 16.0D, 16.0D);
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(SOUTH, QTR_SOUTH);


    public OverlayVerticalCornerThin(Properties properties) {
        super(((BlockSettingsAccessor) properties)
                .setCustomOffsetter(CustomOffsetType.LAYERS_STATIC)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 8).setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        switch (state.getValue(DIRECTION)) {
            case NORTH:
            default:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case WEST:
                return WEST_SHAPE;
            case EAST:
                return EAST_SHAPE;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockPos down = currentPos.below();
        BlockState blockStateDown = level.getBlockState(down);

        if (blockStateDown.hasProperty(Layer.LAYERS) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM)) {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random).setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random).setValue(LAYERS, 8);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        super.addProperties(builder);
        builder.add(LAYERS);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockPos down = blockpos.below();
        BlockState blockStateDown = iblockreader.getBlockState(down);

        if (blockStateDown.hasProperty(Layer.LAYERS) || (blockStateDown.hasProperty(Slab.LAYERS) && blockStateDown.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM)) {
            return super.getStateForPlacement(context).setValue(LAYERS, blockStateDown.getValue(LAYERS));
        } else {
            return super.getStateForPlacement(context).setValue(LAYERS, 8);
        }
    }

    //================================================================
    public static class Half extends OverlayVerticalCornerThin {

        private static final VoxelShape EAST = Block.box(0.0D, 0.0D, 0.0D, 2.0D, 8.0D, 16.0D);
        private static final VoxelShape QTR_EAST = Block.box(2.0D, 0.0D, 14.0D, 16.0D, 8.0D, 16.0D);
        private static final VoxelShape EAST_SHAPE = Shapes.or(EAST, QTR_EAST);

        private static final VoxelShape WEST = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
        private static final VoxelShape QTR_WEST = Block.box(0.0D, 0.0D, 0.0D, 14.0D, 8.0D, 2.0D);
        private static final VoxelShape WEST_SHAPE = Shapes.or(WEST, QTR_WEST);

        private static final VoxelShape NORTH = Block.box(0.0D, 0.0D, 14.0D, 16.0D, 8.0D, 16.0D);
        private static final VoxelShape QTR_NORTH = Block.box(14.0D, 0.0D, 0.0D, 16.0D, 8.0D, 14.0D);
        private static final VoxelShape NORTH_SHAPE = Shapes.or(NORTH, QTR_NORTH);

        private static final VoxelShape SOUTH = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 2.0D);
        private static final VoxelShape QTR_SOUTH = Block.box(0.0D, 0.0D, 2.0D, 2.0D, 8.0D, 16.0D);
        private static final VoxelShape SOUTH_SHAPE = Shapes.or(SOUTH, QTR_SOUTH);

        public Half(Properties properties)  {
            super(((BlockSettingsAccessor) properties)
                    .setCustomOffsetter(CustomOffsetType.LAYERS_STATIC)
                    .offsetType(BlockBehaviour.OffsetType.NONE)
                    .dynamicShape()
            );
            this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 8).setValue(WATERLOGGED, false));
        }

        @Override
        public VoxelShape getShape(BlockState state) {
            switch (state.getValue(DIRECTION)) {
                case NORTH:
                default:
                    return NORTH_SHAPE;
                case SOUTH:
                    return SOUTH_SHAPE;
                case WEST:
                    return WEST_SHAPE;
                case EAST:
                    return EAST_SHAPE;
            }
        }
    }
}