package com.conquestrefabricated.content.treefelling;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Finds the tree a cut belongs to.
 *
 * <p>Wood is everything in {@code #minecraft:logs} that is actually part of a tree - that tag also holds
 * the player-built beams and firewood stacks, which are skipped by name - joined through all 26
 * neighbours, because branches meet corner to corner. Foliage is leaves (and nether wart blocks) reached
 * through their six faces, the way leaf decay measures distance.</p>
 *
 * <p>Canopies of neighbouring trees touch, so foliage is shared out the way decay would shorten the
 * distances: a leaf goes with the felled tree when that tree's wood is at least as near, in leaf steps,
 * as any other wood. Arborea's invisible {@code leaf_support} blocks (logs by tag, but not wood) are carried
 * along so they don't hold up a canopy that has lost its tree.</p>
 *
 * <p>Sphagnum moss shapes clad some trunks, so they count as wood too - but moss also carpets the ground,
 * so only moss above the cut, and no more than {@link #MAX_MOSS_RUN} blocks from real wood, is taken. That
 * keeps a mossy trunk whole without the search running off across a bog or into the next tree.</p>
 */
final class TreeScan {

    static final int MAX_WOOD = 1500;
    static final int MAX_FOLIAGE = 20000;
    /** Wood looked at around the cut to tell this tree from its neighbours, and how far out (in blocks). */
    private static final int MAX_SEARCH = 12000;
    private static final int SEARCH_RADIUS = 28;
    /** Blocks within this distance of the cut's column are the cut trunk itself (a wide foot, a stump ring). */
    private static final int TRUNK_RADIUS = 3;
    /** Leaf steps explored from the wood; leaf decay itself gives up after 6. */
    private static final int EXPLORE_DEPTH = 14;
    /** Leaves further than this from the felled wood are never taken. */
    private static final int OWN_DEPTH = 12;
    /** A tree needs this many natural leaves; a log pile or a hand-built pillar has none. */
    private static final int MIN_LEAVES = 3;
    /** The most moss blocks in a row, from real wood, that still count as trunk. */
    private static final int MAX_MOSS_RUN = 3;

    private static final byte LEAF = 1;
    private static final byte SUPPORT = 2;

    private static final Map<Block, Boolean> NOT_TREE = new ConcurrentHashMap<>();
    private static final Map<Block, Boolean> BRANCH = new ConcurrentHashMap<>();
    private static final Map<Block, Boolean> MOSS = new ConcurrentHashMap<>();

    /** Positions to remove; all lists are in no particular order. {@code wood} includes the cut. */
    record Result(List<BlockPos> wood, List<BlockPos> leaves, List<BlockPos> supports) {
    }

    private TreeScan() {
    }

    static boolean isWood(BlockState state) {
        if (!state.is(BlockTags.LOGS) || state.hasBlockEntity() || isSupport(state)) {
            return false;
        }
        return !NOT_TREE.computeIfAbsent(state.getBlock(), block -> {
            String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
            return path.contains("beam") || path.contains("firewood") || path.contains("young_tree");
        });
    }

    /** Wood that is a branch rather than trunk: it snaps off against the ground instead of holding a tree up. */
    static boolean isBranch(BlockState state) {
        return BRANCH.computeIfAbsent(state.getBlock(), block -> BuiltInRegistries.BLOCK.getKey(block).getPath().contains("branch"));
    }

    /** Sphagnum moss in any shape: trunk cladding when it is on a tree, ground cover when it isn't. */
    static boolean isMoss(BlockState state) {
        return !state.hasBlockEntity() && MOSS.computeIfAbsent(state.getBlock(), block -> BuiltInRegistries.BLOCK.getKey(block).getPath().contains("sphagnum_moss_block"));
    }

    /** An invisible log stand-in inside a canopy. */
    static boolean isSupport(BlockState state) {
        return state.is(BlockTags.LOGS) && state.getRenderShape() == RenderShape.INVISIBLE;
    }

    static boolean isLeaf(BlockState state) {
        return (state.is(BlockTags.LEAVES) || state.is(BlockTags.WART_BLOCKS)) && !state.hasBlockEntity();
    }

    /** Leaves a player placed carry {@code persistent}; generated ones don't. */
    static boolean isNatural(BlockState state) {
        return !state.hasProperty(BlockStateProperties.PERSISTENT) || !state.getValue(BlockStateProperties.PERSISTENT);
    }

    /** What would keep a leaf from decaying, other than a support. */
    private static boolean isRealLog(BlockState state) {
        return isWood(state) || (state.is(BlockTags.PREVENTS_NEARBY_LEAF_DECAY) && !isSupport(state));
    }

    private static byte kind(BlockState state) {
        if (isLeaf(state)) {
            return LEAF;
        }
        return isSupport(state) ? SUPPORT : 0;
    }

    /**
     * The wood of the tree being cut, the cut included; null if there is no tree to fell here.
     *
     * <p>In a forest, branches of neighbouring trees touch, so the wood joined to the cut can be several
     * trees. All of it near the cut is gathered first, then shared out: every tree's foot (wood standing on
     * the ground, away from the cut's own trunk) and all wood below the cut (the stump, and trees standing
     * lower) claims what is nearest it along the wood, and the felled tree is what the cut is nearest.</p>
     *
     * <p>The cut must sever its trunk: if the trunk itself, around the cut, still runs down past it, it is a
     * branch or a trunk only partly chopped through, and nothing falls.</p>
     */
    private static @Nullable LongOpenHashSet ownWood(BlockGetter level, BlockPos cut) {
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos under = new BlockPos.MutableBlockPos();
        LongOpenHashSet seen = new LongOpenHashSet();
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
        // How many moss blocks in a row each moss block is from real wood.
        Long2IntOpenHashMap mossRun = new Long2IntOpenHashMap();
        // Where the sharing out starts from, besides the cut.
        List<Long> feet = new ArrayList<>();
        seen.add(cut.asLong());
        queue.enqueue(cut.asLong());
        while (!queue.isEmpty()) {
            long key = queue.dequeueLong();
            BlockPos p = BlockPos.of(key);
            int run = mossRun.get(key);
            boolean inTrunk = inTrunk(cut, p);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        probe.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                        long nk = probe.asLong();
                        if (seen.contains(nk) || Math.abs(probe.getX() - cut.getX()) > SEARCH_RADIUS
                                || Math.abs(probe.getZ() - cut.getZ()) > SEARCH_RADIUS) {
                            continue;
                        }
                        BlockState state = level.getBlockState(probe);
                        if (!isWood(state)) {
                            if (isMoss(state) && probe.getY() > cut.getY() && run < MAX_MOSS_RUN) {
                                seen.add(nk);
                                mossRun.put(nk, run + 1);
                                queue.enqueue(nk);
                            }
                            continue;
                        }
                        if (probe.getY() < cut.getY()) {
                            if (p.equals(cut)) {
                                continue; // the stump: it stays standing
                            }
                            if (inTrunk) {
                                return null; // the trunk still stands on what is below the cut
                            }
                            // Another tree standing lower, or a branch of the stump: it keeps what is nearest it.
                            seen.add(nk);
                            feet.add(nk);
                            continue;
                        }
                        if (seen.size() >= MAX_SEARCH) {
                            continue;
                        }
                        seen.add(nk);
                        queue.enqueue(nk);
                        if (!inTrunk(cut, probe)) {
                            under.set(probe.getX(), probe.getY() - 1, probe.getZ());
                            if (isGround(level.getBlockState(under), level, under)) {
                                feet.add(nk);
                            }
                        }
                    }
                }
            }
        }

        // Share it out a step at a time from every start at once; the cut steps first, so a tie stays with this tree.
        LongOpenHashSet own = new LongOpenHashSet();
        LongOpenHashSet claimed = new LongOpenHashSet();
        LongArrayFIFOQueue ownFront = new LongArrayFIFOQueue();
        LongArrayFIFOQueue otherFront = new LongArrayFIFOQueue();
        own.add(cut.asLong());
        claimed.add(cut.asLong());
        ownFront.enqueue(cut.asLong());
        for (long foot : feet) {
            if (claimed.add(foot)) {
                otherFront.enqueue(foot);
            }
        }
        while (!ownFront.isEmpty() || !otherFront.isEmpty()) {
            ownFront = spread(ownFront, seen, claimed, own, probe);
            otherFront = spread(otherFront, seen, claimed, null, probe);
            if (own.size() > MAX_WOOD) {
                return null;
            }
        }
        return own;
    }

    /** One step of the sharing out: everything gathered next to {@code from} that nobody has yet. */
    private static LongArrayFIFOQueue spread(LongArrayFIFOQueue from, LongOpenHashSet seen, LongOpenHashSet claimed,
                                             @Nullable LongOpenHashSet into, BlockPos.MutableBlockPos probe) {
        LongArrayFIFOQueue next = new LongArrayFIFOQueue();
        while (!from.isEmpty()) {
            BlockPos p = BlockPos.of(from.dequeueLong());
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        probe.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                        long nk = probe.asLong();
                        if (seen.contains(nk) && claimed.add(nk)) {
                            if (into != null) {
                                into.add(nk);
                            }
                            next.enqueue(nk);
                        }
                    }
                }
            }
        }
        return next;
    }

    /** Within the trunk being cut: its own wide foot or stump ring is not another tree standing beside it. */
    private static boolean inTrunk(BlockPos cut, BlockPos p) {
        int dx = p.getX() - cut.getX();
        int dz = p.getZ() - cut.getZ();
        return dx * dx + dz * dz <= TRUNK_RADIUS * TRUNK_RADIUS;
    }

    /** What a tree can stand on: solid, and not more tree. */
    private static boolean isGround(BlockState state, BlockGetter level, BlockPos pos) {
        return !state.isAir() && !isWood(state) && !isMoss(state) && !isLeaf(state) && !isSupport(state)
                && !state.getCollisionShape(level, pos).isEmpty();
    }

    static @Nullable Result scan(BlockGetter level, BlockPos cut) {
        if (!isWood(level.getBlockState(cut))) {
            return null;
        }
        LongOpenHashSet woodSet = ownWood(level, cut);
        if (woodSet == null) {
            return null;
        }
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();

        // Foliage: leaf steps from the wood. A support passes the count on to leaves but not to other supports.
        Long2IntOpenHashMap depth = new Long2IntOpenHashMap();
        depth.defaultReturnValue(-1);
        Long2ByteOpenHashMap kinds = new Long2ByteOpenHashMap();
        List<BlockPos> nodes = new ArrayList<>();
        for (long w : woodSet) {
            depth.put(w, 0);
            queue.enqueue(w);
        }
        while (!queue.isEmpty()) {
            long key = queue.dequeueLong();
            BlockPos p = BlockPos.of(key);
            int d = depth.get(key);
            if (d >= EXPLORE_DEPTH) {
                continue;
            }
            byte pk = kinds.get(key);
            for (Direction dir : Direction.values()) {
                probe.setWithOffset(p, dir);
                long nk = probe.asLong();
                if (depth.containsKey(nk)) {
                    continue;
                }
                byte k = kind(level.getBlockState(probe));
                if (k == 0 || (k == SUPPORT && pk == SUPPORT)) {
                    continue;
                }
                if (nodes.size() >= MAX_FOLIAGE) {
                    // A forest's canopy runs on and on; what is this far out isn't this tree's anyway.
                    queue.clear();
                    break;
                }
                depth.put(nk, d + 1);
                kinds.put(nk, k);
                nodes.add(probe.immutable());
                queue.enqueue(nk);
            }
        }

        // Other wood next to the foliage, and how many leaf steps it is from each node.
        Long2IntOpenHashMap other = new Long2IntOpenHashMap();
        other.defaultReturnValue(-1);
        for (BlockPos p : nodes) {
            for (Direction dir : Direction.values()) {
                probe.setWithOffset(p, dir);
                if (!depth.containsKey(probe.asLong()) && isRealLog(level.getBlockState(probe))) {
                    other.put(p.asLong(), 1);
                    queue.enqueue(p.asLong());
                    break;
                }
            }
        }
        while (!queue.isEmpty()) {
            long key = queue.dequeueLong();
            BlockPos p = BlockPos.of(key);
            int d = other.get(key);
            if (d >= EXPLORE_DEPTH) {
                continue;
            }
            byte pk = kinds.get(key);
            for (Direction dir : Direction.values()) {
                probe.setWithOffset(p, dir);
                long nk = probe.asLong();
                if (other.containsKey(nk) || !kinds.containsKey(nk) || (pk == SUPPORT && kinds.get(nk) == SUPPORT)) {
                    continue;
                }
                other.put(nk, d + 1);
                queue.enqueue(nk);
            }
        }

        List<BlockPos> leaves = new ArrayList<>();
        List<BlockPos> supports = new ArrayList<>();
        LongOpenHashSet falling = new LongOpenHashSet(woodSet);
        int natural = 0;
        for (BlockPos p : nodes) {
            long key = p.asLong();
            int d = depth.get(key);
            int o = other.get(key);
            if (d > OWN_DEPTH || (o >= 0 && o < d)) {
                continue;
            }
            falling.add(key);
            if (kinds.get(key) == LEAF) {
                leaves.add(p);
                if (isNatural(level.getBlockState(p))) {
                    natural++;
                }
            } else {
                supports.add(p);
            }
        }
        if (natural < MIN_LEAVES) {
            return null;
        }

        // A support still touching a leaf or log that stays is still holding something up.
        supports.removeIf(p -> {
            for (Direction dir : Direction.values()) {
                BlockPos n = p.relative(dir);
                if (falling.contains(n.asLong())) {
                    continue;
                }
                BlockState s = level.getBlockState(n);
                if (isLeaf(s) || isRealLog(s)) {
                    return true;
                }
            }
            return false;
        });

        List<BlockPos> wood = new ArrayList<>(woodSet.size());
        for (long w : woodSet) {
            wood.add(BlockPos.of(w));
        }
        return new Result(wood, leaves, supports);
    }
}
