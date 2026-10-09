package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.api.tags.ModTags;
import com.conquestrefabricated.content.blocks.block.directional.HorizontalDirectional;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.content.curing.CuringVesselBlockEntity;
import com.conquestrefabricated.core.asset.annotation.ItemDescription;
import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Crates that food can be packed in: the same blocks as their plain namesakes - same shape, same models, same
 * toggle - with a screen on top. Right-click one and it packs whatever a {@code conquest:curing} recipe
 * says, salting meat and fish or putting vegetables up in jars. See {@link CuringVesselBlockEntity}.
 *
 * <p>The plain crates change their look on an empty-handed click, in creative mode or with a mallet. That
 * still works with a mallet in hand, or by sneaking in creative; any other click opens the screen.</p>
 *
 * <p>Two classes because the two crates differ in kind - the large one is a model block, the small one has a
 * facing and a position - and a block can have only one parent.</p>
 */
public final class CuringCrate {

    private CuringCrate() {
    }

    /** The click: change the look if that is what was meant, otherwise open the screen. */
    static InteractionResult use(Level level, BlockPos pos, Player player, java.util.function.Supplier<InteractionResult> toggle) {
        boolean toggling = player.getMainHandItem().is(ModTags.CYCLING_TOOLS)
                || (player.getAbilities().instabuild && player.isShiftKeyDown());
        if (toggling) {
            return toggle.get();
        }
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MenuProvider provider) {
            player.openMenu(provider);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    static <T extends BlockEntity> BlockEntityTicker<T> ticker(Level level, BlockEntityType<T> type) {
        if (level.isClientSide() || type != TileEntityTypes.CURING_VESSEL) {
            return null;
        }
        return (world, pos, state, entity) -> ((CuringVesselBlockEntity) entity).tick(world, pos, state);
    }

    /** The large crate: a model block with three looks. */
    @ItemDescription(description = "toggle_3")
    public static class Model extends ModelBlock.Toggle3 implements EntityBlock {

        public Model(Props props) {
            super(props);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                                   BlockHitResult hit) {
            return use(level, pos, player, () -> super.useWithoutItem(state, level, pos, player, hit));
        }

        @Override
        public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new CuringVesselBlockEntity(pos, state);
        }

        @Override
        public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                                BlockEntityType<T> type) {
            return ticker(level, type);
        }
    }

    /** The small crate: facing, position and four looks. */
    @ItemDescription(description = "toggle_4")
    public static class Directional extends HorizontalDirectional.OffsetXYZ.Toggle4 implements EntityBlock {

        public Directional(Props props) {
            super(props);
        }

        @Override
        protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                                   BlockHitResult hit) {
            return use(level, pos, player, () -> super.useWithoutItem(state, level, pos, player, hit));
        }

        @Override
        public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new CuringVesselBlockEntity(pos, state);
        }

        @Override
        public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                                BlockEntityType<T> type) {
            return ticker(level, type);
        }
    }
}
