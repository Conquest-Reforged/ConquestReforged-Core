package com.conquestrefabricated.content.arms;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.enchantment.Repairable;

import java.util.List;
import java.util.Optional;

/**
 * A single-input arms station recipe: one item in, one item out, with the input's material carried
 * over to the result.
 *
 * <p>Reforging a diamond chestplate into a Conquest chestplate gives you diamond protection,
 * toughness, durability and enchantability; feed it copper and you get copper's. That is the whole
 * point of the station - the recipe picks the <i>shape</i> of the gear, the item you put in picks
 * the <i>metal</i>. Set {@code "inherit_material": false} on a recipe whose result should keep its
 * own hand-tuned stats instead.</p>
 *
 * <p>Recipe JSON looks like a stonecutting recipe:</p>
 * <pre>{@code
 * {
 *   "type": "conquest:arms_station",
 *   "ingredient": "minecraft:diamond_chestplate",
 *   "result": "conquest_armory:crusader_chestplate"
 * }
 * }</pre>
 *
 * <p>The result is labelled with the material it came out of: a tooltip line reading "Material:
 * Diamond", plus the bare id under {@code minecraft:custom_data}{@code .material} for submodules that
 * want to render differently per metal.</p>
 *
 * <p>Submodules generate these with {@link ArmsStationRecipeBuilder}.</p>
 */
public class ArmsStationRecipe extends SingleItemRecipe {

    /**
     * What the input's material decides. Everything outside this list - the model, the equip slot
     * and armor texture ({@code minecraft:equippable}), the name, the rarity - stays with the result,
     * so a Conquest chestplate reforged from diamond still looks like a Conquest chestplate.
     */
    public static final List<DataComponentType<?>> MATERIAL_COMPONENTS = List.of(
            DataComponents.ATTRIBUTE_MODIFIERS,  // armor, toughness, knockback resistance, attack damage/speed
            DataComponents.MAX_DAMAGE,           // durability pool
            DataComponents.ENCHANTABLE,          // enchantability
            DataComponents.REPAIRABLE,           // what repairs it on an anvil
            DataComponents.DAMAGE_RESISTANT,     // netherite's fire immunity
            DataComponents.TOOL,                 // mining tier and speed
            DataComponents.WEAPON                // durability cost per swing, shield disable
    );

    /** Key the material id is written under in the result's {@code minecraft:custom_data}. */
    public static final String MATERIAL_TAG_KEY = "material";
    /** Lang key for the tooltip line; takes the material name as its argument. */
    public static final String MATERIAL_TOOLTIP_KEY = "tooltip." + ArmsStation.NAMESPACE + ".arms_station.material";
    /** Lang key prefix for naming a material, e.g. {@code material.conquest.netherite}. */
    public static final String MATERIAL_NAME_PREFIX = "material." + ArmsStation.NAMESPACE + ".";

    private static final String TAG_PREFIX = "repairs_";
    /** Longest first: {@code _tool_materials} has to win over {@code _materials}. */
    private static final List<String> TAG_SUFFIXES = List.of("_tool_materials", "_materials", "_armor", "_helmet");

    /** What the player put into the input and should not lose by reforging it. */
    public static final List<DataComponentType<?>> PLAYER_COMPONENTS = List.of(
            DataComponents.CUSTOM_DATA,
            DataComponents.ENCHANTMENTS,
            DataComponents.CUSTOM_NAME,
            DataComponents.UNBREAKABLE,
            DataComponents.REPAIR_COST
    );

    public static final MapCodec<ArmsStationRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ArmsStationRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(ArmsStationRecipe::result),
            Codec.BOOL.optionalFieldOf("inherit_material", true).forGetter(ArmsStationRecipe::inheritsMaterial)
    ).apply(instance, ArmsStationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArmsStationRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, ArmsStationRecipe::input,
            ItemStackTemplate.STREAM_CODEC, ArmsStationRecipe::result,
            ByteBufCodecs.BOOL, ArmsStationRecipe::inheritsMaterial,
            ArmsStationRecipe::new
    );

    private final boolean inheritMaterial;

    public ArmsStationRecipe(CommonInfo commonInfo, Ingredient input, ItemStackTemplate result, boolean inheritMaterial) {
        super(commonInfo, input, result);
        this.inheritMaterial = inheritMaterial;
    }

    public ArmsStationRecipe(Ingredient input, ItemStackTemplate result) {
        this(new CommonInfo(false), input, result, true);
    }

    /** Whether the result takes its stats from whatever was fed into the station. */
    public boolean inheritsMaterial() {
        return this.inheritMaterial;
    }

    @Override
    public RecipeType<ArmsStationRecipe> getType() {
        return ArmsStation.RECIPE_TYPE;
    }

    @Override
    public RecipeSerializer<ArmsStationRecipe> getSerializer() {
        return ArmsStation.RECIPE_SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean isSpecial() {
        // Kept out of the recipe book: the arms station has its own recipe picker.
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input) {
        ItemStack from = input.item();
        ItemStack result = result().create();

        if (this.inheritMaterial) {
            for (DataComponentType<?> type : MATERIAL_COMPONENTS) {
                copy(from, result, type);
            }
        }
        for (DataComponentType<?> type : PLAYER_COMPONENTS) {
            copy(from, result, type);
        }
        carryWear(from, result);

        if (this.inheritMaterial) {
            // After the component copies: labelling writes into CUSTOM_DATA, which the loop above
            // replaces wholesale with the input's copy.
            materialOf(from).ifPresent(material -> label(result, material));
        }

        return result;
    }

    /**
     * The material an item is made of, as a bare id like {@code diamond} or {@code copper}.
     *
     * <p>26.1 has no material field left to read - tools and armor are just bags of components - but
     * what repairs a piece of gear still says what it is made of, and unlike the item's display name
     * that is stable data rather than English. So the material is read off the repair ingredient tag:
     * {@code #minecraft:diamond_tool_materials} and {@code #minecraft:repairs_diamond_armor} both
     * mean diamond. Empty for gear with no repair material, such as a bow.</p>
     */
    public static Optional<String> materialOf(ItemStack stack) {
        Repairable repairable = stack.get(DataComponents.REPAIRABLE);
        if (repairable == null) {
            return Optional.empty();
        }
        return repairable.items().unwrapKey().map(tag -> materialFromTagPath(tag.location().getPath()));
    }

    /** {@code repairs_diamond_armor} and {@code diamond_tool_materials} both reduce to {@code diamond}. */
    static String materialFromTagPath(String tagPath) {
        String material = tagPath.startsWith(TAG_PREFIX) ? tagPath.substring(TAG_PREFIX.length()) : tagPath;
        for (String suffix : TAG_SUFFIXES) {
            if (material.endsWith(suffix)) {
                return material.substring(0, material.length() - suffix.length());
            }
        }
        return material;
    }

    /**
     * The tooltip line for a material, e.g. "Material: Diamond". Materials the lang file doesn't
     * name fall back to their id in title case, so a modded metal reads sensibly without needing a
     * translation - add {@code material.conquest.<id>} to override it.
     */
    public static Component materialTooltip(String material) {
        return Component.translatable(MATERIAL_TOOLTIP_KEY, materialName(material))
                .withStyle(style -> style.withColor(ChatFormatting.GRAY).withItalic(false));
    }

    public static Component materialName(String material) {
        return Component.translatableWithFallback(MATERIAL_NAME_PREFIX + material, titleCase(material));
    }

    /** Records the material on the result, both as a tooltip line and for submodules to read back. */
    private static void label(ItemStack stack, String material) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(MATERIAL_TAG_KEY, material));
        ItemLore lore = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        stack.set(DataComponents.LORE, lore.withLineAdded(materialTooltip(material)));
    }

    private static String titleCase(String id) {
        StringBuilder text = new StringBuilder(id.length());
        boolean startOfWord = true;
        for (char c : id.toCharArray()) {
            if (c == '_') {
                text.append(' ');
                startOfWord = true;
            } else {
                text.append(startOfWord ? Character.toUpperCase(c) : c);
                startOfWord = false;
            }
        }
        return text.toString();
    }

    /**
     * Reforging moves the wear across rather than resetting it, so the station cannot be used as a
     * free anvil. Clamped in case the result has a smaller durability pool than the input, which can
     * only happen when this recipe does not inherit the material.
     */
    private static void carryWear(ItemStack from, ItemStack to) {
        Integer damage = from.get(DataComponents.DAMAGE);
        Integer maxDamage = to.get(DataComponents.MAX_DAMAGE);
        if (damage == null || maxDamage == null || maxDamage <= 0) {
            return;
        }
        to.set(DataComponents.DAMAGE, Math.min(damage, maxDamage - 1));
    }

    private static <T> void copy(ItemStack from, ItemStack to, DataComponentType<T> type) {
        T value = from.get(type);
        if (value != null) {
            to.set(type, value);
        }
    }
}
