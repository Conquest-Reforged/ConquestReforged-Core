package com.conquestrefabricated.content.arms;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

/**
 * Builds {@code conquest:arms_station} recipes from a data generator.
 *
 * <p>This is the entry point content submodules use. It is loader agnostic - it only needs vanilla's
 * {@link RecipeOutput} - so the same calls work from a Fabric {@code FabricRecipeProvider} and from a
 * NeoForge {@code RecipeProvider}:</p>
 *
 * <pre>{@code
 * // inside RecipeProvider#buildRecipes
 * ArmsStationRecipeBuilder.armsStation(Items.IRON_SWORD, ModItems.CRUSADER_SWORD).save(this.output);
 *
 * // one result reachable from several tiers of vanilla gear
 * ArmsStationRecipeBuilder.armsStationFromAny(this.output, ModItems.BASTARD_SWORD,
 *         Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
 *
 * // tag input - RecipeProvider#tag gives you the Ingredient
 * ArmsStationRecipeBuilder.armsStation(this.tag(ItemTags.SWORDS), ModItems.LONGSWORD).save(this.output);
 *
 * // gear whose numbers are hand-tuned rather than derived from the metal you feed in
 * ArmsStationRecipeBuilder.armsStation(Items.SHIELD, ModItems.HERALDIC_SHIELD)
 *         .keepOwnStats()
 *         .save(this.output);
 * }</pre>
 *
 * <p>By default the result inherits the input's material - protection, durability, attack values,
 * enchantability - so one recipe per shape covers every metal tier you offer it. See
 * {@link ArmsStationRecipe#MATERIAL_COMPONENTS}.</p>
 *
 * <p>Recipes are written without an unlock advancement, matching how the arms station is meant to be
 * used: the station itself is the discovery surface, not the recipe book.</p>
 */
public final class ArmsStationRecipeBuilder {

    /** Appended to the result's item name when no explicit recipe id is given. */
    public static final String DEFAULT_ID_SUFFIX = "_from_arms_station";

    private final Ingredient input;
    private final ItemLike result;
    private int count = 1;
    private boolean showNotification = false;
    private boolean inheritMaterial = true;

    private ArmsStationRecipeBuilder(Ingredient input, ItemLike result) {
        this.input = input;
        this.result = result;
    }

    public static ArmsStationRecipeBuilder armsStation(Ingredient input, ItemLike result) {
        return new ArmsStationRecipeBuilder(input, result);
    }

    /** Reforges a single vanilla (or modded) item into {@code result}. */
    public static ArmsStationRecipeBuilder armsStation(ItemLike input, ItemLike result) {
        return armsStation(Ingredient.of(input), result);
    }

    /**
     * Reforges anything in {@code input} into {@code result}.
     *
     * <p>Item tags are not bound in the built-in registry while data is being generated, so the
     * lookup has to come from the generator's registries - inside a {@code RecipeProvider} that is
     * {@code this.registries.lookupOrThrow(Registries.ITEM)}, or just use {@code this.tag(input)}
     * with {@link #armsStation(Ingredient, ItemLike)}.</p>
     */
    public static ArmsStationRecipeBuilder armsStation(HolderGetter<Item> items, TagKey<Item> input, ItemLike result) {
        return armsStation(Ingredient.of(items.getOrThrow(input)), result);
    }

    /** How many of the result a single craft yields. Defaults to 1. */
    public ArmsStationRecipeBuilder count(int count) {
        this.count = count;
        return this;
    }

    /** Whether crafting this pops the "new recipe unlocked" toast. Defaults to false. */
    public ArmsStationRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    /**
     * Turns off material inheritance, so the result keeps the stats it was defined with instead of
     * taking them from whatever was fed into the station. Use this for gear whose numbers are
     * hand-tuned rather than derived from a metal.
     *
     * @see ArmsStationRecipe#MATERIAL_COMPONENTS
     */
    public ArmsStationRecipeBuilder keepOwnStats() {
        this.inheritMaterial = false;
        return this;
    }

    public ArmsStationRecipe build() {
        return new ArmsStationRecipe(
                new Recipe.CommonInfo(this.showNotification),
                this.input,
                new ItemStackTemplate(this.result.asItem(), this.count),
                this.inheritMaterial);
    }

    /** Writes the recipe under {@code id}. */
    public void save(RecipeOutput output, Identifier id) {
        output.accept(ResourceKey.create(Registries.RECIPE, id), this.build(), null);
    }

    /** Writes the recipe under {@code <result namespace>:<result path>_from_arms_station}. */
    public void save(RecipeOutput output) {
        this.save(output, defaultId(this.result));
    }

    /** The id {@link #save(RecipeOutput)} would use for {@code result}. */
    public static Identifier defaultId(ItemLike result) {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        return Identifier.fromNamespaceAndPath(resultId.getNamespace(), resultId.getPath() + DEFAULT_ID_SUFFIX);
    }

    /** Shorthand for the common case: one input item, one result, default id. */
    public static void armsStation(RecipeOutput output, ItemLike input, ItemLike result) {
        armsStation(input, result).save(output);
    }

    /**
     * Offers the same result from several inputs - for example every metal tier of a vanilla sword.
     * Each input gets its own recipe, suffixed with the input's name so the ids stay unique.
     */
    public static void armsStationFromAny(RecipeOutput output, ItemLike result, ItemLike... inputs) {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result.asItem());
        for (ItemLike input : inputs) {
            Identifier inputId = BuiltInRegistries.ITEM.getKey(input.asItem());
            armsStation(input, result).save(output, Identifier.fromNamespaceAndPath(
                    resultId.getNamespace(), resultId.getPath() + "_from_" + inputId.getPath()));
        }
    }
}
