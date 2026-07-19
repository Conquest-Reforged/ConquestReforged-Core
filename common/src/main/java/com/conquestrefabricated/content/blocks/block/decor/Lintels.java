package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.SlabQuarterNoLayers;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.HorizontalDirectionalShape;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Assets(
        state = @State(name = "%s_lintel", template = "parent_lintel"),
        item = @Model(name = "item/%s_lintel", parent = "block/%s_lintel_single_top", template = "item/parent_lintel"),
        block = {
                @Model(name = "block/%s_lintel_left_bottom", template = "block/parent_lintel_left_bottom"),
                @Model(name = "block/%s_lintel_right_bottom", template = "block/parent_lintel_right_bottom"),
                @Model(name = "block/%s_lintel_middle_bottom", template = "block/parent_lintel_middle_bottom"),
                @Model(name = "block/%s_lintel_single_bottom", template = "block/parent_lintel_single_bottom"),
                @Model(name = "block/%s_lintel_left_top", template = "block/parent_lintel_left_top"),
                @Model(name = "block/%s_lintel_right_top", template = "block/parent_lintel_right_top"),
                @Model(name = "block/%s_lintel_middle_top", template = "block/parent_lintel_middle_top"),
                @Model(name = "block/%s_lintel_single_top", template = "block/parent_lintel_single_top")
        }
)

@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class Lintels extends HorizontalDirectionalShape {

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 4);
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    public Lintels(Properties properties) {
        super(((BlockSettingsAccessor) properties)
                .setCustomOffsetter(CustomOffsetType.LAYER_XYZ)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
                .noCollision()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE).add(TYPE_UPDOWN).add(OFFSET_TOGGLE);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();

        Half topBottom = Half.TOP;
        if (facing != Direction.DOWN && (facing == Direction.UP || !(context.getClickLocation().y - (double)pos.getY() > 0.5D))) {
            topBottom = Half.BOTTOM;
        }
        boolean isSlab = PlacementHelper.isFacingSlab(context);
        return super.getStateForPlacement(context).setValue(OFFSET_TOGGLE, isSlab).setValue(TOGGLE, 1).setValue(TYPE_UPDOWN, topBottom);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(TYPE_UPDOWN) == Half.BOTTOM) {
            switch (state.getValue(DIRECTION)) {
                case NORTH:
                default:
                    return SlabQuarterNoLayers.BOTTOM_NORTH_SHAPE;
                case SOUTH:
                    return SlabQuarterNoLayers.BOTTOM_SOUTH_SHAPE;
                case WEST:
                    return SlabQuarterNoLayers.BOTTOM_WEST_SHAPE;
                case EAST:
                    return SlabQuarterNoLayers.BOTTOM_EAST_SHAPE;
            }
        } else {
            switch (state.getValue(DIRECTION)) {
                case NORTH:
                default:
                    return SlabQuarterNoLayers.TOP_NORTH_SHAPE;
                case SOUTH:
                    return SlabQuarterNoLayers.TOP_SOUTH_SHAPE;
                case WEST:
                    return SlabQuarterNoLayers.TOP_WEST_SHAPE;
                case EAST:
                    return SlabQuarterNoLayers.TOP_EAST_SHAPE;
            }
        }
    }
}