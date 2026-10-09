package com.conquestrefabricated.content.moss;

import com.conquestrefabricated.core.Namespaces;
import com.conquestrefabricated.core.block.StateUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

/**
 * Which blocks have a mossy twin, found by name: {@code ns:thing} pairs with {@code ns:mossy_thing}.
 *
 * <p>The convention holds for Conquest's own stone, brick and logs and for vanilla's cobblestone and
 * stone bricks, and because every shape of a family is named after its parent it carries over to
 * stairs, slabs and the rest without a table to keep in step.</p>
 */
public final class Mossing {

    public static final String PREFIX = "mossy_";

    private Mossing() {
    }

    /**
     * The mossy twin of {@code block}, if it has one and is not mossy itself. Looked for in the
     * block's own namespace first, then Conquest's, which is where mossy versions of vanilla woods live.
     */
    public static Optional<Block> mossyOf(Block block) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        if (id.getPath().startsWith(PREFIX)) {
            return Optional.empty();
        }
        return firstPresent(block, PREFIX + id.getPath(), id.getNamespace(), Namespaces.DEFAULT);
    }

    /** What {@code mossy} is a mossy version of, in its own namespace or vanilla's, if the plain block exists. */
    public static Optional<Block> plainOf(Block mossy) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(mossy);
        if (!id.getPath().startsWith(PREFIX)) {
            return Optional.empty();
        }
        return firstPresent(mossy, id.getPath().substring(PREFIX.length()), id.getNamespace(), "minecraft");
    }

    private static Optional<Block> firstPresent(Block except, String path, String... namespaces) {
        for (String namespace : namespaces) {
            Optional<Block> found = BuiltInRegistries.BLOCK
                    .getOptional(Identifier.fromNamespaceAndPath(namespace, path))
                    .filter(block -> block != except);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    /** A mossy log's plain log, or empty for anything that is not a mossy log. */
    public static Optional<Block> plainLogOf(Block mossy) {
        // By name: block tags are not bound while data is being generated, which is when this matters.
        if (!BuiltInRegistries.BLOCK.getKey(mossy).getPath().endsWith("_log")) {
            return Optional.empty();
        }
        return plainOf(mossy);
    }

    /** {@code to}'s default state with every property {@code from} shares with it carried across. */
    public static BlockState transfer(BlockState from, Block to) {
        BlockState state = to.defaultBlockState();
        for (Property<?> property : from.getProperties()) {
            state = copy(state, from, property);
        }
        return state;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Comparable<T>> BlockState copy(BlockState to, BlockState from, Property<T> property) {
        Property target = to.getBlock().getStateDefinition().getProperty(property.getName());
        if (target == null) {
            return to;
        }
        return StateUtils.with(to, target, property.getName(from.getValue(property)));
    }
}
