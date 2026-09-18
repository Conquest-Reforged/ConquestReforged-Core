package com.conquestrefabricated.content.leatherworking;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * The two recipe types the leatherworking blocks work from: soaking in a barrel and stretching on a
 * frame.
 *
 * <p>Neither block has a menu, so there is nothing here but the recipe types - the blocks themselves are
 * ordinary Conquest blocks, registered by whichever module declares them. Core never names an item of
 * its own in these recipes; the recipes are data, and the module that owns the hides owns the JSON.</p>
 *
 * @see SoakingRecipe
 * @see StretchingRecipe
 */
public final class LeatherworkingStations {

    /** Recipe type id for {@link SoakingRecipe}. */
    public static final Identifier SOAKING_ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "soaking");

    /** Recipe type id for {@link StretchingRecipe}. */
    public static final Identifier STRETCHING_ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "stretching");

    public static final RecipeType<SoakingRecipe> SOAKING_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return SOAKING_ID.toString();
        }
    };

    public static final RecipeSerializer<SoakingRecipe> SOAKING_SERIALIZER =
            new RecipeSerializer<>(SoakingRecipe.MAP_CODEC, SoakingRecipe.STREAM_CODEC);

    public static final RecipeType<StretchingRecipe> STRETCHING_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return STRETCHING_ID.toString();
        }
    };

    public static final RecipeSerializer<StretchingRecipe> STRETCHING_SERIALIZER =
            new RecipeSerializer<>(StretchingRecipe.MAP_CODEC, StretchingRecipe.STREAM_CODEC);

    private LeatherworkingStations() {
    }
}
