package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.content.blocks.block.plants.AbstractCropsBlock;
import com.conquestrefabricated.content.blocks.block.plants.Bush;
import com.conquestrefabricated.content.blocks.block.vanilla.BeetrootsVanilla;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The flat pick box shared by plants and crops. When a plant is lowered onto a layer/slab its model
 * is drawn shifted down by the offset, so the box is moved by the same offset to sit on the surface.
 */
public final class PlantHitboxes {

    public static final VoxelShape FLAT_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

    private PlantHitboxes() {
    }

    /** Whether this block uses the flat box (and so needs {@link PlantPickHelper} to be reachable). */
    public static boolean isFlatPlant(Block block) {
        return block instanceof Bush || block instanceof AbstractCropsBlock || block instanceof BeetrootsVanilla;
    }

    /** Players who can't build (adventure) pass through plants; everyone else sees the box. */
    public static boolean canTarget(Player player) {
        return player.getAbilities().instabuild || player.getAbilities().mayBuild;
    }

    public static VoxelShape flat(BlockState state, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof Player player && !canTarget(player)) {
                return Shapes.empty();
            }
        }
        return lowered(state, pos);
    }

    public static VoxelShape lowered(BlockState state, BlockPos pos) {
        Vec3 offset = state.getOffset(pos);
        return FLAT_SHAPE.move(offset.x, offset.y, offset.z);
    }
}
