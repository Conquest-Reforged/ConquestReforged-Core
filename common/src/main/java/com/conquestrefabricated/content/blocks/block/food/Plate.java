package com.conquestrefabricated.content.blocks.block.food;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.block.properties.PropertyVariant;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class Plate extends CakeBlock implements PropertyVariant {

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public Plate(Properties properties) {
        super(((BlockSettingsAccessor)properties).setCustomOffsetter(CustomOffsetType.LAYER_XYZ).offsetType(OffsetType.NONE).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(BITES, Integer.valueOf(6)).setValue(OFFSET_TOGGLE, false));
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
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BITES, OFFSET_TOGGLE);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!world.isClientSide()) {
            if (this.eatActionResult(world, pos, state, itemstack, player) == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }
            if (itemstack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;

    }

    private InteractionResult eatActionResult(Level world, BlockPos pos, BlockState state, ItemStack itemStack, Player playerEntity) {
        if (!playerEntity.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            playerEntity.awardStat(Stats.EAT_CAKE_SLICE);
            playerEntity.getFoodData().eat(2, 0.1F);
            int i = state.getValue(BITES);
            if (itemStack.getItem().components().has(DataComponents.FOOD) && state.getValue(BITES) != 0) {
                if (!playerEntity.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                world.setBlock(pos, state.setValue(BITES, Integer.valueOf(state.getValue(BITES) - 1)), 3);
                return InteractionResult.SUCCESS;
            } else if (i < 6) {
                world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i + 1)), 3);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }
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
}
