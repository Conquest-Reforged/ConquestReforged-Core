package com.conquestrefabricated.content.cauldron;

import com.conquestrefabricated.content.blocks.block.decor.CookingPot;
import com.conquestrefabricated.content.blocks.util.CauldronBehavior;
import com.conquestrefabricated.content.salt.Salt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

/**
 * What every cauldron that can cook has in common, whether it is vanilla's {@code minecraft:cauldron}
 * (reached by a mixin) or one of Conquest's own pots ({@link CookingPot}): which blocks count, how full
 * they are, and what right-clicking them does.
 *
 * <p>Vanilla's is a different block empty and with water, so none of this is kept on the block - see
 * {@link CauldronStore}.</p>
 */
public final class CauldronBlocks {

    private CauldronBlocks() {
    }

    /** Whether this is a cauldron that can be cooked in: the empty and water ones, and Conquest's pots. */
    public static boolean cookable(BlockState state) {
        return state.is(Blocks.CAULDRON) || state.is(Blocks.WATER_CAULDRON) || state.getBlock() instanceof CookingPot;
    }

    /** Levels of water in it, 0 to 3. */
    public static int fluidLevel(BlockState state) {
        if (state.is(Blocks.WATER_CAULDRON)) {
            return state.getValue(LayeredCauldronBlock.LEVEL);
        }
        if (state.getBlock() instanceof CookingPot) {
            return state.getValue(CauldronBehavior.LEVEL);
        }
        return 0;
    }

    /** The same cauldron holding {@code level} levels of water. */
    public static BlockState withFluidLevel(BlockState state, int level) {
        int clamped = Math.max(0, Math.min(3, level));
        if (state.getBlock() instanceof CookingPot) {
            return state.setValue(CauldronBehavior.LEVEL, clamped);
        }
        return clamped == 0 ? Blocks.CAULDRON.defaultBlockState()
                : Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, clamped);
    }

    /**
     * A right-click with something in hand, ahead of whatever the block would do with it.
     *
     * @return what to answer, or null if this has no say and the block should carry on
     */
    public static @Nullable InteractionResult useItem(ItemStack held, BlockState state, Level level, BlockPos pos,
                                                      Player player, InteractionHand hand) {
        if (!cookable(state) || held.isEmpty()) {
            return null;
        }
        boolean sea = Salt.isSeaBucket(held) || Salt.isSeaBottle(held);
        boolean taking = held.is(Items.BUCKET) || held.is(Items.GLASS_BOTTLE);
        boolean fresh = held.is(Items.WATER_BUCKET) || isWaterBottle(held);
        if (!sea && !taking && !fresh) {
            return null;
        }
        if (!(level instanceof ServerLevel server)) {
            return sea ? InteractionResult.SUCCESS : null;
        }

        CauldronStore store = CauldronStore.of(server);
        CauldronData data = store.peek(pos);
        int water = fluidLevel(state);
        boolean brine = data != null && data.isBrine() && water > 0;

        if (sea) {
            if ((water > 0 && !brine) || water >= 3) {
                return null;
            }
            int filled = Salt.isSeaBucket(held) ? 3 : water + 1;
            level.setBlockAndUpdate(pos, withFluidLevel(state, filled));
            store.data(pos).setBrine(true);
            player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, Salt.emptied(held)));
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
            return InteractionResult.SUCCESS;
        }
        if (brine && taking) {
            player.sendOverlayMessage(Component.translatable("message.conquest.cauldron.too_salty"));
            return InteractionResult.SUCCESS;
        }
        if (brine && fresh) {
            // Fresh water on top of it: whatever is in there is no longer brine. The block carries on as usual.
            data.setBrine(false);
        }
        return null;
    }

    private static boolean isWaterBottle(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return stack.is(Items.POTION) && contents != null && contents.is(Potions.WATER);
    }

    /** An empty-handed right-click: opens the cooking screen. */
    public static InteractionResult open(BlockState state, Level level, BlockPos pos, Player player) {
        if (!cookable(state)) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel server) {
            CauldronData data = CauldronStore.of(server).data(pos);
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, who) -> new CauldronMenu(id, inventory, server, pos, data),
                    Component.translatable("container.conquest.cauldron")));
        }
        return InteractionResult.SUCCESS;
    }

    /** Whether rain should be kept out: it would turn brine fresh without telling anyone. */
    public static boolean isBrine(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel server)) {
            return false;
        }
        CauldronData data = CauldronStore.of(server).peek(pos);
        return data != null && data.isBrine();
    }
}
