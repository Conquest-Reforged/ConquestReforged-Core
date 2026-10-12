package com.conquestrefabricated.content.treefelling;

import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A felled tree on its way down.
 *
 * <p>The blocks are already gone from the world; each is stood in for by a block display at its old
 * position, and the whole tree is turned as one rigid body about a hinge on the stump's edge on the side
 * it falls towards. The displays are only ever told where they will be a few ticks from now (the client
 * does the in-between), so a short step is chosen to keep the straight-line interpolation of each block's
 * corner close to its true arc. The fall starts slowly and gathers speed, as a real one does; when it
 * ends the drops appear where the blocks came to rest.</p>
 */
final class FallingTree {

    /** Ticks between updates to the displays, and how long each one takes to play. */
    private static final int STEP = 4;
    private static final int MAX_DISPLAYS = 3500;

    /** One block of the tree: where it started, and what to show. */
    record Piece(BlockPos pos, BlockState state) {
    }

    private final ServerLevel level;
    private final Vec3 pivot;
    private final Vec3 axis;
    private final double maxAngle;
    private final int duration;
    private final List<Piece> pieces;
    private final List<Display.BlockDisplay> displays = new ArrayList<>();
    private final List<ItemStack> drops;
    private final SoundEvent landSound;
    private int age;
    private boolean finished;

    /**
     * @param pieces what to draw, including the cut block
     * @param drops  everything the tree yields, merged, ready to land
     * @param fall   unit horizontal direction the tree falls towards
     */
    FallingTree(ServerLevel level, List<Piece> pieces, List<ItemStack> drops, Vec3 pivot, Vec3 fall, double maxAngle, int duration, SoundEvent landSound) {
        this.level = level;
        this.pieces = pieces;
        this.drops = drops;
        this.pivot = pivot;
        // Turning (0,1,0) towards the fall direction.
        this.axis = new Vec3(fall.z, 0, -fall.x);
        this.maxAngle = maxAngle;
        this.duration = Math.max(STEP * 2, duration - duration % STEP);
        this.landSound = landSound;
    }

    ServerLevel level() {
        return level;
    }

    boolean finished() {
        return finished;
    }

    /** Adds the displays to the world; {@code owned} is told about each first, so stale ones can be told apart. */
    void spawn(java.util.function.Consumer<Display.BlockDisplay> owned) {
        int shown = 0;
        for (Piece piece : pieces) {
            if (shown++ >= MAX_DISPLAYS) {
                break;
            }
            Display.BlockDisplay display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
            display.setPos(piece.pos().getX(), piece.pos().getY(), piece.pos().getZ());
            display.setBlockState(piece.state());
            display.setNoGravity(true);
            display.setInvulnerable(true);
            display.addTag(TreeFelling.TAG);
            owned.accept(display);
            level.addFreshEntity(display);
            displays.add(display);
        }
    }

    void tick() {
        if (finished) {
            return;
        }
        age++;
        // The first tick after spawning is left alone so the clients get the displays at rest first.
        if (age >= 2 && (age - 2) % STEP == 0 && age - 2 < duration) {
            int target = Math.min(duration, age - 2 + STEP);
            pose(angleAt((double) target / duration), STEP);
        }
        if (age - 2 >= duration + STEP) {
            land();
        }
    }

    /** Ends the fall now, with the tree lying where it was heading. */
    void finishNow() {
        if (!finished) {
            land();
        }
    }

    private double angleAt(double progress) {
        // Slow at first, then quickening: a tree hangs on its hinge before it goes.
        double eased = 1.4 * progress * progress - 0.4 * progress * progress * progress;
        return maxAngle * eased;
    }

    private void pose(double angle, int ticks) {
        Quaternionf rotation = new Quaternionf().fromAxisAngleRad((float) axis.x, (float) axis.y, (float) axis.z, (float) angle);
        for (int i = 0; i < displays.size(); i++) {
            Display.BlockDisplay display = displays.get(i);
            if (display.isRemoved()) {
                continue;
            }
            BlockPos pos = pieces.get(i).pos();
            Vec3 corner = new Vec3(pos.getX(), pos.getY(), pos.getZ());
            // Where the block's corner ends up, relative to where the display sits.
            Vec3 moved = rotate(corner.subtract(pivot), angle).add(pivot).subtract(corner);
            display.setTransformationInterpolationDuration(ticks);
            display.setTransformationInterpolationDelay(0);
            display.setTransformation(new Transformation(new Vector3f((float) moved.x, (float) moved.y, (float) moved.z), rotation, new Vector3f(1, 1, 1), new Quaternionf()));
        }
    }

    /** Rotates {@code v} about {@link #axis} (a unit vector) through {@code angle}. */
    private Vec3 rotate(Vec3 v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return v.scale(cos).add(axis.cross(v).scale(sin)).add(axis.scale(axis.dot(v) * (1 - cos)));
    }

    private Vec3 restingCentre(BlockPos pos) {
        Vec3 centre = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        return rotate(centre.subtract(pivot), maxAngle).add(pivot);
    }

    private void land() {
        finished = true;
        for (Display.BlockDisplay display : displays) {
            display.discard();
        }
        if (pieces.isEmpty()) {
            return;
        }
        var random = level.getRandom();

        // Dust and splinters where it hit: a sample of the blocks, not every one.
        int effects = Math.min(30, pieces.size());
        for (int i = 0; i < effects; i++) {
            Piece piece = pieces.get(random.nextInt(pieces.size()));
            Vec3 at = restingCentre(piece.pos());
            level.levelEvent(2001, BlockPos.containing(at), Block.getId(piece.state()));
        }
        Vec3 tip = restingCentre(pieces.get(random.nextInt(pieces.size())).pos());
        level.playSound(null, tip.x, tip.y, tip.z, landSound, SoundSource.BLOCKS, 2.5F, 0.55F);

        for (ItemStack stack : drops) {
            Vec3 at = restingCentre(pieces.get(random.nextInt(pieces.size())).pos());
            ItemEntity item = new ItemEntity(level, at.x, at.y + 0.25, at.z, stack);
            item.setDeltaMovement(random.nextGaussian() * 0.04, 0.1, random.nextGaussian() * 0.04);
            level.addFreshEntity(item);
        }
    }

    /** Whether any block of the tree, turned through {@code angle}, would be inside something solid that isn't a tree. */
    static boolean blocked(ServerLevel level, List<BlockPos> centres, Vec3 pivot, Vec3 axis, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (BlockPos pos : centres) {
            Vec3 v = new Vec3(pos.getX() + 0.5 - pivot.x, pos.getY() + 0.5 - pivot.y, pos.getZ() + 0.5 - pivot.z);
            Vec3 r = v.scale(cos).add(axis.cross(v).scale(sin)).add(axis.scale(axis.dot(v) * (1 - cos)));
            probe.set(Math.floor(pivot.x + r.x), Math.floor(pivot.y + r.y), Math.floor(pivot.z + r.z));
            BlockState state = level.getBlockState(probe);
            if (!state.getCollisionShape(level, probe).isEmpty() && !state.is(BlockTags.LOGS) && !state.is(BlockTags.LEAVES)) {
                return true;
            }
        }
        return false;
    }
}
