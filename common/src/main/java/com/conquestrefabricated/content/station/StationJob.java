package com.conquestrefabricated.content.station;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

/**
 * What a workstation has been told to make.
 *
 * <p>Two things can be picked from a station's list and they are not the same kind of thing. Most
 * options are a recipe, and naming one by its key is enough. The shape toggle's options are not
 * recipes at all - they come from the block family - so there is no key to name, and the job is the
 * shape itself.</p>
 *
 * @see StationFamilies
 */
public sealed interface StationJob {

    /** One of the station's own recipes. */
    record OfRecipe(ResourceKey<Recipe<?>> recipe) implements StationJob {
    }

    /** A member of the input's block family, reached by the shape toggle rather than by a recipe. */
    record OfShape(Identifier shape) implements StationJob {

        /** The item this names, or null if nothing is registered under it any more. */
        public @Nullable Item item() {
            return BuiltInRegistries.ITEM.getOptional(this.shape).orElse(null);
        }
    }

    static StationJob of(ResourceKey<Recipe<?>> recipe) {
        return new OfRecipe(recipe);
    }

    static StationJob ofShape(ItemStack shape) {
        return new OfShape(BuiltInRegistries.ITEM.getKey(shape.getItem()));
    }
}
