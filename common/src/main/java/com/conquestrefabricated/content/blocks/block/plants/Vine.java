package com.conquestrefabricated.content.blocks.block.plants;

import com.conquestrefabricated.api.tags.ModTags;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

public class Vine extends VineBlock {

    public Vine(Props properties) {
        super(properties.tag(ModTags.VINE).toSettings());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 0.0F;
    }

    @Override
    public float getMaxVerticalOffset() {
        return 0.0F;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());
        boolean bl = blockState.is(this);
        BlockState blockState2 = bl ? blockState : this.defaultBlockState();

        for(Direction direction : ctx.getNearestLookingDirections()) {
            if (direction != Direction.DOWN) {
                BooleanProperty booleanProperty = getPropertyForFace(direction);
                boolean bl2 = bl && (Boolean)blockState.getValue(booleanProperty);
                if (!bl2 && this.canSupportAtFace(ctx.getLevel(), ctx.getClickedPos(), direction)) {
                    return (BlockState)blockState2.setValue(booleanProperty, true);
                }
            }
        }

        //New Condition added by CR
        if (ctx.getLevel().getBlockState(ctx.getClickedPos().above()).isSolid() || ctx.getLevel().getBlockState(ctx.getClickedPos().above()).getBlock() instanceof VineBlock) {
            BooleanProperty booleanProperty = getPropertyForFace(ctx.getHorizontalDirection());
            return blockState2.setValue(booleanProperty, true);
        }

        //New Condition added by CR
        if (ctx.getLevel() instanceof ServerLevel serverLevel && !serverLevel.getGameRules().get(GameRules.SPREAD_VINES)) {
            BooleanProperty booleanProperty = getPropertyForFace(ctx.getHorizontalDirection());
            return blockState2.setValue(booleanProperty, true);
        }


        return bl ? blockState2 : null;
    }

    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (world.getGameRules().get(GameRules.SPREAD_VINES)) {
            if (random.nextInt(4) == 0) {
                Direction direction = Direction.getRandom(random);
                BlockPos blockPos = pos.above();
                if (direction.getAxis().isHorizontal() && !(Boolean)state.getValue(getPropertyForFace(direction))) {
                    if (this.canSpread(world, pos)) {
                        BlockPos blockPos2 = pos.relative(direction);
                        BlockState blockState = world.getBlockState(blockPos2);
                        if (blockState.isAir()) {
                            Direction direction2 = direction.getClockWise();
                            Direction direction3 = direction.getCounterClockWise();
                            boolean bl = (Boolean)state.getValue(getPropertyForFace(direction2));
                            boolean bl2 = (Boolean)state.getValue(getPropertyForFace(direction3));
                            BlockPos blockPos3 = blockPos2.relative(direction2);
                            BlockPos blockPos4 = blockPos2.relative(direction3);
                            if (bl && isAcceptableNeighbour(world, blockPos3, direction2)) {
                                world.setBlock(blockPos2, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(direction2), true), 2);
                            } else if (bl2 && isAcceptableNeighbour(world, blockPos4, direction3)) {
                                world.setBlock(blockPos2, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(direction3), true), 2);
                            } else {
                                Direction direction4 = direction.getOpposite();
                                if (bl && world.isEmptyBlock(blockPos3) && isAcceptableNeighbour(world, pos.relative(direction2), direction4)) {
                                    world.setBlock(blockPos3, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(direction4), true), 2);
                                } else if (bl2 && world.isEmptyBlock(blockPos4) && isAcceptableNeighbour(world, pos.relative(direction3), direction4)) {
                                    world.setBlock(blockPos4, (BlockState)this.defaultBlockState().setValue(getPropertyForFace(direction4), true), 2);
                                } else if ((double)random.nextFloat() < 0.05 && isAcceptableNeighbour(world, blockPos2.above(), Direction.UP)) {
                                    world.setBlock(blockPos2, (BlockState)this.defaultBlockState().setValue(UP, true), 2);
                                }
                            }
                        } else if (isAcceptableNeighbour(world, blockPos2, direction)) {
                            world.setBlock(pos, (BlockState)state.setValue(getPropertyForFace(direction), true), 2);
                        }

                    }
                } else {
                    if (direction == Direction.UP && pos.getY() < world.getMaxY() - 1) {
                        if (this.canSupportAtFace(world, pos, direction)) {
                            world.setBlock(pos, (BlockState)state.setValue(UP, true), 2);
                            return;
                        }

                        if (world.isEmptyBlock(blockPos)) {
                            if (!this.canSpread(world, pos)) {
                                return;
                            }

                            BlockState blockState2 = state;

                            for(Direction direction2 : Direction.Plane.HORIZONTAL) {
                                if (random.nextBoolean() || !isAcceptableNeighbour(world, blockPos.relative(direction2), direction2)) {
                                    blockState2 = (BlockState)blockState2.setValue(getPropertyForFace(direction2), false);
                                }
                            }

                            if (this.hasHorizontalConnection(blockState2)) {
                                world.setBlock(blockPos, blockState2, 2);
                            }

                            return;
                        }
                    }

                    if (pos.getY() > world.getMinY()) {
                        BlockPos blockPos2 = pos.below();
                        BlockState blockState = world.getBlockState(blockPos2);
                        if (blockState.isAir() || blockState.is(this)) {
                            BlockState blockState3 = blockState.isAir() ? this.defaultBlockState() : blockState;
                            BlockState blockState4 = this.copyRandomFaces(state, blockState3, random);
                            if (blockState3 != blockState4 && this.hasHorizontalConnection(blockState4)) {
                                world.setBlock(blockPos2, blockState4, 2);
                            }
                        }
                    }

                }
            }
        }
    }

    private BlockState copyRandomFaces(BlockState above, BlockState state, RandomSource random) {
        for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextBoolean()) {
                BooleanProperty booleanProperty = getPropertyForFace(direction);
                if ((Boolean)above.getValue(booleanProperty)) {
                    state = (BlockState)state.setValue(booleanProperty, true);
                }
            }
        }

        return state;
    }

    private boolean hasHorizontalConnection(BlockState state) {
        return (Boolean)state.getValue(NORTH) || (Boolean)state.getValue(EAST) || (Boolean)state.getValue(SOUTH) || (Boolean)state.getValue(WEST);
    }

    private boolean canSupportAtFace(BlockGetter world, BlockPos pos, Direction side) {
        if (side == Direction.DOWN) {
            return false;
        } else {
            BlockPos blockPos = pos.relative(side);
            if (isAcceptableNeighbour(world, blockPos, side)) {
                return true;
            } else if (side.getAxis() == Direction.Axis.Y) {
                return false;
            } else {
                BooleanProperty booleanProperty = PROPERTY_BY_DIRECTION.get(side);
                BlockState blockState = world.getBlockState(pos.above());
                return (blockState.getBlock() instanceof VineBlock && blockState.getValue(booleanProperty) //change to use instanceof
                        || blockState.isSolid()
                ); //Change is here, added condition for isSolid
            }
        }
    }

    public static boolean isAcceptableNeighbour(BlockGetter world, BlockPos pos, Direction direction) {
        return MultifaceBlock.canAttachTo(world, direction, pos, world.getBlockState(pos)) || Block.canSupportRigidBlock(world, pos); //Add condition hasTopRim
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        if (blockState.is(this)) {
            if (context.getClickedFace() == Direction.UP || context.getClickedFace() == Direction.DOWN) {
                return false;
            }
            return this.countFaces(blockState) < PROPERTY_BY_DIRECTION.size();
        } else {
            return super.canBeReplaced(state, context);
        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == Direction.DOWN) {
            return super.updateShape(stateIn, level, ticks, currentPos, directionToNeighbour, neighbourPos, neighbourState, random);
        } else {
            BlockState blockState = this.getUpdatedState(stateIn, level, currentPos);
            return !(this.hasFaces(blockState) || level.getBlockState(currentPos.above()).isSolid() || (level.getBlockState(currentPos.above()).getBlock() instanceof VineBlock)) ? Blocks.AIR.defaultBlockState() : blockState; //Changes here isSolid
        }
    }

    private BlockState getUpdatedState(BlockState state, BlockGetter world, BlockPos pos) {
        BlockPos blockPos = pos.above();
        if ((Boolean)state.getValue(UP)) {
            state = (BlockState)state.setValue(UP, isAcceptableNeighbour(world, blockPos, Direction.DOWN));
        }

        BlockState blockState = null;

        for(Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty booleanProperty = getPropertyForFace(direction);
            if ((Boolean)state.getValue(booleanProperty)) {
                boolean bl = this.canSupportAtFace(world, pos, direction);
                if (!bl) {
                    if (blockState == null) {
                        blockState = world.getBlockState(blockPos);
                    }

                    bl = blockState.is(this) && (Boolean)blockState.getValue(booleanProperty);
                }

                state = (BlockState)state.setValue(booleanProperty, bl);
            }
        }

        return state;
    }

    private boolean hasFaces(BlockState state) {
        return this.countFaces(state) > 0;
    }

    private int countFaces(BlockState state) {
        int i = 0;

        for(BooleanProperty booleanProperty : PROPERTY_BY_DIRECTION.values()) {
            if ((Boolean)state.getValue(booleanProperty)) {
                ++i;
            }
        }

        return i;
    }

    private boolean canSpread(BlockGetter world, BlockPos pos) {
        //Change is here, added condition for isSolid
        if (world.getBlockState(pos.above()).isSolid()) {
            return true;
        }

        int i = 4;
        Iterable<BlockPos> iterable = BlockPos.betweenClosed(pos.getX() - 4, pos.getY() - 1, pos.getZ() - 4, pos.getX() + 4, pos.getY() + 1, pos.getZ() + 4);
        int j = 5;

        for(BlockPos blockPos : iterable) {
            if (world.getBlockState(blockPos).is(this)) {
                --j;
                if (j <= 0) {
                    return false;
                }
            }
        }

        return true;
    }
}
