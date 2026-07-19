package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.util.CauldronBehavior;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class Cauldron extends Block {
    private final CauldronBehavior behavior;
    protected final CauldronInteraction.Dispatcher interactions = CauldronInteractions.EMPTY;

    public Cauldron(Props props) {
        super(props
                .customOffsetType(CustomOffsetType.LAYER_XYZ)
                .offset(BlockBehaviour.OffsetType.NONE)
                .dynamicBounds(true)
                .toSettings()
        );

        List<VoxelShape> hitBox = props.get("hitBox", List.class);
        this.behavior = new CauldronBehavior(hitBox);
        this.registerDefaultState(this.stateDefinition.any().setValue(CauldronBehavior.OFFSET_TOGGLE, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(CauldronBehavior.OFFSET_TOGGLE)) {
            return behavior.getCollisionShape(state, worldIn, pos, context);
        } else {
            return super.getCollisionShape(state, worldIn, pos, context);
        }
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace().getOpposite();
        BlockGetter blockreader = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = blockreader.getBlockState(pos.relative(facing));
        Block block = state.getBlock();
        boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

        return super.getStateForPlacement(context).setValue(CauldronBehavior.OFFSET_TOGGLE, isSlab);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return behavior.getOutlineShape(state, world, pos, context);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        behavior.precipitationTick(state, world, pos, precipitation);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return behavior.hasComparatorOutput(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CauldronBehavior.LEVEL, CauldronBehavior.OFFSET_TOGGLE);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType navigationType) {
        return false;
    }

    @Render(RenderLayer.CUTOUT)
    @SpecialOffset(offsetType = SpecialOffsetType.XYZ)
    public static class CauldronDirectional extends HorizontalDirectional {
        private final CauldronBehavior behavior;
        protected final CauldronInteraction.Dispatcher interactions = CauldronInteractions.EMPTY;

        public CauldronDirectional(Props props) {
            super(props
                    .customOffsetType(CustomOffsetType.LAYER_XYZ)
                    .offset(BlockBehaviour.OffsetType.NONE)
                    .dynamicBounds(true)
            );

            List<VoxelShape> hitBox = props.get("hitBox", List.class);
            this.behavior = new CauldronBehavior(hitBox);
            this.registerDefaultState(this.stateDefinition.any().setValue(CauldronBehavior.OFFSET_TOGGLE, false));
        }

        @Override
        public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
            if (state.getValue(CauldronBehavior.OFFSET_TOGGLE)) {
                return behavior.getCollisionShape(state, worldIn, pos, context);
            } else {
                return super.getCollisionShape(state, worldIn, pos, context);
            }
        }

        @Override
        @NotNull
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getClickedFace().getOpposite();
            BlockGetter blockreader = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState state = blockreader.getBlockState(pos.relative(facing));
            Block block = state.getBlock();
            boolean isSlab = behavior.calculateSlabOffset(facing, block, state, context);

            return super.getStateForPlacement(context).setValue(CauldronBehavior.OFFSET_TOGGLE, isSlab);
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
            return behavior.getOutlineShape(state, world, pos, context);
        }

        @Override
        protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            net.minecraft.core.cauldron.CauldronInteraction cauldronBehavior = this.interactions.get(stack);
            return cauldronBehavior.interact(state, world, pos, player, hand, stack);
        }

        @Override
        public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
            behavior.precipitationTick(state, world, pos, precipitation);
        }

        @Override
        public boolean hasAnalogOutputSignal(BlockState state) {
            return behavior.hasComparatorOutput(state);
        }

        @Override
        protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(CauldronBehavior.LEVEL, CauldronBehavior.OFFSET_TOGGLE);
        }

        @Override
        protected boolean isPathfindable(BlockState state, PathComputationType navigationType) {
            return false;
        }
    }
}
