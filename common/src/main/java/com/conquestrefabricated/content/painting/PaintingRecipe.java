package com.conquestrefabricated.content.painting;

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
        // Kept out of the recipe book: the kit's own picker is where these are found.
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        // Nothing here can be laid out in a crafting grid.
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }

    @Override
    public RecipeType<PaintingRecipe> getType() {
        return PaintersKit.RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<PaintingRecipe> getSerializer() {
        return PaintersKit.RECIPE_SERIALIZER;
    }
}
