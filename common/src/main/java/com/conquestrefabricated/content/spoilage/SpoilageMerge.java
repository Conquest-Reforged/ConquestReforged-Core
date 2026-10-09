package com.conquestrefabricated.content.spoilage;

import net.minecraft.world.item.ItemStack;

/**
 * Stacks of one food that differ only in freshness do stack - and the stack they make is the average.
 *
 * <p>Vanilla decides whether two stacks merge with {@code ItemStack.isSameItemSameComponents} and then
 * grows one by the other's count, through {@code setCount}, from a dozen places: clicking in a menu,
 * shift-clicking, hoppers, picking up, items on the ground. Rather than patch each, three small mixins
 * on {@code ItemStack} meet at this class:</p>
 * <ol>
 *   <li>when that comparison says "different" only because of freshness, {@link #relax} says "same" and
 *       remembers the pair;</li>
 *   <li>the next time one of the pair is made larger - {@link #growing}, or {@link #copied} where vanilla
 *       grows a copy - it takes the average of the two stages first.</li>
 * </ol>
 *
 * <p>The blend depends on which way the merge goes. Adding <em>staler</em> food to fresher takes the plain
 * average of the two stages, each stack counting once whatever its size, so one nearly rotten apple halves
 * the freshness of a full stack. Adding <em>fresher</em> food to staler takes the average weighted by how
 * many items there are on each side, so nothing is gained: a fresh apple cannot lift a stack of rotten ones
 * (moving them in one at a time, say by hopper, would otherwise lift it a little at a time and launder the
 * lot). Either way the merged food is never fresher than the same food kept apart. The pair is remembered per thread and dropped as soon as it is used, so a comparison that was
 * only a question - "is there room?" - does nothing.</p>
 */
public final class SpoilageMerge {

    /** Two stacks judged to be merging, and how fresh each was at the time - vanilla may empty one before it grows the other. */
    private record Pair(ItemStack a, int stageA, ItemStack b, int stageB) {
        /** The stage of the other stack in the pair, or -1 if {@code stack} is neither. */
        int partnerStage(ItemStack stack) {
            return a == stack ? stageB : b == stack ? stageA : -1;
        }

        int stageOf(ItemStack stack) {
            return a == stack ? stageA : stageB;
        }
    }

    private static final ThreadLocal<Pair> PAIR = new ThreadLocal<>();

    private SpoilageMerge() {
    }

    /** True if {@code a} and {@code b} differ only in how fresh they are. Remembers them as the stacks about to merge. */
    public static boolean relax(ItemStack a, ItemStack b) {
        if (!Spoilage.enabled() || a.isEmpty() || b.isEmpty() || !ItemStack.isSameItem(a, b)
                || Spoilage.classOf(a) == null || Spoilage.stage(a) == Spoilage.stage(b)) {
            return false;
        }
        ItemStack bare = a.copy();
        bare.remove(Spoilage.FRESHNESS);
        ItemStack other = b.copy();
        other.remove(Spoilage.FRESHNESS);
        if (!ItemStack.isSameItemSameComponents(bare, other)) {
            return false;
        }
        PAIR.set(new Pair(a, Spoilage.stage(a), b, Spoilage.stage(b)));
        return true;
    }

    /** {@code target} is about to be given {@code count} items. If it is half of the remembered pair, blend. */
    public static void growing(ItemStack target, int count) {
        Pair pair = PAIR.get();
        if (pair == null || count <= target.getCount()) {
            return;
        }
        int incoming = pair.partnerStage(target);
        if (incoming >= 0) {
            Spoilage.setStage(target, blend(pair.stageOf(target), target.getCount(), incoming, count - target.getCount()));
            PAIR.remove();
        }
    }

    /** Vanilla made a larger copy of {@code source}, to stand in for the merged stack. */
    public static void copied(ItemStack source, ItemStack copy) {
        Pair pair = PAIR.get();
        if (pair == null || copy.getCount() <= source.getCount()) {
            return;
        }
        int incoming = pair.partnerStage(source);
        if (incoming >= 0) {
            Spoilage.setStage(copy, blend(pair.stageOf(source), source.getCount(), incoming, copy.getCount() - source.getCount()));
            PAIR.remove();
        }
    }

    /** Forgets any pair that was never used. */
    public static void forget() {
        PAIR.remove();
    }

    /** The stage of {@code held} items at stage {@code have} after {@code added} items at stage {@code adding} join them. */
    private static int blend(int have, int held, int adding, int added) {
        if (adding > have) {
            return Math.round((have + adding) / 2.0F);
        }
        return Math.round((have * (float) held + adding * (float) added) / (held + added));
    }
}
