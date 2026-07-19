package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;

@Render(RenderLayer.CUTOUT)
@ItemDescription(description = "toggle_4")
public class Chains extends ChainBlock {

    public Chains(Properties properties) {
        super(properties);
    }

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, AXIS, WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild) {
            level.setBlock(blockPos, state.cycle(TOGGLE), 4);
            return InteractionResult.SUCCESS;
        }
        if (player.getMainHandItem().is(CYCLING_TOOLS)) {
            level.setBlock(blockPos, state.cycle(TOGGLE), 4);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }
}