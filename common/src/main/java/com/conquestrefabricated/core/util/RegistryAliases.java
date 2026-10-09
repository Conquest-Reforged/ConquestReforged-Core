package com.conquestrefabricated.core.util;

import com.conquestrefabricated.core.util.log.Log;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

/**
 * Old name -> new name redirects for registry entries, so content can be renamed without being
 * dropped from existing worlds. Both loaders resolve an alias wherever a registry is looked up by
 * name (chunk palettes, item stacks, commands), and an alias only applies while the old name
 * isn't registered itself.
 * <p>
 * Aliases have to be added while the registries are still open, ie during block registration.
 */
public final class RegistryAliases {

    private RegistryAliases() {
    }

    /**
     * Aliases a block and its block item, which share an id.
     */
    public static void block(Identifier from, Identifier to) {
        if (from.equals(to)) {
            return;
        }
        add(BuiltInRegistries.BLOCK, from, to);
        add(BuiltInRegistries.ITEM, from, to);
        Log.debug("Aliased {} -> {}", from, to);
    }

    @ExpectPlatform
    public static void add(Registry<?> registry, Identifier from, Identifier to) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }
}
