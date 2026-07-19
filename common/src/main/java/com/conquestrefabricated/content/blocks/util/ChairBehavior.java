package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.tileentity.seat.SeatTileEntity;
import com.conquestrefabricated.content.entities.seat.SeatEntity;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ChairBehavior {
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    private final double chairHeight;

    public ChairBehavior(double chairHeight) {
        this.chairHeight = chairHeight;
    }

    public double getSeatHeight() {
        return chairHeight;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context, VoxelShape defaultShape) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        } else {
            return defaultShape;
        }
    }

    public boolean calculateSlabOffset(Direction facing, Block block, BlockState state, BlockPlaceContext context) {
        if (facing == Direction.DOWN) {
            return block instanceof Slab ||
                    block instanceof SlabBlock ||
                    block instanceof Layer ||
                    block instanceof SnowLayerBlock ||
                    block instanceof SlabLessLayers ||
                    block instanceof BoardsHorizontal;
        } else if (facing != Direction.UP) { // If facing is not up or down, it is horizontal
            return block instanceof VerticalSlab && state.getValue(VerticalSlab.DIRECTION) == context.getClickedFace();
        }
        return false;
    }

    public InteractionResult handleSittingInteraction(BlockState state, Level level, BlockPos blockPos, Player player, BlockHitResult hitResult, FallbackAction fallbackAction, Integer maxToggleValue) {
        final var seat = (SeatTileEntity) level.getBlockEntity(blockPos);

        //Make sure we're not on the client; we do Success because doing Pass allows the rest of the method to run potentially
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }



        // Check if player is sneaking (usually for toggling shape)
        if (player.isShiftKeyDown()) {
            return fallbackAction.execute();
        }

        // Check toggle value constraint if provided
        if (maxToggleValue != null && state.hasProperty(getToggleProperty(state))) {
            int toggleValue = state.getValue(getToggleProperty(state));
            if (toggleValue <= maxToggleValue) {
                // Create new seat
                return createNewSeat(seat, player, level, blockPos);
            } else {
                return fallbackAction.execute();
            }
        }

        // Handle existing seat entities
        List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(blockPos));
        if (!seats.isEmpty()) {
            SeatEntity seatEntity = seats.get(0);
            List<Entity> passengers = seatEntity.getPassengers();
            if (!passengers.isEmpty() && passengers.get(0) instanceof Player) {
                // Seat is occupied, end method)
                return InteractionResult.SUCCESS;
            }
            // Seat exists but is empty, take over the seat
            seatEntity.ejectPassengers();
            final boolean success = player.startRiding(seatEntity);
            if (success) {
                BlockState currentState = level.getBlockState(blockPos);
                level.sendBlockUpdated(blockPos, currentState, currentState, 3);
                seat.setChanged();
            }
            return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        // Create new seat
        return createNewSeat(seat, player, level, blockPos);
    }

    private InteractionResult createNewSeat(SeatTileEntity seat, Player player, Level level, BlockPos blockPos) {
        final boolean success = seat.rideSeat(player);
        if (success) {
            BlockState currentState = level.getBlockState(blockPos);
            level.sendBlockUpdated(blockPos, currentState, currentState, 3);
            seat.setChanged();
        }
        return success ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private net.minecraft.world.level.block.state.properties.IntegerProperty getToggleProperty(BlockState state) {
        // Try to find the TOGGLE property dynamically
        for (var property : state.getProperties()) {
            if (property.getName().equals("toggle")) {
                return (net.minecraft.world.level.block.state.properties.IntegerProperty) property;
            }
        }
        return null; // No toggle property found
    }

    public BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SeatTileEntity(blockPos, blockState);
    }

    /**
     * Functional interface for lazy evaluation of fallback actions
     */
    @FunctionalInterface
    public interface FallbackAction {
        InteractionResult execute();
    }
}