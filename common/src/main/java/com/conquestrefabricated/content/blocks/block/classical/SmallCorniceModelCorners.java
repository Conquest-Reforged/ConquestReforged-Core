package com.conquestrefabricated.content.blocks.block.classical;

import com.conquestrefabricated.content.blocks.BlockSettingsAccessor;
import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.directional.Half;
import com.conquestrefabricated.content.blocks.util.PlacementHelper;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.util.RenderLayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.conquestrefabricated.core.block.properties.ModBlockProperties.TYPE_UPDOWN;

@Render(RenderLayer.CUTOUT)
public class SmallCorniceModelCorners extends Half.Directional {

    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    private final List<VoxelShape> hitBox;

    public SmallCorniceModelCorners(Props props) {
        super(((BlockSettingsAccessor) props.toSettings())
                .setCustomOffsetter(CustomOffsetType.LAYER_XZ)
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .dynamicShape()
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(OFFSET_TOGGLE, false).setValue(HINGE,  DoorHingeSide.LEFT));
        this.hitBox = props.get("hitBox", List.class);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean isSlab = PlacementHelper.isFacingSlab(context);
        Direction facingHorizontal = context.getHorizontalDirection().getOpposite();
        BlockState state2 = this.defaultBlockState().setValue(DIRECTION, facingHorizontal).setValue(TYPE_UPDOWN, net.minecraft.world.level.block.state.properties.Half.BOTTOM).setValue(OFFSET_TOGGLE, isSlab).setValue(HINGE, this.getHingeSide(context));
        Direction facing = context.getClickedFace();
        return facing != Direction.DOWN && (facing == Direction.UP || !(context.getClickLocation().y - (double) context.getClickedPos().getY() > 0.5D)) ? state2 : state2.setValue(TYPE_UPDOWN, net.minecraft.world.level.block.state.properties.Half.TOP).setValue(OFFSET_TOGGLE, isSlab);
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE_UPDOWN).add(OFFSET_TOGGLE).add(HINGE);
    }

    @Override
    public VoxelShape getShape(BlockState state) {
        boolean hasEightShapes = hitBox.size() == 8;
        switch (state.getValue(HINGE)) {
            case LEFT:
            default:
                if (state.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return hitBox.get(0);
                        case SOUTH:
                            return hitBox.get(hasEightShapes ? 1 : 0);
                        case EAST:
                            return hitBox.get(hasEightShapes ? 2 : 0);
                        case WEST:
                            return hitBox.get(hasEightShapes ? 3 : 0);
                    }
                } else {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return hitBox.get(hasEightShapes ? 4 : 0);
                        case SOUTH:
                            return hitBox.get(hasEightShapes ? 5 : 0);
                        case EAST:
                            return hitBox.get(hasEightShapes ? 6 : 0);
                        case WEST:
                            return hitBox.get(hasEightShapes ? 7 : 0);
                    }
                }
            case RIGHT:
                if (state.getValue(TYPE_UPDOWN) == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return hitBox.get(hasEightShapes ? 2 : 0);
                        case SOUTH:
                            return hitBox.get(hasEightShapes ? 3 : 0);
                        case EAST:
                            return hitBox.get(hasEightShapes ? 1 : 0);
                        case WEST:
                            return hitBox.get(0);
                    }
                } else {
                    switch (state.getValue(DIRECTION)) {
                        case NORTH:
                        default:
                            return hitBox.get(hasEightShapes ? 6 : 0);
                        case SOUTH:
                            return hitBox.get(hasEightShapes ? 7 : 0);
                        case EAST:
                            return hitBox.get(hasEightShapes ? 5 : 0);
                        case WEST:
                            return hitBox.get(hasEightShapes ? 4 : 0);
                    }
                }
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

    private DoorHingeSide getHingeSide(BlockPlaceContext context) {
        BlockGetter iblockreader = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getHorizontalDirection();
        BlockPos blockpos1 = blockpos.above();
        Direction direction1 = direction.getCounterClockWise();
        BlockPos blockpos2 = blockpos.relative(direction1);
        BlockState blockstate = iblockreader.getBlockState(blockpos2);
        BlockPos blockpos3 = blockpos1.relative(direction1);
        BlockState blockstate1 = iblockreader.getBlockState(blockpos3);
        Direction direction2 = direction.getClockWise();
        BlockPos blockpos4 = blockpos.relative(direction2);
        BlockState blockstate2 = iblockreader.getBlockState(blockpos4);
        BlockPos blockpos5 = blockpos1.relative(direction2);
        BlockState blockstate3 = iblockreader.getBlockState(blockpos5);
        int i = (blockstate.isCollisionShapeFullBlock(iblockreader, blockpos2) ? -1 : 0) + (blockstate1.isCollisionShapeFullBlock(iblockreader, blockpos3) ? -1 : 0) + (blockstate2.isCollisionShapeFullBlock(iblockreader, blockpos4) ? 1 : 0) + (blockstate3.isCollisionShapeFullBlock(iblockreader, blockpos5) ? 1 : 0);
        if (i <= 0) {
            if (i == 0) {
                int j = direction.getStepX();
                int k = direction.getStepZ();
                Vec3 vec3d = context.getClickLocation();
                double d0 = vec3d.x - (double) blockpos.getX();
                double d1 = vec3d.z - (double) blockpos.getZ();
                return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            } else {
                return DoorHingeSide.LEFT;
            }
        } else {
            return DoorHingeSide.RIGHT;
        }
    }
}