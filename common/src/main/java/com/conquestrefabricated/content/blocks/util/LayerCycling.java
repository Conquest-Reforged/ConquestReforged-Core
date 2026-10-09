package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.api.tags.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/**
 * How a partial block (slab, vertical slab, quarter, corner, pillar, layer) gets thicker.
 *
 * <p>Those blocks no longer stack by placing more of the same item onto them: every one of them drops
 * its full parent block however many layers it has, so stacking items would simply eat them. Instead
 * a mallet - anything in {@code conquest:cycling_tools} - cycles the layers in place, wrapping from
 * the thickest back to a single layer. Creative players keep the old way: placing the same item on a
 * partial block still adds a layer ({@link #stacksOnPlace}).</p>
 */
public final class LayerCycling {

    private LayerCycling() {
    }

    /** Whether the player placing is in creative, where partial blocks still stack by placing more of the same item. */
    public static boolean stacksOnPlace(BlockPlaceContext context) {
        Player player = context.getPlayer();
        return player != null && player.getAbilities().instabuild;
    }

    /**
     * Steps {@code layers} on {@code state} if {@code player} is holding a mallet.
     *
     * @return {@link InteractionResult#PASS} when they are not, so the block falls back to whatever it
     * would have done anyway
     */
    public static InteractionResult cycle(BlockState state, Level level, BlockPos pos, Player player, IntegerProperty layers) {
        if (!player.getMainHandItem().is(ModTags.CYCLING_TOOLS)) {
            return InteractionResult.PASS;
        }
        level.setBlock(pos, state.cycle(layers), 3);
        return InteractionResult.SUCCESS;
    }
}
