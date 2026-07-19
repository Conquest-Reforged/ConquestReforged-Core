package com.conquestrefabricated.content.blocks.block.windows;

import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.block.base.WaterloggedHorizontalDirectionalShape;
import com.conquestrefabricated.core.block.properties.Waterloggable;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Assets(
        state = @State(name = "%s_arrowslit", template = "parent_arrowslit"),
        item = @Model(name = "item/%s_arrowslit", parent = "block/%s_arrowslit", template = "item/parent_arrowslit"),
        block = {
                @Model(name = "block/%s_arrowslit", template = "block/parent_arrowslit"),
        }
)
public class ArrowSlit extends WaterloggedHorizontalDirectionalShape implements Waterloggable {


    private static final VoxelShape NORTH_FRONT = Stream.of(Block.box(0, 0, 8, 3, 16, 16), Block.box(0, 0, 15, 7, 16, 16), Block.box(13, 0, 8, 16, 16, 16), Block.box(9, 0, 15, 16, 16, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape EAST_FRONT = Stream.of(Block.box(0, 0, 0, 8, 16, 3), Block.box(0, 0, 0, 1, 16, 7), Block.box(0, 0, 13, 8, 16, 16), Block.box(0, 0, 9, 1, 16, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SOUTH_FRONT = Stream.of(Block.box(13, 0, 0, 16, 16, 8), Block.box(9, 0, 0, 16, 16, 1), Block.box(0, 0, 0, 3, 16, 8), Block.box(0, 0, 0, 7, 16, 1)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape WEST_FRONT = Stream.of(Block.box(8, 0, 13, 16, 16, 16), Block.box(15, 0, 9, 16, 16, 16), Block.box(8, 0, 0, 16, 16, 3), Block.box(15, 0, 0, 16, 16, 7)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape NORTH_BACK = Stream.of(Block.box(0, 0, 0, 3, 16, 8), Block.box(0, 0, 7, 7, 16, 8), Block.box(13, 0, 0, 16, 16, 8), Block.box(9, 0, 7, 16, 16, 8)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape EAST_BACK = Stream.of(Block.box(8, 0, 0, 16, 16, 3), Block.box(8, 0, 0, 9, 16, 7), Block.box(8, 0, 13, 16, 16, 16), Block.box(8, 0, 9, 9, 16, 16)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape SOUTH_BACK = Stream.of(Block.box(13, 0, 8, 16, 16, 16), Block.box(9, 0, 8, 16, 16, 9), Block.box(0, 0, 8, 3, 16, 16), Block.box(0, 0, 8, 7, 16, 9)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    private static final VoxelShape WEST_BACK = Stream.of(Block.box(0, 0, 13, 8, 16, 16), Block.box(7, 0, 9, 8, 16, 16), Block.box(0, 0, 0, 8, 16, 3), Block.box(7, 0, 0, 8, 16, 7)).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final EnumProperty<Position> POSITION = EnumProperty.create("position", Position.class);

    public ArrowSlit(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(DIRECTION, Direction.NORTH).setValue(WATERLOGGED, false));

    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(DIRECTION, context.getHorizontalDirection().getOpposite()).setValue(POSITION, this.getBlockDirectionalHalf(context));
    }

    private Position getBlockDirectionalHalf(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection();
        Vec3 hit = context.getClickLocation();

        // If the player clicked a N/S/E/W face, always place FRONT
        Direction hitFace = context.getClickedFace();
        if (hitFace.getAxis().isHorizontal()) {
            return Position.FRONT;
        }

        // Hit was on top or bottom face — check depth along the relevant axis
        double dx = hit.x - blockPos.getX(); // 0.0 to 1.0 within the block
        double dz = hit.z - blockPos.getZ();

        return switch (facing) {
            // Player faces NORTH (arrow points north), front is the north side (z=0)
            // farther from player = smaller Z = FRONT
            case NORTH -> dz < 0.5 ? Position.FRONT : Position.BACK;

            // Player faces SOUTH, front is south side (z=1)
            // farther from player = larger Z = FRONT
            case SOUTH -> dz > 0.5 ? Position.FRONT : Position.BACK;

            // Player faces WEST, front is west side (x=0)
            case WEST -> dx < 0.5 ? Position.FRONT : Position.BACK;

            // Player faces EAST, front is east side (x=1)
            case EAST -> dx > 0.5 ? Position.FRONT : Position.BACK;

            default -> Position.FRONT;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        if (state.getValue(POSITION) == Position.FRONT) {
            return switch (state.getValue(DIRECTION)) {
                case EAST -> EAST_FRONT;
                case SOUTH -> SOUTH_FRONT;
                case WEST -> WEST_FRONT;
                default -> NORTH_FRONT;
            };
        } else {
            return switch (state.getValue(DIRECTION)) {
                case EAST -> EAST_BACK;
                case SOUTH -> SOUTH_BACK;
                case WEST -> WEST_BACK;
                default -> NORTH_BACK;
            };
        }
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POSITION);
    }

    public enum Position implements StringRepresentable {
        FRONT,
        BACK;

        public String toString() {
            return this.getSerializedName();
        }

        public String getSerializedName() {
            return this == BACK ? "back" : "front";
        }
    }

}
