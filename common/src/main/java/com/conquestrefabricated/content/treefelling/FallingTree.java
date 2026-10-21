package com.conquestrefabricated.content.treefelling;

import com.mojang.math.Transformation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Brightness;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A felled tree on its way down.
 *
 * <p>The blocks are already gone from the world; each visible one is stood in for by a block display at its
 * old position. The tree is turned as one rigid body about a hinge on the stump's edge on the side it falls
 * towards, slowly at first and then faster. Only the wood collides with the ground: if the trunk comes down
 * at an awkward angle (before it is nearly flat) it snaps at the point of contact and the part beyond
 * carries on falling, up to twice, and a trunk that reaches the flat while still above the ground drops the
 * rest of the way. Leaves have no collision and break when they are driven into the ground. When all has
 * come to rest the trunk lies there for a moment, then pops into its drops.</p>
 *
 * <p>The whole fall is worked out when the tree is made (see {@link #plan}) and then only played back, so a
 * tree that can't fall at all is known before the world is touched. The displays are only ever told where
 * they will be a few ticks from now, and the client does the in-between, so a short step is used to keep the
 * straight-line interpolation of each block's corner close to its true arc.</p>
 */
final class FallingTree {

    /** Ticks between updates to the displays, and how long each one takes to play. */
    private static final int STEP = 4;
    /** Ticks the trunk lies where it landed before it pops. */
    private static final int HOLD = 20;
    private static final int MAX_DISPLAYS = 3500;
    private static final double SEARCH_STEP = Math.toRadians(2);
    private static final double FLAT = Math.toRadians(90);
    /** A trunk that stops before this has hit something awkwardly and breaks. */
    private static final double SNAPS_BELOW = Math.toRadians(80);
    private static final int MAX_SPLIT_DEPTH = 2;
    private static final int MAX_DROP = 48;
    /** Real gravity in blocks per tick squared (9.8 m/s^2), for the tip. Minecraft's own entity gravity is far stronger. */
    private static final double GRAVITY = 9.8 / 400;
    /** The lean, in radians, the tree is treated as already having when cut, and the push the cut gives it per tick. */
    private static final double START_LEAN = Math.toRadians(2.5);
    private static final double START_PUSH = 0.0015;
    private static final int SUBSTEPS = 8;
    /** The longest a tree takes to go over, in ticks. */
    private static final int MAX_TIP_TICKS = 160;

    /**
     * One block of the tree: where it started, what to show and what it gives. Only {@code collides} blocks -
     * the trunk - hold the tree up; leaves and branches break off where they meet the ground.
     */
    record Piece(BlockPos pos, BlockState state, boolean wood, boolean collides, boolean shown, int blockLight, int skyLight, List<ItemStack> drops) {
    }

    /** A turn about {@code point} through {@code angle}, then a slide by {@code shift}. */
    private record Step(Vec3 point, double angle, Vec3 shift) {
    }

    /**
     * A piece of the tree that moves as one: the steps it has been through, in order. A part broken off another
     * moved with it in every stage up to {@code splitStage}.
     */
    private static final class Part {
        final List<Step> done = new ArrayList<>();
        final int parent;
        final int splitStage;

        Part(int parent, int splitStage) {
            this.parent = parent;
            this.splitStage = splitStage;
        }
    }

    /**
     * One movement of one part, played over {@code duration} ticks. {@code before} is what the part had already
     * done. {@code curve}, if any, is how far through the movement it is at each tick; otherwise it simply
     * gathers speed.
     */
    private record Stage(int part, List<Step> before, Step step, int duration, double @Nullable [] curve, @Nullable Vec3 impact) {
    }

    private final ServerLevel level;
    private final List<Piece> pieces;
    private final Vec3 axis;
    private final SoundEvent landSound;
    private final int[] partOf;
    private final List<Part> parts = new ArrayList<>();
    private final List<Stage> stages = new ArrayList<>();
    private final Display.BlockDisplay[] displays;
    private final boolean[] gone;
    private final Vec3 pivot;
    /** Where every display stands; see {@link #frame()}. */
    private Vec3 anchor = Vec3.ZERO;
    private BlockPos anchorBlock = BlockPos.ZERO;
    private float cullWidth;
    private float cullHeight;

    private int age;
    private int stageIndex;
    private int stageStart;
    private boolean finished;

    private FallingTree(ServerLevel level, List<Piece> pieces, Vec3 pivot, Vec3 fall, SoundEvent landSound) {
        this.level = level;
        this.pivot = pivot;
        this.pieces = pieces;
        // Turning (0,1,0) towards the fall direction.
        this.axis = new Vec3(fall.z, 0, -fall.x);
        this.landSound = landSound;
        this.partOf = new int[pieces.size()];
        this.displays = new Display.BlockDisplay[pieces.size()];
        this.gone = new boolean[pieces.size()];
    }

    /**
     * Works out how the tree falls.
     *
     * @param pivot        the hinge: the stump's edge on the side it falls towards
     * @param fall         unit horizontal direction the tree falls towards
     * @param height       how tall the falling tree is, which sets how long it takes to go over
     * @return the tree, or null if it can't fall (it is hard against something at once)
     */
    static @Nullable FallingTree plan(ServerLevel level, List<Piece> pieces, Vec3 pivot, Vec3 fall, int height, SoundEvent landSound) {
        FallingTree tree = new FallingTree(level, pieces, pivot, fall, landSound);
        tree.parts.add(new Part(-1, -1));
        if (!tree.planPart(0, 0, pivot, 0, height)) {
            return null;
        }
        tree.frame();
        return tree;
    }

    private boolean planPart(int partId, double startTotal, Vec3 point, int depth, int height) {
        Part part = parts.get(partId);
        double add = 0;
        int contact = -1;
        while (add + SEARCH_STEP <= FLAT - startTotal + 1.0E-9) {
            contact = firstBlocked(partId, part.done, new Step(point, add + SEARCH_STEP, Vec3.ZERO));
            if (contact >= 0) {
                break;
            }
            add += SEARCH_STEP;
        }
        if (partId == 0 && add < Math.toRadians(8)) {
            return false;
        }
        double total = startTotal + add;
        Step turn = new Step(point, add, Vec3.ZERO);
        Vec3 impact = contact >= 0 ? apply(turn, apply(part.done, centre(pieces.get(contact).pos()))) : null;

        // Where the trunk would break, if it does.
        boolean snaps = contact >= 0 && total < SNAPS_BELOW && depth < MAX_SPLIT_DEPTH;
        int breakY = snaps ? pieces.get(contact).pos().getY() + 1 : 0;
        boolean detaches = false;
        if (snaps) {
            for (int i = 0; i < pieces.size(); i++) {
                if (partOf[i] == partId && pieces.get(i).collides() && pieces.get(i).pos().getY() >= breakY) {
                    detaches = true;
                    break;
                }
            }
        }

        if (add >= SEARCH_STEP) {
            if (partId == 0) {
                addStage(partId, turn, tipCurve(add, height), impact);
            } else {
                addStage(partId, turn, 12 + (int) Math.round(Math.toDegrees(add) * 0.4), null, impact);
            }
        }

        if (contact < 0) {
            drop(partId);
            return true;
        }
        if (!detaches) {
            return true;
        }

        // The trunk snaps just above the block that struck: everything higher becomes a part of its own,
        // hinged on the break.
        double sx = 0;
        double sz = 0;
        int n = 0;
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            if (partOf[i] == partId && piece.collides() && piece.pos().getY() == breakY - 1) {
                sx += piece.pos().getX() + 0.5;
                sz += piece.pos().getZ() + 0.5;
                n++;
            }
        }
        BlockPos hit = pieces.get(contact).pos();
        Vec3 breakPoint = n > 0 ? new Vec3(sx / n, breakY, sz / n) : new Vec3(hit.getX() + 0.5, breakY, hit.getZ() + 0.5);
        Vec3 hinge = apply(part.done, breakPoint);

        Part upper = new Part(partId, stages.size() - 1);
        upper.done.addAll(part.done);
        parts.add(upper);
        int upperId = parts.size() - 1;
        for (int i = 0; i < pieces.size(); i++) {
            if (partOf[i] == partId && pieces.get(i).pos().getY() >= breakY) {
                partOf[i] = upperId;
            }
        }
        return planPart(upperId, total, hinge, depth + 1, height);
    }

    /** A part that has come to rest flat but still in the air falls the rest of the way. */
    private void drop(int partId) {
        Part part = parts.get(partId);
        int fall = 0;
        while (fall < MAX_DROP && firstBlocked(partId, part.done, new Step(Vec3.ZERO, 0, new Vec3(0, -(fall + 1), 0))) < 0) {
            fall++;
        }
        if (fall >= 1) {
            int duration = Math.max(STEP, (int) Math.round(5 * Math.sqrt(fall)));
            addStage(partId, new Step(Vec3.ZERO, 0, new Vec3(0, -fall, 0)), duration, null, null);
        }
    }

    private void addStage(int partId, Step step, double[] curve, @Nullable Vec3 impact) {
        addStage(partId, step, curve.length - 1, curve, impact);
    }

    private void addStage(int partId, Step step, int duration, double @Nullable [] curve, @Nullable Vec3 impact) {
        Part part = parts.get(partId);
        // Whole steps of display updates; a curve is stretched or squeezed to fit.
        int ticks = Math.max(STEP, (duration + STEP / 2) / STEP * STEP);
        stages.add(new Stage(partId, new ArrayList<>(part.done), step, ticks, curve == null ? null : resample(curve, ticks), impact));
        part.done.add(step);
    }

    /**
     * How a tree goes over: as a pole tipping about its foot, whose fall quickens with the sine of its lean. It
     * starts from a small lean with a small push, the swing of the cut, so for the first few degrees it barely
     * moves; past that, gravity has it and it comes down hard. Taller trees are slower to get going. Returns the
     * fraction of {@code target} reached at each tick.
     */
    private static double[] tipCurve(double target, int height) {
        double length = Math.max(4, height + 1);
        double pull = 1.5 * GRAVITY / length;
        double angle = 0;
        double speed = START_PUSH;
        List<Double> curve = new ArrayList<>();
        curve.add(0.0);
        while (angle < target && curve.size() <= MAX_TIP_TICKS) {
            for (int i = 0; i < SUBSTEPS; i++) {
                speed += pull * Math.sin(START_LEAN + angle) / SUBSTEPS;
                angle += speed / SUBSTEPS;
            }
            curve.add(Math.min(1.0, angle / target));
        }
        curve.set(curve.size() - 1, 1.0);
        double[] fractions = new double[curve.size()];
        for (int i = 0; i < fractions.length; i++) {
            fractions[i] = curve.get(i);
        }
        // A very tall tree would take too long to get going; play it faster rather than make you wait.
        return fractions.length - 1 > MAX_TIP_TICKS ? resample(fractions, MAX_TIP_TICKS) : fractions;
    }

    /** {@code curve} spread over {@code ticks} ticks (so {@code ticks + 1} samples). */
    private static double[] resample(double[] curve, int ticks) {
        double[] out = new double[ticks + 1];
        for (int t = 0; t <= ticks; t++) {
            out[t] = sample(curve, (double) t / ticks);
        }
        return out;
    }

    private static double sample(double[] curve, double u) {
        double x = Math.max(0, Math.min(1, u)) * (curve.length - 1);
        int i = Math.min((int) x, curve.length - 2);
        return curve[i] + (curve[i + 1] - curve[i]) * (x - i);
    }

    /** The first wood block of the part that, after {@code step}, would be inside solid ground; -1 if none. */
    private int firstBlocked(int partId, List<Step> before, Step step) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int i = 0; i < pieces.size(); i++) {
            Piece piece = pieces.get(i);
            if (partOf[i] != partId || !piece.collides()) {
                continue;
            }
            Vec3 at = apply(step, apply(before, centre(piece.pos())));
            probe.set(Math.floor(at.x), Math.floor(at.y), Math.floor(at.z));
            if (solidGround(probe)) {
                return i;
            }
        }
        return -1;
    }

    /** Anything with a body that isn't another tree: trees are passed through. */
    private boolean solidGround(BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return !state.getCollisionShape(level, pos).isEmpty() && !state.is(BlockTags.LOGS) && !state.is(BlockTags.LEAVES)
                && !TreeScan.isMoss(state);
    }

    private static Vec3 centre(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    private Vec3 rotate(Vec3 v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return v.scale(cos).add(axis.cross(v).scale(sin)).add(axis.scale(axis.dot(v) * (1 - cos)));
    }

    private Vec3 apply(Step step, Vec3 x) {
        Vec3 moved = step.angle() == 0 ? x : step.point().add(rotate(x.subtract(step.point()), step.angle()));
        return moved.add(step.shift());
    }

    private Vec3 apply(List<Step> steps, Vec3 x) {
        for (Step step : steps) {
            x = apply(step, x);
        }
        return x;
    }

    /**
     * Picks one spot for every display to stand on, low under the hinge, and a culling box that holds the
     * whole fall. A display is culled by a box rising from where it stands; standing each at its own block, the
     * canopy's boxes were up in the empty air where the tree had been, so when you looked down at the tree
     * coming down they went out of view, stopped updating, and jumped back when they came into view again.
     */
    private void frame() {
        double reach = 0;
        double lowest = pivot.y;
        for (int i = 0; i < pieces.size(); i++) {
            Vec3 start = centre(pieces.get(i).pos());
            Vec3 end = apply(parts.get(partOf[i]).done, start);
            reach = Math.max(reach, Math.max(start.distanceTo(pivot), end.distanceTo(pivot)));
            lowest = Math.min(lowest, end.y);
        }
        reach += 2;
        anchor = new Vec3(pivot.x, Math.floor(lowest) - 3, pivot.z);
        anchorBlock = BlockPos.containing(anchor);
        cullWidth = (float) (reach * 2);
        cullHeight = (float) (pivot.y + reach - anchor.y);
    }

    /**
     * Tells the client where the block stood before it fell, for its tint (see the client mixin on the block
     * display renderer). The glow colour is unused while the display isn't glowing, so it carries a marker in
     * its top byte and the offset from the anchor in the other three; -1 is vanilla's default.
     */
    private int marker(BlockPos pos) {
        int dx = pos.getX() - anchorBlock.getX();
        int dy = pos.getY() - anchorBlock.getY();
        int dz = pos.getZ() - anchorBlock.getZ();
        if (Math.abs(dx) > 127 || Math.abs(dy) > 127 || Math.abs(dz) > 127) {
            return TreeFelling.DISPLAY_MARKER;
        }
        return TreeFelling.DISPLAY_MARKER | (dx & 0xFF) << 16 | (dy & 0xFF) << 8 | (dz & 0xFF);
    }

    // ---- playback ----

    boolean finished() {
        return finished;
    }

    ServerLevel level() {
        return level;
    }

    /** Adds the displays to the world; {@code owned} is told about each first, so stale ones can be told apart. */
    void spawn(Consumer<Display.BlockDisplay> owned) {
        int count = 0;
        for (int i = 0; i < pieces.size() && count < MAX_DISPLAYS; i++) {
            Piece piece = pieces.get(i);
            if (!piece.shown()) {
                continue;
            }
            count++;
            Display.BlockDisplay display = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level);
            display.setPos(anchor.x, anchor.y, anchor.z);
            display.setBlockState(piece.state());
            Vec3 offset = new Vec3(piece.pos().getX(), piece.pos().getY(), piece.pos().getZ()).subtract(anchor);
            display.setTransformation(new Transformation(new Vector3f((float) offset.x, (float) offset.y, (float) offset.z), new Quaternionf(), new Vector3f(1, 1, 1), new Quaternionf()));
            display.setWidth(cullWidth);
            display.setHeight(cullHeight);
            display.setGlowColorOverride(marker(piece.pos()));
            // The light the block had in the tree, not what the empty air where it started has now.
            display.setBrightnessOverride(new Brightness(piece.blockLight(), piece.skyLight()));
            display.setNoGravity(true);
            display.setInvulnerable(true);
            display.addTag(TreeFelling.TAG);
            owned.accept(display);
            level.addFreshEntity(display);
            displays[i] = display;
        }
    }

    void tick() {
        if (finished) {
            return;
        }
        age++;
        // The first tick after spawning is left alone so the clients get the displays at rest first.
        if (age < 2) {
            return;
        }
        while (stageIndex < stages.size()) {
            Stage stage = stages.get(stageIndex);
            int t = age - 2 - stageStart;
            if (t >= stage.duration()) {
                end(stage);
                stageIndex++;
                stageStart = age - 2;
                continue;
            }
            if (t % STEP == 0) {
                pose(stage, Math.min(1.0, (double) (t + STEP) / stage.duration()));
            }
            return;
        }
        if (age - 2 - stageStart >= HOLD) {
            pop();
        }
    }

    /** Ends the fall now, with the tree lying where it was heading. */
    void finishNow() {
        if (!finished) {
            pop();
        }
    }

    private static double ease(Stage stage, double u) {
        // A tree tips as worked out in tipCurve; a piece that has broken off, or drops, just speeds up.
        return stage.curve() != null ? sample(stage.curve(), u) : u * u;
    }

    /**
     * Whether pieces of {@code part} move in {@code stage}: its own stages, and those of the parts it was broken
     * off, from before it broke away. (The split is planned before any of it plays, so a piece already belongs to
     * the part it will end up in while it is still riding along with the rest of the tree.)
     */
    private boolean moves(int part, Stage stage) {
        // Only ever asked about the stage being played.
        int index = stageIndex;
        for (int p = part; p >= 0; p = parts.get(p).parent) {
            if (stage.part() == p) {
                return true;
            }
            if (index > parts.get(p).splitStage) {
                return false;
            }
        }
        return false;
    }

    private void pose(Stage stage, double u) {
        double e = ease(stage, u);
        Step now = new Step(stage.step().point(), stage.step().angle() * e, stage.step().shift().scale(e));
        double turned = now.angle();
        for (Step before : stage.before()) {
            turned += before.angle();
        }
        Quaternionf rotation = new Quaternionf().fromAxisAngleRad((float) axis.x, (float) axis.y, (float) axis.z, (float) turned);
        for (int i = 0; i < pieces.size(); i++) {
            Display.BlockDisplay display = displays[i];
            if (display == null || gone[i] || !moves(partOf[i], stage)) {
                continue;
            }
            if (display.isRemoved()) {
                continue;
            }
            Piece piece = pieces.get(i);
            Vec3 corner = new Vec3(piece.pos().getX(), piece.pos().getY(), piece.pos().getZ());
            Vec3 moved = apply(now, apply(stage.before(), corner)).subtract(anchor);
            display.setTransformationInterpolationDuration(STEP);
            display.setTransformationInterpolationDelay(0);
            display.setTransformation(new Transformation(new Vector3f((float) moved.x, (float) moved.y, (float) moved.z), rotation, new Vector3f(1, 1, 1), new Quaternionf()));

            // Leaves and branches don't hold anything up: one driven into the ground breaks off.
            if (!piece.collides()) {
                Vec3 at = apply(now, apply(stage.before(), centre(piece.pos())));
                if (solidGround(BlockPos.containing(at))) {
                    breakOff(i, at);
                }
            }
        }
    }

    private void breakOff(int i, Vec3 at) {
        gone[i] = true;
        Display.BlockDisplay display = displays[i];
        if (display != null) {
            display.discard();
        }
        Piece piece = pieces.get(i);
        level.levelEvent(2001, BlockPos.containing(at), Block.getId(piece.state()));
        for (ItemStack stack : piece.drops()) {
            spawnItem(stack.copy(), at);
        }
    }

    /** A part has come to rest: a crack where it struck. */
    private void end(Stage stage) {
        if (stage.impact() == null) {
            return;
        }
        Vec3 at = stage.impact();
        level.playSound(null, at.x, at.y, at.z, landSound, SoundSource.BLOCKS, 2.5F, 0.55F);
        for (int i = 0; i < pieces.size(); i++) {
            if (partOf[i] == stage.part() && pieces.get(i).wood()) {
                level.levelEvent(2001, BlockPos.containing(at), Block.getId(pieces.get(i).state()));
                break;
            }
        }
    }

    private void spawnItem(ItemStack stack, Vec3 at) {
        var random = level.getRandom();
        ItemEntity item = new ItemEntity(level, at.x, at.y + 0.25, at.z, stack);
        item.setDeltaMovement(random.nextGaussian() * 0.04, 0.1, random.nextGaussian() * 0.04);
        level.addFreshEntity(item);
    }

    /** The trunk has lain long enough: it breaks apart into what it yields. */
    private void pop() {
        finished = true;
        var random = level.getRandom();
        List<Vec3> spots = new ArrayList<>();
        List<Integer> shownIndexes = new ArrayList<>();
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < pieces.size(); i++) {
            if (gone[i]) {
                continue;
            }
            Piece piece = pieces.get(i);
            for (ItemStack stack : piece.drops()) {
                TreeFelling.merge(drops, stack);
            }
            if (displays[i] != null) {
                displays[i].discard();
                shownIndexes.add(i);
                spots.add(apply(parts.get(partOf[i]).done, centre(piece.pos())));
            }
        }
        if (spots.isEmpty()) {
            return;
        }

        int effects = Math.min(30, spots.size());
        for (int i = 0; i < effects; i++) {
            int k = random.nextInt(spots.size());
            level.levelEvent(2001, BlockPos.containing(spots.get(k)), Block.getId(pieces.get(shownIndexes.get(k)).state()));
        }
        Vec3 tip = spots.get(random.nextInt(spots.size()));
        level.playSound(null, tip.x, tip.y, tip.z, landSound, SoundSource.BLOCKS, 1.5F, 0.9F);
        for (ItemStack stack : drops) {
            spawnItem(stack, spots.get(random.nextInt(spots.size())));
        }
    }
}
