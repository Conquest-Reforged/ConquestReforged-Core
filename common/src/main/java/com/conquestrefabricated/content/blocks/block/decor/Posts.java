package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.Interactions;
import com.conquestrefabricated.content.blocks.block.windows.WindowSmallHalf;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.base.HorizontalDirectionalShape;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@Assets(
        state = @State(name = "%s_posts", template = "parent_posts"),
        item = @Model(name = "item/%s_posts", parent = "block/%s_posts_single", template = "item/parent_posts"),
        block = {
                @Model(name = "block/%s_posts_left", template = "block/parent_posts_left"),
                @Model(name = "block/%s_posts_right", template = "block/parent_posts_right"),
                @Model(name = "block/%s_posts_single", template = "block/parent_posts_single")
        }
)

@SpecialOffset(offsetType = SpecialOffsetType.XZ)
public class Posts extends HorizontalDirectionalShape {

    public static final IntegerProperty TOGGLE = IntegerProperty.create("toggle", 1, 3);
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    public Posts(Properties properties) {
        super(((BlockSettingsAccessor) properties)
                .setCustomOffsetter(CustomOffsetType.LAYER_XZ)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
                .noCollision()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOGGLE, OFFSET_TOGGLE);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = PlacementHelper.isFacingSlab(context);
        return super.getStateForPlacement(context).setValue(OFFSET_TOGGLE, isSlab).setValue(TOGGLE, 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult) {
        return Interactions.onUseToggleItem(player, level, blockPos, state, TOGGLE);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        switch (state.getValue(DIRECTION)) {
            case NORTH:
            default:
                return WindowSmallHalf.NORTH_SHAPE;
            case SOUTH:
                return WindowSmallHalf.SOUTH_SHAPE;
            case WEST:
                return WindowSmallHalf.WEST_SHAPE;
            case EAST:
                return WindowSmallHalf.EAST_SHAPE;
        }
    }
}