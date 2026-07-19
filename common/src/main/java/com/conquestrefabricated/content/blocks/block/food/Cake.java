package com.conquestrefabricated.content.blocks.block.food;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.block.properties.PropertyVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class Cake extends CakeBlock implements PropertyVariant {

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    public Cake(Properties properties) {
        super(((BlockSettingsAccessor)properties).setCustomOffsetter(CustomOffsetType.LAYER_XYZ).offsetType(OffsetType.NONE).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return true;
    }

    @Override
    public Property<?> getVariantProperty() {
        return CakeBlock.BITES;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BITES, OFFSET_TOGGLE);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = PlacementHelper.isFacingSlab(context);

        return super.getStateForPlacement(context).setValue(OFFSET_TOGGLE, isSlab);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        } else {
            return super.getCollisionShape(state, worldIn, pos, context);
        }
    }

    /*
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
        PropertyVariant.fillGroup(this, items);
    }*/

    // Directional
    public static class Directional extends Cake {

        public Directional(Properties properties) {
            super(properties);
        }

        public static final EnumProperty<Direction> DIRECTION = BlockStateProperties.HORIZONTAL_FACING;

        @Override
        public BlockState rotate(BlockState state, Rotation rot) {
            return state.setValue(DIRECTION, rot.rotate(state.getValue(DIRECTION)));
        }

        @Override
        public BlockState mirror(BlockState state, Mirror mirrorIn) {
            return state.rotate(mirrorIn.getRotation(state.getValue(DIRECTION)));
        }

        @NotNull
        @Override
        public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction facing = context.getHorizontalDirection().getOpposite();
            return super.getStateForPlacement(context).setValue(DIRECTION, facing);
        }

        @Override
        protected final void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
            builder.add(DIRECTION, BITES, OFFSET_TOGGLE);
        }
    }
}