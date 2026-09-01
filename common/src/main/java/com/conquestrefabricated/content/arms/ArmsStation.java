package com.conquestrefabricated.content.arms;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Every object that makes up the arms station: the block, its item, the menu type and the
 * {@code conquest:arms_station} recipe type/serializer.
 *
 * <p>The recipe type and serializer are safe to build eagerly. The block, item and menu type are
 * not: {@code Block} and {@code Item} constructors claim an intrusive holder from their registry,
 * which only works while that registry is unfrozen. NeoForge unfreezes one registry at a time during
 * {@code RegisterEvent}, so each is built on demand by its {@code create...} method, which the
 * loader calls from the matching registration phase (see {@code ArmsStationInit} in the fabric and
 * neoforge source sets).</p>
 */
public final class ArmsStation {

    public static final String NAMESPACE = "conquest";
    public static final String PATH = "arms_station";
    public static final Identifier ID = Identifier.fromNamespaceAndPath(NAMESPACE, PATH);

    public static final ResourceKey<Block> BLOCK_KEY = ResourceKey.create(Registries.BLOCK, ID);
    public static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(Registries.ITEM, ID);

    public static final String TOOLTIP_KEY = "tooltip." + NAMESPACE + ".block." + PATH;
    public static final String CONTAINER_TITLE_KEY = "container." + NAMESPACE + "." + PATH;

    public static final RecipeType<ArmsStationRecipe> RECIPE_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return ID.toString();
        }
    };

    public static final RecipeSerializer<ArmsStationRecipe> RECIPE_SERIALIZER =
            new RecipeSerializer<>(ArmsStationRecipe.MAP_CODEC, ArmsStationRecipe.STREAM_CODEC);

    /** Set by {@link #createBlock()} during block registration. */
    public static Block BLOCK;
    /** Set by {@link #createItem()} during item registration. */
    public static Item ITEM;
    /** Set by {@link #createMenu()} during menu registration. */
    public static MenuType<ArmsStationMenu> MENU;

    private ArmsStation() {
    }

    /** Builds the block. Call only while the block registry is open, then register the result. */
    public static Block createBlock() {
        BLOCK = new ArmsStationBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.SMITHING_TABLE).setId(BLOCK_KEY));
        return BLOCK;
    }

    /** Builds the block item. Call only while the item registry is open, after {@link #createBlock()}. */
    public static Item createItem() {
        ITEM = new ArmsStationBlockItem(
                Objects.requireNonNull(BLOCK, "Arms station block must be created before its item"),
                new Item.Properties().useBlockDescriptionPrefix().setId(ITEM_KEY));
        return ITEM;
    }

    /** Builds the menu type. */
    public static MenuType<ArmsStationMenu> createMenu() {
        MENU = new MenuType<>(ArmsStationMenu::new, FeatureFlags.DEFAULT_FLAGS);
        return MENU;
    }

    /**
     * Every arms station recipe that accepts {@code input}, in datapack order.
     *
     * <p>Custom recipe types are never shipped to clients, so this only ever returns results on the
     * logical server; the client is told what to draw by {@link ArmsStationOptionsPayload}.</p>
     */
    public static List<RecipeHolder<ArmsStationRecipe>> recipesFor(Level level, ItemStack input) {
        if (input.isEmpty() || !(level.recipeAccess() instanceof RecipeManager recipes)) {
            return List.of();
        }

        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        List<RecipeHolder<ArmsStationRecipe>> matches = new ArrayList<>();
        for (RecipeHolder<?> holder : recipes.getRecipes()) {
            if (holder.value() instanceof ArmsStationRecipe recipe && recipe.matches(recipeInput, level)) {
                @SuppressWarnings("unchecked")
                RecipeHolder<ArmsStationRecipe> typed = (RecipeHolder<ArmsStationRecipe>) holder;
                matches.add(typed);
            }
        }
        return matches;
    }

    /** Whether any arms station recipe accepts {@code input}. Logical server only. */
    public static boolean isValidInput(Level level, ItemStack input) {
        if (input.isEmpty() || !(level.recipeAccess() instanceof RecipeManager recipes)) {
            return false;
        }

        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        for (RecipeHolder<?> holder : recipes.getRecipes()) {
            if (holder.value() instanceof ArmsStationRecipe recipe && recipe.matches(recipeInput, level)) {
                return true;
            }
        }
        return false;
    }
}
