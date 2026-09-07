package com.conquestrefabricated.content.arms;

import com.conquestrefabricated.content.items.item.ArmorItem;
import com.conquestrefabricated.content.items.item.WeaponType;
import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     * The vanilla tag each armour slot is reforged from: any helmet makes a helmet, any chestplate
     * makes a chestplate. {@link ArmorType#BODY} is absent on purpose - wolf and horse armour has no
     * station recipe.
     */
    public static final Map<ArmorType, TagKey<Item>> ARMOR_INPUTS = Map.of(
            ArmorType.HELMET, ItemTags.HEAD_ARMOR,
            ArmorType.CHESTPLATE, ItemTags.CHEST_ARMOR,
            ArmorType.LEGGINGS, ItemTags.LEG_ARMOR,
            ArmorType.BOOTS, ItemTags.FOOT_ARMOR);

    /**
     * Writes one arms station recipe for every piece of Conquest armour that is registered, taking
     * the matching vanilla slot tag as its input - so any chestplate reforges into any of ours, and
     * the material it was made of carries across.
     *
     * <p>Call it from whichever module's recipe provider has the armour on its classpath; it walks
     * {@link Namespaces} so a module picks up its own items and any addon's registered alongside
     * it.</p>
     *
     * <p>Armour is recognised by {@link ArmorItem#getArmorType()} rather than by its
     * {@code minecraft:equippable} component, because item components are not bound while data is
     * being generated - {@code Item.components()} throws there. Gear registered as a plain
     * {@code Item} carries nothing datagen can read, so it is skipped; {@code skipped} in the return
     * value counts those.</p>
     *
     * @param items the generator's item lookup, for resolving the slot tags
     * @return how many recipes were written, and how many Conquest items were passed over
     */
    public static Generated allArmor(RecipeOutput output, HolderGetter<Item> items) {
        return generate(output, armorInputs(items)::get);
    }

    /**
     * Writes one arms station recipe for every Conquest weapon registered through
     * {@code ModItemHelper}'s weapon methods, taking the matching vanilla input - any sword reforges
     * into any of our swords, any axe into any of our axes.
     *
     * <p>A weapon registered through plain {@code register(..)} has no recorded kind and is passed
     * over; see {@link WeaponType}.</p>
     *
     * @param items the generator's item lookup, for resolving the input tags
     * @return how many recipes were written, and how many Conquest items were passed over
     */
    public static Generated allWeapons(RecipeOutput output, HolderGetter<Item> items) {
        return generate(output, weaponInputs(items)::get);
    }

    /**
     * {@link #allArmor} and {@link #allWeapons} in one pass, which is what a content module normally
     * wants. Because it is one pass, {@code skipped} counts only the items neither could classify.
     */
    public static Generated allEquipment(RecipeOutput output, HolderGetter<Item> items) {
        Map<Item, Ingredient> armour = armorInputs(items);
        Map<Item, Ingredient> weapons = weaponInputs(items);
        return generate(output, item -> {
            Ingredient input = armour.get(item);
            return input != null ? input : weapons.get(item);
        });
    }

    /**
     * Walks every registered item in a {@link Namespaces Conquest namespace}, writing a recipe for
     * each one {@code input} can place at the station.
     */
    private static Generated generate(RecipeOutput output, Function<Item, Ingredient> input) {
        Set<String> namespaces = Namespaces.stream().collect(Collectors.toSet());
        int written = 0;
        int skipped = 0;

        for (Item item : BuiltInRegistries.ITEM) {
            if (!namespaces.contains(BuiltInRegistries.ITEM.getKey(item).getNamespace())) {
                continue;
            }
            Ingredient ingredient = input.apply(item);
            if (ingredient == null) {
                skipped++;
                continue;
            }
            armsStation(ingredient, item).save(output);
            written++;
        }

        return new Generated(written, skipped);
    }

    /** Every registered Conquest armour piece, against the vanilla tag for its slot. */
    private static Map<Item, Ingredient> armorInputs(HolderGetter<Item> items) {
        Map<ArmorType, Ingredient> byType = new EnumMap<>(ArmorType.class);
        ARMOR_INPUTS.forEach((type, tag) -> byType.put(type, Ingredient.of(items.getOrThrow(tag))));

        Map<Item, Ingredient> inputs = new IdentityHashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof ArmorItem armor) {
                Ingredient input = byType.get(armor.getArmorType());
                if (input != null) {
                    inputs.put(item, input);
                }
            }
        }
        return inputs;
    }

    /** Every registered Conquest weapon, against the vanilla input for its kind. */
    private static Map<Item, Ingredient> weaponInputs(HolderGetter<Item> items) {
        Map<WeaponType, Ingredient> byKind = new EnumMap<>(WeaponType.class);
        for (WeaponType kind : WeaponType.values()) {
            byKind.put(kind, kind.input(items));
        }

        Map<Item, Ingredient> inputs = new IdentityHashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            WeaponType.of(item).ifPresent(kind -> inputs.put(item, byKind.get(kind)));
        }
        return inputs;
    }

    /**
     * @param recipes recipes written
     * @param skipped Conquest items the pass could not classify, so had to pass over
     */
    public record Generated(int recipes, int skipped) {
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
