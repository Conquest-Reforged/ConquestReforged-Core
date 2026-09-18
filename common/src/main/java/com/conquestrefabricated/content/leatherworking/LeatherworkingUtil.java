package com.conquestrefabricated.content.leatherworking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** What the soaking barrel and the tanning frame both do to tell the player about themselves. */
final class LeatherworkingUtil {

    private LeatherworkingUtil() {
    }

    /** Hands a stack to a player, in stacks that fit, dropping whatever their inventory cannot take. */
    static void give(Player player, ItemStack stack) {
        ItemStack rest = stack.copy();
        while (!rest.isEmpty()) {
            player.getInventory().placeItemBackInInventory(rest.split(Math.min(rest.getCount(), rest.getMaxStackSize())));
        }
    }

    /** Drops stacks into the world, in stacks that fit, for a block that is broken with something in it. */
    static void dropAll(Level level, BlockPos pos, ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            ItemStack rest = stack.copy();
            while (!rest.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        rest.split(Math.min(rest.getCount(), rest.getMaxStackSize())));
            }
        }
    }

    /** The one-off cue that something is finished: a chime and a burst of green sparkles. */
    static void ready(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_CHIME.value(), SoundSource.BLOCKS, 0.8F, 1.4F);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    8, 0.3, 0.3, 0.3, 0.0);
        }
    }

    /** Puts a percentage on the player's action bar. */
    static void progress(Player player, String key, int progress, int duration) {
        int percent = duration <= 0 ? 0 : Math.min(100, progress * 100 / duration);
        player.sendOverlayMessage(Component.translatable(key, percent));
    }

    static void message(Player player, String key, Object... args) {
        player.sendOverlayMessage(Component.translatable(key, args));
    }

    /** Lets a neighbouring comparator know the signal may have changed. */
    static void notifyComparators(Level level, BlockPos pos) {
        level.updateNeighbourForOutputSignal(pos, level.getBlockState(pos).getBlock());
    }
}
