package com.conquestrefabricated.content.blocks.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Plants lowered onto a layer/slab keep their flat pick box in the cell above, but the box
 * itself sits in the cell below. A ray that stays inside the lower cell never visits the plant's
 * cell, so vanilla's raycast misses it. This tests the plant above every cell the ray crosses.
 */
public final class PlantPickHelper {

    private PlantPickHelper() {
    }

    public static HitResult refine(Entity entity, double range, float partialTick, HitResult original) {
        if (!(entity instanceof Player player)) {
            return original;
        }
        Level level = entity.level();
        if (!level.isClientSide()) {
            return original;
        }

        Vec3 from = entity.getEyePosition(partialTick);
        Vec3 to = from.add(entity.getViewVector(partialTick).scale(range));
        CollisionContext context = CollisionContext.of(player);

        double bestDistance = original.getType() == HitResult.Type.BLOCK
                ? original.getLocation().distanceToSqr(from)
                : Double.MAX_VALUE;
        BlockHitResult[] best = new BlockHitResult[1];

        BlockGetter.traverseBlocks(from, to, level, (lvl, pos) -> {
            BlockPos plantPos = pos.above();
            BlockState state = lvl.getBlockState(plantPos);
            if (PlantHitboxes.isFlatPlant(state.getBlock()) && state.getOffset(plantPos).y < 0.0D) {
                VoxelShape shape = state.getShape(lvl, plantPos, context);
                if (!shape.isEmpty()) {
                    BlockHitResult hit = shape.clip(from, to, plantPos);
                    if (hit != null) {
                        double distance = hit.getLocation().distanceToSqr(from);
                        if (distance < bestDistance && (best[0] == null || distance < best[0].getLocation().distanceToSqr(from))) {
                            best[0] = hit;
                        }
                    }
                }
            }
            return null;
        }, lvl -> null);

        return keepInCell(level, best[0] != null ? best[0] : original);
    }

    /**
     * The hit on a lowered plant lies below its own cell. The server rejects a use-item packet whose
     * hit location is more than a block from the clicked block's centre (which is what the debug
     * stick, toggles and block placement all go through), so pull the location back up to the cell.
     */
    private static HitResult keepInCell(Level level, HitResult result) {
        if (!(result instanceof BlockHitResult hit) || result.getType() != HitResult.Type.BLOCK) {
            return result;
        }
        BlockPos pos = hit.getBlockPos();
        Vec3 location = hit.getLocation();
        if (location.y >= pos.getY() || !PlantHitboxes.isFlatPlant(level.getBlockState(pos).getBlock())) {
            return result;
        }
        return new BlockHitResult(new Vec3(location.x, pos.getY(), location.z), hit.getDirection(), pos, hit.isInside());
    }
}
