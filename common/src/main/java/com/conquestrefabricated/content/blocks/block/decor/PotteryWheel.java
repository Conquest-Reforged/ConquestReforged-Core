package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.pottery.PotteryWheelBlockEntity;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class PotteryWheel extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<PotteryWheel> CODEC = simpleCodec(PotteryWheel::new);

    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    protected static final VoxelShape BASE_X_AXIS = Block.box(0.0D, 0.0D, 1.0D, 16.0D, 4.0D, 15.0D);
    protected static final VoxelShape BASE_Z_AXIS = Block.box(1.0D, 0.0D, 0.0D, 15.0D, 4.0D, 16.0D);
    protected static final VoxelShape MIDDLE_X_AXIS = Block.box(0.0D, 4.0D, 2.0D, 16.0D, 10.0D, 14.0D);
    protected static final VoxelShape MIDDLE_Z_AXIS = Block.box(2.0D, 4.0D, 0.0D, 14.0D, 10.0D, 16.0D);
    protected static final VoxelShape TOP_X_AXIS = Block.box(0.0D, 10.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape TOP_Z_AXIS = Block.box(4.0D, 10.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape X_AXIS_AABB = Shapes.or(BASE_X_AXIS, MIDDLE_X_AXIS, TOP_X_AXIS);
    protected static final VoxelShape Z_AXIS_AABB = Shapes.or(BASE_Z_AXIS, MIDDLE_Z_AXIS, TOP_Z_AXIS);

    public PotteryWheel(Properties properties) {
        super(((BlockSettingsAccessor)properties).setCustomOffsetter(CustomOffsetType.LAYER_XYZ).offsetType(OffsetType.NONE).dynamicShape());
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch(direction.getAxis()) {
            case Z:
            default:
                return Z_AXIS_AABB;
            case X:
                return X_AXIS_AABB;
        }
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = PlacementHelper.isFacingSlab(context);

        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos = context.getClickedPos();
        BlockPos blockpos1 = blockpos.relative(direction);
        Level level = context.getLevel();
        return level.getBlockState(blockpos1).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(blockpos1) ? this.defaultBlockState().setValue(FACING, direction).setValue(OFFSET_TOGGLE, isSlab) : null;
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == getNeighbourDirection(stateIn.getValue(PART), stateIn.getValue(FACING))) {
            return neighbourState.is(this) && neighbourState.getValue(PART) != stateIn.getValue(PART) ? super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random) : Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType type) {
        return false;
    }

    private static Direction getNeighbourDirection(BedPart part, Direction direction) {
        return part == BedPart.FOOT ? direction : direction.getOpposite();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative()) {
            BedPart bedpart = state.getValue(PART);
            if (bedpart == BedPart.FOOT) {
                BlockPos blockpos = pos.relative(getNeighbourDirection(bedpart, state.getValue(FACING)));
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
                    level.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, entity, itemStack);
        if (!level.isClientSide()) {
            BlockPos blockpos = pos.relative(state.getValue(FACING));
            level.setBlockAndUpdate(blockpos, state.setValue(PART, BedPart.HEAD));
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    public long getSeed(BlockState state, BlockPos pos) {
        BlockPos blockpos = pos.relative(state.getValue(FACING), state.getValue(PART) == BedPart.HEAD ? 0 : 1);
        return Mth.getSeed(blockpos.getX(), pos.getY(), blockpos.getZ());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OFFSET_TOGGLE);
    }

    // ------------------------------------------------------------------------------ workstation

    /**
     * Only the foot carries the block entity. A wheel is two blocks and one machine, so the head is
     * a plain half that sends the player to the foot - see {@link #mainPos}.
     */
    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == BedPart.FOOT ? new PotteryWheelBlockEntity(pos, state) : null;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                            BlockEntityType<T> type) {
        return type == TileEntityTypes.POTTERY_WHEEL
                ? (world, pos, state1, blockEntity) -> ((PotteryWheelBlockEntity) blockEntity).tick(world, pos)
                : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()
                && level.getBlockEntity(mainPos(state, pos)) instanceof MenuProvider provider) {
            player.openMenu(provider);
        }
        return InteractionResult.SUCCESS;
    }

    /** Where the machine actually lives: the foot, whichever half was clicked. */
    private static BlockPos mainPos(BlockState state, BlockPos pos) {
        return state.getValue(PART) == BedPart.FOOT
                ? pos
                : pos.relative(state.getValue(FACING).getOpposite());
    }
}
