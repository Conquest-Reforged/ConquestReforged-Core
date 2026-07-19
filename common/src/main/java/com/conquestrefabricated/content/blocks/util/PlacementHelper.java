package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.content.blocks.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class PlacementHelper {

    /**
     * Helper method for safely calling BlockItemUseContext.replacingClickedOnBlock() which
     * may StackOverflow if it's an instanceof DirectionalPlaceContext.
     */
    public static boolean replacingClickedOnBlock(BlockPlaceContext context) {
        // filter out DirectionPlaceContexts as they are not caused by Players
        if (context instanceof DirectionalPlaceContext) {
            return false;
        }
        return context.replacingClickedOnBlock();
    }

    /**
     * Helper to determine whether an IBlockReader is being used during world gen or
     * during game-play.
     */
    public static boolean isDuringWorldGen(BlockGetter reader) {
        return !(reader instanceof Level);
    }

    /**
     * Helper to determine what GameMode has been set in the world, if the provided
     * IBlockReader happens to be an IWorld instance.
     */
    //public static boolean isGameMode(IBlockReader reader, GameType mode) {
    //    if (reader instanceof IWorld) {
    //        return ((IWorld) reader).getLevelData().getGameType() == mode;
    //    }
    //    return false;
    //}

    /**
     * Helper to determine which Direction BlockState should be returned depending on
     * which half of a block's side the player is interacting with.
     * Similar to how the Slab does this on the Y-axis.
     */
    public static Direction getHitVecHorizontalAxisDirection(Direction facing, BlockPos pos, BlockPlaceContext context) {
        switch (facing) {
            case NORTH: {
                return (!(context.getClickLocation().x - (double) pos.getX() > 0.5D)) ? facing.getClockWise() : facing;
            }
            case SOUTH: {
                return (!(context.getClickLocation().x - (double) pos.getX() < 0.5D)) ? facing.getClockWise() : facing;
            }
            case EAST: {
                return (!(context.getClickLocation().z - (double) pos.getZ() > 0.5D)) ? facing.getClockWise() : facing;
            }
            default: {
                return (!(context.getClickLocation().z - (double) pos.getZ() < 0.5D)) ? facing.getClockWise() : facing;
            }
        }
    }

    public static int getOrdinalDirection(BlockPlaceContext ctx, int ordinalState) {
        Player player = ctx.getPlayer();
        if (player != null) {
            float yaw = player.getYRot() % 360;
            if (yaw < 0) yaw += 360;

            // Check if player is looking in an ordinal direction
            boolean isOrdinal = false;
            float tolerance = 25.0f;
            isOrdinal = (Math.abs(yaw - 45) <= tolerance) ||
                    (Math.abs(yaw - 135) <= tolerance) ||
                    (Math.abs(yaw - 225) <= tolerance) ||
                    (Math.abs(yaw - 315) <= tolerance);

            if (isOrdinal) {
                return ordinalState;
            }
        }
        return 1;
    }

    /**
     * Method for seeing whether a block is being placed on a slab fitting for the direction.
     * Usually used for determining whether the "OFFSET_TOGGLE" property is true or false.
     */
    public static boolean isFacingSlab(BlockPlaceContext context) {
        Direction facing = context.getClickedFace().getOpposite();
        BlockGetter blockreader = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = blockreader.getBlockState(pos.relative(facing));
        Block block = state.getBlock();
        boolean isSlab = false;
        if (facing == Direction.DOWN) {
            isSlab = block instanceof Slab ||
                    block instanceof SlabBlock ||
                    block instanceof Layer ||
                    block instanceof SlabEighth ||
                    block instanceof SlabCorner ||
                    block instanceof SlabQuarter ||
                    block instanceof SnowLayerBlock ||
                    block instanceof SlabLessLayers ||
                    block instanceof BoardsHorizontal;
        } else if (facing != Direction.UP) { // If facing is not up or down, it is horizontal
            isSlab = block instanceof VerticalSlab && state.getValue(VerticalSlab.DIRECTION) == context.getClickedFace();
        }
        return isSlab;
    }

    /**
     * Applies corner-style mirroring to a BlockState with a DIRECTION property.
     * This transforms directions in a way that creates mirrored corner orientations.
     *
     * @param state The BlockState to mirror
     * @param mirrorIn The type of mirror to apply
     * @param directionProperty The direction property to modify
     * @param fallbackMirror Fallback mirror method to call if no custom mirroring is applied
     * @return The mirrored BlockState
     */
    public static BlockState mirrorCornerDirection(BlockState state, Mirror mirrorIn,
                                                   EnumProperty<Direction> directionProperty,
                                                   java.util.function.BiFunction<BlockState, Mirror, BlockState> fallbackMirror) {
        switch (mirrorIn) {
            case FRONT_BACK:
                switch(state.getValue(directionProperty)) {
                    case NORTH:
                        return state.setValue(directionProperty, Direction.EAST);
                    case EAST:
                        return state.setValue(directionProperty, Direction.NORTH);
                    case SOUTH:
                        return state.setValue(directionProperty, Direction.WEST);
                    case WEST:
                        return state.setValue(directionProperty, Direction.SOUTH);
                    default:
                        return fallbackMirror.apply(state, mirrorIn);
                }
            case LEFT_RIGHT:
                switch(state.getValue(directionProperty)) {
                    case NORTH:
                        return state.setValue(directionProperty, Direction.WEST);
                    case EAST:
                        return state.setValue(directionProperty, Direction.SOUTH);
                    case SOUTH:
                        return state.setValue(directionProperty, Direction.EAST);
                    case WEST:
                        return state.setValue(directionProperty, Direction.NORTH);
                    default:
                        return fallbackMirror.apply(state, mirrorIn);
                }
        }
        return fallbackMirror.apply(state, mirrorIn);
    }
}
