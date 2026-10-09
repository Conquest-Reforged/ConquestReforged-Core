package com.conquestrefabricated.content.salt;

import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Scooping the sea. A bucket or glass bottle used on a source block of ocean water takes sea water
 * instead of plain: the bucket empties the block exactly as vanilla does, the bottle leaves it.
 * Everywhere else, and with the salt option off, the vanilla items do what they always did.
 */
public final class SaltEvents {

    private SaltEvents() {
    }

    public static void register() {
        InteractionEvent.RIGHT_CLICK_ITEM.register(SaltEvents::scoop);
    }

    private static InteractionResult scoop(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean bucket = held.is(Items.BUCKET);
        if (!(bucket || held.is(Items.GLASS_BOTTLE)) || !Salt.enabled()) {
            return InteractionResult.PASS;
        }

        Level level = player.level();
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(player.blockInteractionRange()));
        BlockHitResult hit = level.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, player));
        if (hit.getType() != HitResult.Type.BLOCK || !Salt.isSeaSource(level, hit.getBlockPos())) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hit.getBlockPos();
        ItemStack filled = Salt.first(bucket ? Salt.SEA_WATER_BUCKETS : Salt.SEA_WATER_BOTTLES);
        if (filled.isEmpty() || !level.mayInteract(player, pos)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (bucket) {
            // Take the water the way vanilla's bucket does: a waterlogged block keeps being a block.
            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof BucketPickup pickup) || pickup.pickupBlock(player, level, pos, state).isEmpty()) {
                return InteractionResult.PASS;
            }
        }
        level.playSound(null, pos, bucket ? SoundEvents.BUCKET_FILL : SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, filled));
        return InteractionResult.SUCCESS;
    }
}
