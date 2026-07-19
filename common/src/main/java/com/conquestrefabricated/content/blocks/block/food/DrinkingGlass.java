package com.conquestrefabricated.content.blocks.block.food;

import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.PropertyVariant;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class DrinkingGlass extends Plate implements PropertyVariant {

    protected static final VoxelShape SHAPE = box(6.0D, 0.0D, 6.0D, 10.0D, 11.0D, 10.0D);

    public DrinkingGlass(Properties properties) {
        super(properties);
    }

    @Override
    public Property<?> getVariantProperty() {
        return BITES;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
/*
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
        PropertyVariant.fillGroup(this, items);
    }*/
}
