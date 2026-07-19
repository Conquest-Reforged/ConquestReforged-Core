package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.Layer;
import com.conquestrefabricated.content.blocks.block.Slab;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.util.RenderLayer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

@Assets(
        state = @State(name = "%s", template = "parent_pane_old"),
        render = @Render(RenderLayer.CUTOUT),
        item = @Model(name = "item/%s", parent = "block/%s_pane_ns", template = "item/parent_pane"),
        block = {
                @Model(name = "block/%s_pane_n", template = "block/parent_flatpane_n"),
                @Model(name = "block/%s_pane_ne", template = "block/parent_flatpane_ne"),
                @Model(name = "block/%s_pane_ns", template = "block/parent_flatpane_ns"),
                @Model(name = "block/%s_pane_nse", template = "block/parent_flatpane_nse"),
                @Model(name = "block/%s_pane_nsew", template = "block/parent_flatpane_nsew"),
                @Model(name = "block/%s_pane_post", template = "block/parent_flatpane_post")
        }
)
public class FenceLayered extends CrossCollisionBlock {
    public static final MapCodec<FenceLayered> CODEC = simpleCodec(FenceLayered::new);

    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;

    public FenceLayered(Properties properties) {
        super(1.0F, 1.0F, 16.0F, 16.0F, 22.0F, ((BlockSettingsAccessor) properties)
                .setCustomOffsetter(CustomOffsetType.LAYERS_STATIC)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
        );
        this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(LAYERS, 8).setValue(WATERLOGGED, false);
    }

    public boolean canConnectTo(BlockState state, boolean sideSolidFullSquare, Direction dir) {
        boolean bl2 = state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, dir);
        return !isExceptionForConnection(state) && sideSolidFullSquare || state.getBlock() instanceof IronBarsBlock || state.getBlock() instanceof FenceLayered || state.getBlock() instanceof Half || state.is(BlockTags.WALLS) || bl2;
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockGetter blockView = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        BlockPos down = blockPos.below();
        BlockState blockStateDown = blockView.getBlockState(down);
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.south();
        BlockPos blockPos4 = blockPos.west();
        BlockPos blockPos5 = blockPos.east();
        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);


        if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS)) {
            return this.defaultBlockState().setValue(LAYERS, blockStateDown.getValue(LAYERS))
                    .setValue(NORTH, this.canConnectTo(blockState, blockState.isFaceSturdy(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH))
                    .setValue(SOUTH, this.canConnectTo(blockState2, blockState2.isFaceSturdy(blockView, blockPos3, Direction.NORTH), Direction.NORTH))
                    .setValue(WEST, this.canConnectTo(blockState3, blockState3.isFaceSturdy(blockView, blockPos4, Direction.EAST), Direction.EAST))
                    .setValue(EAST, this.canConnectTo(blockState4, blockState4.isFaceSturdy(blockView, blockPos5, Direction.WEST), Direction.WEST))
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        } else {
            return this.defaultBlockState().setValue(LAYERS, 8)
                    .setValue(NORTH, this.canConnectTo(blockState, blockState.isFaceSturdy(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH))
                    .setValue(SOUTH, this.canConnectTo(blockState2, blockState2.isFaceSturdy(blockView, blockPos3, Direction.NORTH), Direction.NORTH))
                    .setValue(WEST, this.canConnectTo(blockState3, blockState3.isFaceSturdy(blockView, blockPos4, Direction.EAST), Direction.EAST))
                    .setValue(EAST, this.canConnectTo(blockState4, blockState4.isFaceSturdy(blockView, blockPos5, Direction.WEST), Direction.WEST))
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        BlockPos down = currentPos.below();
        BlockState blockStateDown = level.getBlockState(down);

        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS)) {
            if (directionToNeighbour.getAxis().isHorizontal()) {
                return stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), this.canConnectTo(neighbourState, neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour.getOpposite()), directionToNeighbour))
                        .setValue(LAYERS, blockStateDown.getValue(LAYERS));
            } else {
                return stateIn.setValue(LAYERS, blockStateDown.getValue(LAYERS));
            }
        } else {
            if (directionToNeighbour.getAxis().isHorizontal()) {
                return stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), this.canConnectTo(neighbourState, neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour.getOpposite()), directionToNeighbour))
                        .setValue(LAYERS, 8);
            } else {
                return stateIn.setValue(LAYERS, 8);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, LAYERS, WATERLOGGED);
    }

    @Override
    protected MapCodec<? extends CrossCollisionBlock> codec() {
        return CODEC;
    }

    //================================================================

    public static class Half extends CrossCollisionBlock {
        public static final MapCodec<Half> CODEC = simpleCodec(Half::new);

        @Render(RenderLayer.CUTOUT)
        public Half(Properties properties) {
            super(1.0F, 1.0F, 8.0F, 8.0F, 12.0F, ((BlockSettingsAccessor) properties)
                    .setCustomOffsetter(CustomOffsetType.LAYERS_STATIC)
                    .offsetType(BlockBehaviour.OffsetType.NONE)
                    .dynamicShape()
            );
            this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(LAYERS, 8).setValue(WATERLOGGED, false);
        }

        public boolean canConnectTo(BlockState state, boolean sideSolidFullSquare, Direction dir) {
            boolean bl2 = state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, dir);
            return !isExceptionForConnection(state) && sideSolidFullSquare || state.getBlock() instanceof IronBarsBlock || state.getBlock() instanceof FenceLayered || state.getBlock() instanceof Half || state.is(BlockTags.WALLS) || bl2;
        }

        public BlockState getStateForPlacement(BlockPlaceContext ctx) {
            BlockGetter blockView = ctx.getLevel();
            BlockPos blockPos = ctx.getClickedPos();
            BlockPos down = blockPos.below();
            BlockState blockStateDown = blockView.getBlockState(down);
            FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
            BlockPos blockPos2 = blockPos.north();
            BlockPos blockPos3 = blockPos.south();
            BlockPos blockPos4 = blockPos.west();
            BlockPos blockPos5 = blockPos.east();
            BlockState blockState = blockView.getBlockState(blockPos2);
            BlockState blockState2 = blockView.getBlockState(blockPos3);
            BlockState blockState3 = blockView.getBlockState(blockPos4);
            BlockState blockState4 = blockView.getBlockState(blockPos5);


            if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS)) {
                return this.defaultBlockState().setValue(LAYERS, blockStateDown.getValue(LAYERS))
                        .setValue(NORTH, this.canConnectTo(blockState, blockState.isFaceSturdy(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH))
                        .setValue(SOUTH, this.canConnectTo(blockState2, blockState2.isFaceSturdy(blockView, blockPos3, Direction.NORTH), Direction.NORTH))
                        .setValue(WEST, this.canConnectTo(blockState3, blockState3.isFaceSturdy(blockView, blockPos4, Direction.EAST), Direction.EAST))
                        .setValue(EAST, this.canConnectTo(blockState4, blockState4.isFaceSturdy(blockView, blockPos5, Direction.WEST), Direction.WEST))
                        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            } else {
                return this.defaultBlockState().setValue(LAYERS, 8)
                        .setValue(NORTH, this.canConnectTo(blockState, blockState.isFaceSturdy(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH))
                        .setValue(SOUTH, this.canConnectTo(blockState2, blockState2.isFaceSturdy(blockView, blockPos3, Direction.NORTH), Direction.NORTH))
                        .setValue(WEST, this.canConnectTo(blockState3, blockState3.isFaceSturdy(blockView, blockPos4, Direction.EAST), Direction.EAST))
                        .setValue(EAST, this.canConnectTo(blockState4, blockState4.isFaceSturdy(blockView, blockPos5, Direction.WEST), Direction.WEST))
                        .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
            }
        }

        @Override
        public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
            BlockPos down = currentPos.below();
            BlockState blockStateDown = level.getBlockState(down);

            if (stateIn.getValue(WATERLOGGED)) {
                ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }


            if (blockStateDown.hasProperty(Layer.LAYERS) || blockStateDown.hasProperty(Slab.LAYERS)) {
                if (directionToNeighbour.getAxis().isHorizontal()) {
                    return stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), this.canConnectTo(neighbourState, neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour.getOpposite()), directionToNeighbour))
                            .setValue(LAYERS, blockStateDown.getValue(LAYERS));
                } else {
                    return stateIn.setValue(LAYERS, blockStateDown.getValue(LAYERS));
                }
            } else {
                if (directionToNeighbour.getAxis().isHorizontal()) {
                    return stateIn.setValue(PROPERTY_BY_DIRECTION.get(directionToNeighbour), this.canConnectTo(neighbourState, neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour.getOpposite()), directionToNeighbour))
                            .setValue(LAYERS, 8);
                } else {
                    return stateIn.setValue(LAYERS, 8);
                }
            }
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(NORTH, EAST, WEST, SOUTH, LAYERS,WATERLOGGED);
        }

        @Override
        protected MapCodec<? extends Half> codec() {
            return CODEC;
        }
    }
}