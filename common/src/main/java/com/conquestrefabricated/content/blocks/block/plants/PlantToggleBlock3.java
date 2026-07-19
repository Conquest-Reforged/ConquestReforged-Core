package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.content.blocks.util.Interactions;
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
import org.jetbrains.annotations.NotNull;

@ItemDescription(description = "toggle_2")
public class PlantToggleBlock3 extends Bush {

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);

    public PlantToggleBlock3(Props props) {
        super(props);
        registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LAYERS, TOGGLE, OFFSET_TOGGLE);
    }
    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(TOGGLE, 1);
    }

    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
    }
}
