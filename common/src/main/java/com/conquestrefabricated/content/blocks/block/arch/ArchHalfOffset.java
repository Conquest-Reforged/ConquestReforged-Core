package com.conquestrefabricated.content.blocks.block.arch;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@SpecialOffset(offsetType = SpecialOffsetType.XZ)
public class ArchHalfOffset extends ArchHalf {

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    public ArchHalfOffset(Properties properties) {
        super(((BlockSettingsAccessor) properties)
                .setCustomOffsetter(CustomOffsetType.LAYER_XZ)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, FORM, TYPE_UPDOWN, OFFSET_TOGGLE);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = PlacementHelper.isFacingSlab(context);
        return super.getStateForPlacement(context).setValue(OFFSET_TOGGLE, isSlab);
    }
}