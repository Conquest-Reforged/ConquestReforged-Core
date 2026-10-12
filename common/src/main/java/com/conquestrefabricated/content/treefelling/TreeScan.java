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
 */
final class TreeScan {

    static final int MAX_WOOD = 1500;
    static final int MAX_FOLIAGE = 8000;
    /** Leaf steps explored from the wood; leaf decay itself gives up after 6. */
    private static final int EXPLORE_DEPTH = 14;
    /** Leaves further than this from the felled wood are never taken. */
    private static final int OWN_DEPTH = 12;
    /** A tree needs this many natural leaves; a log pile or a hand-built pillar has none. */
    private static final int MIN_LEAVES = 3;

    private static final byte LEAF = 1;
    private static final byte SUPPORT = 2;

    private static final Map<Block, Boolean> NOT_TREE = new ConcurrentHashMap<>();

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

    static @Nullable Result scan(BlockGetter level, BlockPos cut) {
        if (!isWood(level.getBlockState(cut))) {
            return null;
        }
        // Only a cut at the foot of the trunk fells the tree; higher up it is an ordinary break.
        BlockPos.MutableBlockPos probe = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                probe.set(cut.getX() + dx, cut.getY() - 1, cut.getZ() + dz);
                if (isWood(level.getBlockState(probe))) {
                    return null;
                }
            }
        }

        // Wood, through all 26 neighbours.
        LongOpenHashSet woodSet = new LongOpenHashSet();
        LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
        woodSet.add(cut.asLong());
        queue.enqueue(cut.asLong());
        while (!queue.isEmpty()) {
            BlockPos p = BlockPos.of(queue.dequeueLong());
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        probe.set(p.getX() + dx, p.getY() + dy, p.getZ() + dz);
                        if (probe.getY() < cut.getY() || woodSet.contains(probe.asLong()) || !isWood(level.getBlockState(probe))) {
                            continue;
                        }
                        if (woodSet.size() >= MAX_WOOD) {
                            return null;
                        }
                        woodSet.add(probe.asLong());
                        queue.enqueue(probe.asLong());
                    }
                }
            }
        }

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
                    return null;
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
