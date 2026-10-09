package com.conquestrefabricated.content.tools;

import com.conquestrefabricated.content.moss.Mossing;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Optional;

/**
 * Which planks a log is cut into, by name.
 *
 * <p>{@code apple_log} gives {@code apple_wood_planks}; a log with no planks of its own falls back to
 * the wood it is a variety of, dropping leading words until something matches ({@code light_pine_log}
 * gives pine, {@code ivy_covered_oak_log} gives oak). Vanilla's planks win for vanilla woods, and a
 * mossy log gives the planks of the plain one.</p>
 */
public final class LogPlanks {

    private static final String LOG_SUFFIX = "_log";

    private LogPlanks() {
    }

    public static Optional<Block> planksFor(Identifier log) {
        String path = log.getPath();
        if (path.startsWith(Mossing.PREFIX)) {
            path = path.substring(Mossing.PREFIX.length());
        }
        if (!path.endsWith(LOG_SUFFIX)) {
            return Optional.empty();
        }
        String[] words = path.substring(0, path.length() - LOG_SUFFIX.length()).split("_");

        for (int from = 0; from < words.length; from++) {
            String wood = String.join("_", Arrays.copyOfRange(words, from, words.length));
            Identifier[] candidates = {
                    Identifier.withDefaultNamespace(wood + "_planks"),
                    Identifier.fromNamespaceAndPath(log.getNamespace(), wood + "_wood_planks"),
                    Identifier.fromNamespaceAndPath(log.getNamespace(), wood + "_planks"),
                    Identifier.fromNamespaceAndPath(log.getNamespace(), wood + "_wood_plank")};
            for (Identifier candidate : candidates) {
                Optional<Block> planks = BuiltInRegistries.BLOCK.getOptional(candidate);
                if (planks.isPresent()) {
                    return planks;
                }
            }
        }
        return Optional.empty();
    }
}
