package com.conquestrefabricated.core.asset.lang;

import com.conquestrefabricated.core.block.builder.BlockName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Translation keys for the lore lines a block carries.
 * <p>
 * Lore belongs to the whole family a {@code register(TypeList)} call produces, not to each shape
 * in it, so the key is built from the family's {@link BlockName} rather than from a block's
 * registry name. One {@code .lore(..)} call therefore yields one lang entry, however many shapes
 * the family expands to.
 * <p>
 * A single line uses the bare key {@code lore.<namespace>.<name>}; several lines are indexed
 * {@code lore.<namespace>.<name>.0}, {@code .1} and so on.
 */
public final class Lore {

    public static final String PREFIX = "lore";

    private Lore() {
    }

    /**
     * @return the key for line {@code index} of a lore block that is {@code lines} lines long
     */
    public static String key(BlockName name, int index, int lines) {
        String base = PREFIX + '.' + name.getNamespace() + '.' + name.getSingular();
        return lines == 1 ? base : base + '.' + index;
    }

    /**
     * @return every key for a lore block of {@code lines} lines, in order
     */
    public static List<String> keys(BlockName name, int lines) {
        if (lines <= 0) {
            return Collections.emptyList();
        }
        List<String> keys = new ArrayList<>(lines);
        for (int i = 0; i < lines; i++) {
            keys.add(key(name, i, lines));
        }
        return keys;
    }
}
