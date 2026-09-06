package com.conquestrefabricated.core.asset.lang;

import com.conquestrefabricated.core.block.builder.BlockName;
import com.conquestrefabricated.core.client.TooltipKeys;
import com.conquestrefabricated.core.util.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * The lore lines a block family carries, and the translation keys they live under.
 * <p>
 * Lore belongs to a <b>family</b>, not to a block or even to a single {@code register(TypeList)}
 * call. A family that has to be registered in several passes — a pediment whose raking, summit and
 * vertical slabs each need their own type list — declares its lore once and every member picks it
 * up, so the lang file gets one entry no matter how the family is split up:
 * <pre>{@code
 * VanillaProps.stone()
 *         .name("marble_pediment_raking")
 *         .lore("The raking crowns a classical temple.")
 *         .register(TypeList.of(HingeToggle3.class));
 *
 * VanillaProps.stone()
 *         .name("marble_pediment_raking_summit")
 *         .family("marble_pediment_raking")   // inherits the lore above
 *         .register(TypeList.of(HalfToggle4.class));
 * }</pre>
 * <p>
 * A single line uses the bare key {@code lore.<namespace>.<family>}; several lines are indexed
 * {@code lore.<namespace>.<family>.0}, {@code .1} and so on.
 * <p>
 * Lore is collapsed by default: the tooltip shows a one-line hint until the player holds the
 * expand key. See {@link #append}.
 */
public final class Lore {

    public static final String PREFIX = "lore";

    /** Lang key for the "hold SHIFT" hint shown while lore is collapsed. */
    public static final String HINT_KEY = "tooltip.conquest.hold_shift";

    /** Shown when {@link #HINT_KEY} is missing from the loaded language. */
    public static final String HINT_FALLBACK = "Hold SHIFT to reveal description...";

    /** Colour of the hint line. Swap for {@code BLUE} / {@code AQUA} to taste. */
    public static final ChatFormatting HINT_COLOR = ChatFormatting.GREEN;

    /**
     * Family id to lore lines. Insertion-ordered so the generated lang file comes out in
     * registration order rather than hash order.
     * <p>
     * Deliberately outlives {@code BlockDataRegistry}, which is disposed after load: tooltips are
     * built long afterwards and still need these lines.
     */
    private static final Map<Identifier, List<String>> LINES = new LinkedHashMap<>();

    private Lore() {
    }

    /**
     * Works out which family's lore a block shares.
     *
     * @param family the builder's {@code family(..)} target, if it declared one
     * @param name   the block's own name, used when it declares no family
     * @return the id the block's lore is stored and keyed under
     */
    public static Identifier familyId(Optional<Identifier> family, BlockName name) {
        return family.orElseGet(() -> Identifier.fromNamespaceAndPath(name.getNamespace(), name.getSingular()));
    }

    /**
     * Records a family's lore. The first non-empty declaration for an id wins; a second one with
     * different text means two families collided on an id, which is worth saying out loud.
     */
    public static void declare(Identifier familyId, List<String> lines) {
        if (lines.isEmpty()) {
            return;
        }

        List<String> existing = LINES.putIfAbsent(familyId, List.copyOf(lines));
        if (existing != null && !existing.equals(lines)) {
            Log.warn("Lore for {} was already declared as {}; ignoring {}", familyId, existing, lines);
        }
    }

    /**
     * @return the family's lore lines, empty if it has none
     */
    public static List<String> lines(Identifier familyId) {
        return LINES.getOrDefault(familyId, Collections.emptyList());
    }

    /**
     * @return every declared family's lore, in registration order — for the lang datagen
     */
    public static Map<Identifier, List<String>> entries() {
        return Collections.unmodifiableMap(LINES);
    }

    /**
     * @return the key for line {@code index} of a lore block that is {@code lines} lines long
     */
    public static String key(Identifier familyId, int index, int lines) {
        String base = PREFIX + '.' + familyId.getNamespace() + '.' + familyId.getPath();
        return lines == 1 ? base : base + '.' + index;
    }

    /**
     * @return every key for a lore block of {@code lines} lines, in order
     */
    public static List<String> keys(Identifier familyId, int lines) {
        if (lines <= 0) {
            return Collections.emptyList();
        }
        List<String> keys = new ArrayList<>(lines);
        for (int i = 0; i < lines; i++) {
            keys.add(key(familyId, i, lines));
        }
        return keys;
    }

    /**
     * Writes a block's lore into its tooltip, collapsed behind the expand key.
     * <p>
     * With the key held the lore lines are written out in order; otherwise a single hint line
     * stands in for them. A family with no lore gets neither — no stray hint on items that have
     * nothing to reveal.
     * <p>
     * The lookup happens here rather than at registration time, so it doesn't matter which member
     * of a family declared the lore or in what order the members registered.
     *
     * @param familyId the block's family id, from {@link #familyId}
     * @param out      the tooltip line consumer
     */
    public static void append(Identifier familyId, Consumer<Component> out) {
        int count = lines(familyId).size();
        if (count == 0) {
            return;
        }

        if (!TooltipKeys.isExpandDown()) {
            out.accept(Component.translatableWithFallback(HINT_KEY, HINT_FALLBACK).withStyle(HINT_COLOR));
            return;
        }

        for (String key : keys(familyId, count)) {
            out.accept(Component.translatable(key));
        }
    }
}
