package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.blocks.util.CauldronBehavior;
import com.conquestrefabricated.content.leatherworking.SoakingBarrelBlockEntity;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * A water barrel that things can be left to soak in.
 *
 * <p>Everything a {@link Cauldron} does it still does - buckets and bottles fill and drain it - and on
 * top of that it takes one batch of something at a time and works it as long as a
 * {@code conquest:soaking} recipe says. There is no menu: a right-click with an ingredient starts it, an
 * empty hand shows how far it has got or collects what it made, and sneaking with an empty hand calls
 * it off.</p>
 *
 * @see SoakingBarrelBlockEntity
 */
@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class SoakingBarrel extends Cauldron implements EntityBlock {

    public SoakingBarrel(Props props) {
        super(props);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SoakingBarrelBlockEntity barrel) {
            if (!player.getAbilities().mayBuild) {
                return InteractionResult.FAIL;
            }
            InteractionResult result = barrel.useItem(stack, player, hand);
            if (result != null) {
                return result;
            }
        }
        // Not something to soak: it is a bucket, a bottle, or nothing to do with water at all.
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        // Reached with something in hand when it was no use to the barrel - leave that to place a block.
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }
        if (level.getBlockEntity(pos) instanceof SoakingBarrelBlockEntity barrel) {
            return barrel.useEmpty(player);
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SoakingBarrelBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                            BlockEntityType<T> type) {
        if (level.isClientSide() || type != TileEntityTypes.SOAKING_BARREL) {
            return null;
        }
        return (world, pos, blockState, blockEntity) ->
                ((SoakingBarrelBlockEntity) blockEntity).tick(world, pos, blockState);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return level.getBlockEntity(pos) instanceof SoakingBarrelBlockEntity barrel
                ? barrel.signal(state)
                : state.getValue(CauldronBehavior.LEVEL);
    }
}
