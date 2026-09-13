package com.conquestrefabricated.content.painting;

import java.util.List;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.Items;
import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.Stations;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * One thing a painter's kit can make: a base material, something to colour it with, and the result.
 *
 * <pre>{@code
 * {
 *   "type": "conquest:painting",
 *   "base": "minecraft:cobblestone",
 *   "paint": "minecraft:red_dye",
 *   "result": { "id": "conquest:red_stucco", "count": 1 }
 * }
 * }</pre>
 *
 * <p>The only thing separating this from a {@code conquest:tool_crafting} recipe is the second
 * ingredient, which is why it needs a recipe type - and a {@link PaintingInput} - of its own.</p>
 *
 * @see PaintingRecipeBuilder
 */
public class PaintingRecipe implements Recipe<PaintingInput> {

    public static final MapCodec<PaintingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("base").forGetter(PaintingRecipe::base),
            Ingredient.CODEC.fieldOf("paint").forGetter(PaintingRecipe::paint),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(PaintingRecipe::result)
    ).apply(instance, PaintingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PaintingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, PaintingRecipe::base,
            Ingredient.CONTENTS_STREAM_CODEC, PaintingRecipe::paint,
            ItemStackTemplate.STREAM_CODEC, PaintingRecipe::result,
            PaintingRecipe::new
    );

    private final CommonInfo commonInfo;
    private final Ingredient base;
    private final Ingredient paint;
    private final ItemStackTemplate result;

    public PaintingRecipe(CommonInfo commonInfo, Ingredient base, Ingredient paint, ItemStackTemplate result) {
        this.commonInfo = commonInfo;
        this.base = base;
        this.paint = paint;
        this.result = result;
    }

    public PaintingRecipe(Ingredient base, Ingredient paint, ItemStackTemplate result) {
        this(new CommonInfo(false), base, paint, result);
    }

    /** What is being painted. */
    public Ingredient base() {
        return this.base;
    }

    /** What it is painted with. */
    public Ingredient paint() {
        return this.paint;
    }

    public ItemStackTemplate result() {
        return this.result;
    }

    @Override
    public boolean matches(PaintingInput input, Level level) {
        return this.matchesBase(input.base()) && this.matchesPaint(input.paint());
    }

    /**
     * Whether {@code stack} is a material this recipe knows how to paint, whatever the paint.
     *
     * <p>The kit lists what a base could become before any paint is in, so it asks about the halves
     * separately; {@link #matches} is still what decides whether a craft can go ahead.</p>
     */
    public boolean matchesBase(ItemStack stack) {
        return this.base.test(stack);
    }

    public boolean matchesPaint(ItemStack stack) {
        return this.paint.test(stack);
    }

    @Override
    public ItemStack assemble(PaintingInput input) {
        return this.result.create();
    }

    @Override
    public boolean showNotification() {
        return this.commonInfo.showNotification();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean isSpecial() {
        // Not special, though nothing crafts these at a bench. A special recipe is skipped by
        // ServerRecipeBook.addRecipes, which is the only thing that sends a recipe's display to the
        // client - so marking these special made them invisible to every recipe viewer. They are
        // kept out of the recipe book by their category instead, see Stations.
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        // Not laid out in a crafting grid, but it still has to name its ingredients: a recipe that
        // reports none is dropped outright once it is no longer marked special, taking its display
        return PlacementInfo.create(List.of(this.base, this.paint));
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return Stations.RECIPE_BOOK_CATEGORY;
    }

    @Override
    public RecipeType<PaintingRecipe> getType() {
        return PaintersKit.RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<PaintingRecipe> getSerializer() {
        return PaintersKit.RECIPE_SERIALIZER;
    }

    /**
     * What a recipe viewer and the recipe book are shown. Modded recipes never cross to the client,
     * so this is the only description of it that gets there.
     *
     * @see com.conquestrefabricated.content.station.StationRecipeDisplay
     */
    @Override
    public List<RecipeDisplay> display() {
        return StationRecipeDisplay.of(this.base, this.paint, this.result, Stations.PAINTERS_KIT);
    }
}
