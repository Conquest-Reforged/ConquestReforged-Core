package com.conquestrefabricated.content.blocks.block.decor;

import com.conquestrefabricated.content.cauldron.CauldronBlocks;
import com.conquestrefabricated.content.salt.Salt;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A cauldron you can cook in, as vanilla's can be: it is an ordinary Conquest {@link Cauldron} - buckets
 * and bottles fill and drain it - and on top of that an empty-handed right-click opens a screen with
 * fuel, ingredient and output slots, working off {@code conquest:cauldron} recipes.
 *
 * <p>Sea water can be poured into it, and then it can be boiled down for salt; it cannot be scooped out
 * again, only boiled away or diluted. All of that is in {@link CauldronBlocks}, shared with vanilla's
 * cauldron so the two behave alike.</p>
 */
@Render(RenderLayer.CUTOUT)
@SpecialOffset(offsetType = SpecialOffsetType.XYZ)
public class CookingPot extends Cauldron {

    public CookingPot(Props props) {
        super(props);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (Salt.enabled()) {
            InteractionResult result = CauldronBlocks.useItem(stack, state, level, pos, player, hand);
            if (result != null) {
                return result;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        return CauldronBlocks.open(state, level, pos, player);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        // Rain does not top up a pot of brine; it would make it fresh by the back door.
        if (!CauldronBlocks.isBrine(level, pos)) {
            super.handlePrecipitation(state, level, pos, precipitation);
        }
    }
}
