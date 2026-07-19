package com.conquestrefabricated.content.blocks.block.trees;

import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;

@ItemDescription(description = "toggle_2")
public class UnderbranchesToggle2 extends HorizontalDirectional {

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 1, 2);

    public UnderbranchesToggle2(Props props) {
        super(props);
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, POSITION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(TOGGLE, 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        if (player.getAbilities().instabuild) {
			if (player.isShiftKeyDown()) {
				level.setBlock(blockPos, state.cycle(POSITION), 3);
				return InteractionResult.SUCCESS;
			}
            level.setBlock(blockPos, state.cycle(TOGGLE), 3);
            return InteractionResult.SUCCESS;
        }

        if (player.getMainHandItem().is(CYCLING_TOOLS)) {
            if (player.isShiftKeyDown()) {
				level.setBlock(blockPos, state.cycle(POSITION), 3);
				return InteractionResult.SUCCESS;
			}
			level.setBlock(blockPos, state.cycle(TOGGLE), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
