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

@ItemDescription(description = "toggle_4")
public class Flowerbed extends Bush {

    public static final IntegerProperty FLOWER_AMOUNT = IntegerProperty.create("flower_amount", 1, 4);

    public Flowerbed(Props props) {
        super(props);
        registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LAYERS, FLOWER_AMOUNT, OFFSET_TOGGLE);
    }
    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FLOWER_AMOUNT, 1);
    }

    public OffsetType getOffsetType() {
        return OffsetType.NONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, FLOWER_AMOUNT);
    }
}
