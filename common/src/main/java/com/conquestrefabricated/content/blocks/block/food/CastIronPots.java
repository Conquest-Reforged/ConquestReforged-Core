package com.conquestrefabricated.content.blocks.block.food;

import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class CastIronPots extends HorizontalDirectional.OffsetXYZ.Toggle2 {

    public static final IntegerProperty FOOD = IntegerProperty.create("food", 1, 8);

    public CastIronPots(Props properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = stack.getItem();
        if (!world.isClientSide()) {
            if (state.getValue(FOOD) < 8 && item.components().has(DataComponents.FOOD)) {
                world.setBlock(pos, state.setValue(FOOD, state.getValue(FOOD) + 1), 3);
                return InteractionResult.SUCCESS;
            } else if (state.getValue(FOOD) > 0 && item == Items.BOWL) {
                world.setBlock(pos, state.setValue(FOOD, state.getValue(FOOD) - 1), 3);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;

    }


    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> container) {
        super.addProperties(container);
        container.add(FOOD);
    }
}
