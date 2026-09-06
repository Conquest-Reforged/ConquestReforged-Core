package com.conquestrefabricated.core.item.group;

import com.conquestrefabricated.core.Namespaces;
import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.function.Supplier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

/**
 * Creates creative tabs owned by an addon rather than by Conquest itself.
 * <p>
 * A tab made here is registered under the addon's own namespace ({@code <namespace>:<label>}), is
 * ordered by {@code assets/<namespace>/groups/<label>.txt} like every other Conquest tab, and
 * fills itself with the block families assigned to it via {@code Props.group(..)}.
 * <p>
 * Call it during mod initialisation, before blocks register:
 * <pre>{@code
 * public static final CreativeModeTab STONE =
 *         AddonGroups.create("myaddon", "a_stone", 0, AddonGroups.icon("myaddon:basalt_ashlar"));
 * }</pre>
 * then hand the tab to the builder:
 * <pre>{@code
 * VanillaProps.stone().group(STONE).name("basalt_ashlar").register(types);
 * }</pre>
 * <p>
 * Plain (non-block) items have no family, so list them in the tab's {@code .txt} ordering file
 * instead — anything named there that exists is added to the tab.
 */
public final class AddonGroups {

    private AddonGroups() {
    }

    /**
     * Creates and registers a creative tab owned by {@code namespace}.
     *
     * @param namespace the addon's namespace; registered with {@link Namespaces} if it isn't already
     * @param label     the tab's path, also the name of its {@code groups/<label>.txt} ordering file
     * @param order     sort index among Conquest's tabs; also picks the row and column
     * @param icon      the tab's icon
     */
    public static CreativeModeTab create(String namespace, String label, int order, Supplier<ItemStack> icon) {
        Namespaces.register(namespace);
        return createByPlatform(namespace, label, order, icon);
    }

    @ExpectPlatform
    public static CreativeModeTab createByPlatform(String namespace, String label, int order, Supplier<ItemStack> icon) {
        throw new AssertionError("This method should be replaced by platform implementations!");
    }

    /**
     * Icon helper: resolves a block id lazily, so it works before the block is registered.
     */
    public static Supplier<ItemStack> icon(String blockId) {
        return com.conquestrefabricated.core.util.Provider.block(blockId).toStack();
    }

    /**
     * Icon helper that falls back to a second id when the first isn't present, for icons that
     * depend on a module the user may not have installed.
     */
    public static Supplier<ItemStack> icon(String blockId, String fallbackBlockId) {
        return com.conquestrefabricated.content.blocks.group.ModGroups.iconWithFallback(blockId, fallbackBlockId);
    }

    /**
     * Icon helper for a plain item rather than a block.
     */
    public static Supplier<ItemStack> itemIcon(String itemId) {
        return com.conquestrefabricated.core.util.Provider.item(itemId).toStack();
    }
}
