package com.conquestrefabricated.content.blocks.block;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import com.conquestrefabricated.content.blocks.util.LayerCycling;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.WaterloggedShape;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;


@Assets(
        state = @State(name = "%s_pillar", template = "parent_pillar"),
        item = @Model(name = "item/%s_pillar", parent = "block/%s_pillar_4", template = "item/dragon_egg"),
        block = {
                @Model(name = "block/%s_pillar_2", template = "block/parent_pillar_2"),
                @Model(name = "block/%s_pillar_4", template = "block/parent_pillar_4"),
                @Model(name = "block/%s_pillar_6", template = "block/parent_pillar_6"),
        }
)
public class Pillar extends WaterloggedShape {

    public static final IntegerProperty LAYERS = IntegerProperty.create("layer", 1, 3);
    protected static final VoxelShape[] SHAPE = new VoxelShape[]{Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D), Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D)};

    private Block fullBlock;

    public Pillar(Props properties) {
        super(properties.toSettings());
        registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false));
        this.fullBlock = properties.getParent().getBlock();
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        return SHAPE[state.getValue(LAYERS) - 1];
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        // Stacking by placing is creative-only; in survival the mallet thickens a block instead.
        if (!LayerCycling.stacksOnPlace(context)) {
            return false;
        }
        int i = state.getValue(LAYERS);
        if (context.getItemInHand().getItem() == this.asItem() && i <= 4) {
            if (PlacementHelper.replacingClickedOnBlock(context)) {
                return context.getClickedFace() != Direction.UP && context.getClickedFace() != Direction.DOWN;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return LayerCycling.cycle(state, level, pos, player, LAYERS);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        if (blockstate.getBlock() == this) {
            int i = blockstate.getValue(LAYERS);
            if (i == 3) {
                return fullBlock.defaultBlockState();
            }
            return blockstate.setValue(LAYERS, Math.min(3, i + 1));
        } else {
            return super.getStateForPlacement(context);
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

}
