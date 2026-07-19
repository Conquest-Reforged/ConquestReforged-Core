package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.HorizontalDirectionalShape;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Assets(
        state = @State(name = "%s_vertical", template = "parent_beam"),
        item = @Model(name = "item/%s_vertical", parent = "block/%s_beam_support", template = "item/parent_beam_vertical"),
        block = {
                @Model(name = "block/%s_beam_vertical", template = "block/parent_beam_vertical"),
                @Model(name = "block/%s_beam_support", template = "block/parent_beam_support"),
                @Model(name = "block/%s_beam_support_1", template = "block/parent_beam_support_1"),
                @Model(name = "block/%s_beam_support_2", template = "block/parent_beam_support_2"),
                @Model(name = "block/%s_beam_support_3", template = "block/parent_beam_support_3"),
                @Model(name = "block/%s_beam_support_bottom", template = "block/parent_beam_support_bottom")
        }
)
public class BeamVertical extends HorizontalDirectionalShape {

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 6);

    private static final VoxelShape SHAPE_WEST_OFF = Block.box(0.0D, 0.0D, 5.0D, 4.0D, 16.0D, 11.0D);
    private static final VoxelShape SHAPE_EAST_OFF = Block.box(12.0D, 0.0D, 5.0D, 16.0D, 16.0D, 11.0D);
    private static final VoxelShape SHAPE_NORTH_OFF = Block.box(5.0D, 0.0D, 12.0D, 11.0D, 16.0D, 16.0D);
    private static final VoxelShape SHAPE_SOUTH_OFF = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 16.0D, 4.0D);
    private static final VoxelShape SHAPE_WEST_ON = Shapes.or(SHAPE_WEST_OFF, Block.box(0.0D, 12.0D, 5.0D, 16.0D, 16.0D, 11.0D));
    private static final VoxelShape SHAPE_EAST_ON = Shapes.or(SHAPE_EAST_OFF, Block.box(0.0D, 12.0D, 5.0D, 16.0D, 16.0D, 11.0D));
    private static final VoxelShape SHAPE_SOUTH_ON = Shapes.or(SHAPE_SOUTH_OFF, Block.box(5.0D, 12.0D, 0.0D, 11.0D, 16.0D, 16.0D));
    private static final VoxelShape SHAPE_NORTH_ON = Shapes.or(SHAPE_NORTH_OFF, Block.box(5.0D, 12.0D, 0.0D, 11.0D, 16.0D, 16.0D));
    private static final VoxelShape SHAPE_WEST_ON_BOTTOM = Shapes.or(SHAPE_WEST_OFF, Block.box(0.0D, 0.0D, 5.0D, 16.0D, 4.0D, 11.0D));
    private static final VoxelShape SHAPE_EAST_ON_BOTTOM = Shapes.or(SHAPE_EAST_OFF, Block.box(0.0D, 0.0D, 5.0D, 16.0D, 4.0D, 11.0D));
    private static final VoxelShape SHAPE_SOUTH_ON_BOTTOM = Shapes.or(SHAPE_SOUTH_OFF, Block.box(5.0D, 0.0D, 0.0D, 11.0D, 4.0D, 16.0D));
    private static final VoxelShape SHAPE_NORTH_ON_BOTTOM = Shapes.or(SHAPE_NORTH_OFF, Block.box(5.0D, 0.0D, 0.0D, 11.0D, 4.0D, 16.0D));

    public BeamVertical(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(TOGGLE) == 1) {
            return switch (state.getValue(DIRECTION)) {
                case SOUTH -> SHAPE_SOUTH_OFF;
                case WEST -> SHAPE_EAST_OFF;
                case EAST -> SHAPE_WEST_OFF;
                default -> SHAPE_NORTH_OFF;
            };
        } else if (state.getValue(TOGGLE) < 5) {
            return switch (state.getValue(DIRECTION)) {
                case SOUTH -> SHAPE_SOUTH_ON;
                case WEST -> SHAPE_EAST_ON;
                case EAST -> SHAPE_WEST_ON;
                default -> SHAPE_NORTH_ON;
            };
        } else {
            return switch (state.getValue(DIRECTION)) {
                case SOUTH -> SHAPE_SOUTH_ON_BOTTOM;
                case WEST -> SHAPE_EAST_ON_BOTTOM;
                case EAST -> SHAPE_WEST_ON_BOTTOM;
                default -> SHAPE_NORTH_ON_BOTTOM;
            };
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(TOGGLE,1);
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
    }
}
