package com.conquestrefabricated.content.blocks.block.lights;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.block.properties.Waterloggable;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.LIGHT_0_3;

@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class Torch extends Block implements Waterloggable {

    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    protected static final VoxelShape EAST_OPEN_AABB = Block.box(0.0D, 0.0D, 4.0D, 10.0D, 16.0D, 11.0D);
    protected static final VoxelShape WEST_OPEN_AABB = Block.box(6.0D, 0.0D, 4.0D, 16.0D, 16.0D, 11.0D);
    protected static final VoxelShape SOUTH_OPEN_AABB = Block.box(4.0D, 0.0D, 0.0D, 11.0D, 16.0D, 10.0D);
    protected static final VoxelShape NORTH_OPEN_AABB = Block.box(4.0D, 0.0D, 6.0D, 11.0D, 16.0D, 16.0D);
    protected static final VoxelShape BOTTOM_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    protected static final VoxelShape TOP_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public Torch(Properties properties) {
        super(((BlockSettingsAccessor)properties).setCustomOffsetter(CustomOffsetType.LAYER_XYZ_NSEWUD).offsetType(OffsetType.NONE).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIGHT_0_3, 0).setValue(Waterloggable.WATERLOGGED, false).setValue(OFFSET_TOGGLE, false));

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = PlacementHelper.isFacingSlab(context);

        return this.defaultBlockState().setValue(FACING, context.getClickedFace()).setValue(OFFSET_TOGGLE, isSlab);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        } else {
            return super.getCollisionShape(state, worldIn, pos, context);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        {
            switch (state.getValue(FACING)) {
                case NORTH:
                default:
                    return NORTH_OPEN_AABB;
                case SOUTH:
                    return SOUTH_OPEN_AABB;
                case WEST:
                    return WEST_OPEN_AABB;
                case EAST:
                    return EAST_OPEN_AABB;
                case DOWN:
                    return TOP_AABB;
                case UP:
                    return BOTTOM_AABB;
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIGHT_0_3, Waterloggable.WATERLOGGED, OFFSET_TOGGLE);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() == Items.FLINT_AND_STEEL) {
            if (!player.getAbilities().mayBuild) {
                return InteractionResult.FAIL;
            }
            world.setBlock(pos, state.setValue(LIGHT_0_3, 3), 3);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(Waterloggable.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (stateIn.getValue(WATERLOGGED)) {
            ticks.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

}
