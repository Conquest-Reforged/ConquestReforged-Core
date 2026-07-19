package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.WaterloggedShape;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Assets(
        state = @State(name = "%s_bit", template = "parent_bit"),
        item = @Model(name = "item/%s_bit", parent = "block/%s_bit", template = "item/dragon_egg"),
        block = {
                @Model(name = "block/%s_bit_1", template = "block/parent_bit_1"),
                @Model(name = "block/%s_bit_2", template = "block/parent_bit_2")
        }
)

public class Bit extends WaterloggedShape {

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 2);
    private static final VoxelShape TOP_SHAPE = Block.box(4, 8, 4, 12, 16, 12);
    private static final VoxelShape BOTTOM_SHAPE = Block.box(4, 0, 4, 12, 8, 12);

    public Bit(Props properties) {
        super(properties.toSettings());
        registerDefaultState((this.stateDefinition.any()).setValue(WATERLOGGED, false));
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, TYPE_UPDOWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();

        Half topBottom = Half.TOP;
        if (facing != Direction.DOWN && (facing == Direction.UP || !(context.getClickLocation().y - (double)pos.getY() > 0.5D))) {
            topBottom = Half.BOTTOM;
        }
        return super.getStateForPlacement(context).setValue(TOGGLE, 1).setValue(TYPE_UPDOWN, topBottom);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(TYPE_UPDOWN) == Half.TOP) {
            return TOP_SHAPE;
        } else {
            return BOTTOM_SHAPE;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
    }
}