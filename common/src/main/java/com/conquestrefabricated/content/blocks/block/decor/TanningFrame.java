package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.CustomOffsetType;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.leatherworking.TanningFrameBlockEntity;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.conquestrefabricated.content.blocks.util.PlacementHelper.isFacingSlab;

/**
 * A frame to stretch a skin on while it is worked.
 *
 * <p>The {@link #STRETCHED} property is whether there is a skin on it at all, so the model can show one.
 * What is on it and how far along it is are the block entity's business, driven by
 * {@code conquest:stretching} recipes. There is no menu: a right-click with something the frame can
 * work mounts it, a tool applies a manual step, an empty hand shows the progress or collects the
 * finished piece, and sneaking with an empty hand takes it back off.</p>
 *
 * @see TanningFrameBlockEntity
 */
@Render(RenderLayer.CUTOUT)
public class TanningFrame extends HorizontalDirectional implements EntityBlock {

    public static final BooleanProperty STRETCHED = BooleanProperty.create("stretched");
    public static final BooleanProperty OFFSET_TOGGLE = ModBlockProperties.OFFSET_TOGGLE;

    public TanningFrame(Props props) {
        super(props
                .customOffsetType(CustomOffsetType.LAYER_XYZ)
                .offset(BlockBehaviour.OffsetType.NONE)
                .dynamicBounds(true)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(STRETCHED, false).setValue(OFFSET_TOGGLE, false));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (state.getValue(OFFSET_TOGGLE)) {
            return Shapes.empty();
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    protected void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STRETCHED, OFFSET_TOGGLE);
    }

    @Override
    @NotNull
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(STRETCHED, false)
                .setValue(OFFSET_TOGGLE, isFacingSlab(context));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TanningFrameBlockEntity frame) {
            if (!player.getAbilities().mayBuild) {
                return InteractionResult.FAIL;
            }
            InteractionResult result = frame.useItem(stack, player, hand);
            if (result != null) {
                return result;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        // Reached with something in hand when it was no use to the frame - leave that to place a block.
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }
        if (level.getBlockEntity(pos) instanceof TanningFrameBlockEntity frame) {
            return frame.useEmpty(player);
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TanningFrameBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                            BlockEntityType<T> type) {
        if (level.isClientSide() || type != TileEntityTypes.TANNING_FRAME) {
            return null;
        }
        return (world, pos, blockState, blockEntity) -> ((TanningFrameBlockEntity) blockEntity).tick(world);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof TanningFrameBlockEntity frame ? frame.signal(level) : 0;
    }
}
