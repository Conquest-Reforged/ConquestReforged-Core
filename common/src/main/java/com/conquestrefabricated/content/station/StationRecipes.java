package com.conquestrefabricated.content.station;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Recipe lookup for the crafting stations.
 *
 * <p>Custom recipe types are never shipped to clients, so these only ever return results on the
 * logical server; the client is told what to draw by {@link StationOptionsPayload}.</p>
 */
public final class StationRecipes {

    private StationRecipes() {
    }

    /** Every recipe of {@code type} that passes {@code filter} and accepts {@code input}, in datapack order. */
    public static <R extends Recipe<SingleRecipeInput>> List<RecipeHolder<R>> recipesFor(
            Level level, RecipeType<R> type, Predicate<R> filter, ItemStack input) {

        if (input.isEmpty() || !(level.recipeAccess() instanceof RecipeManager recipes)) {
            return List.of();
        }

        return recipesFor(recipes.getRecipes(), type, filter, input, level);
    }

    /**
     * The world-free half of {@link #recipesFor(Level, RecipeType, Predicate, ItemStack)}, so the
     * lookup can be exercised against a hand-built recipe list.
     *
     * @param level only handed to {@code matches}, which single-item recipes ignore
     */
    public static <R extends Recipe<SingleRecipeInput>> List<RecipeHolder<R>> recipesFor(
            Collection<RecipeHolder<?>> recipes, RecipeType<R> type, Predicate<R> filter,
            ItemStack input, Level level) {

        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        List<RecipeHolder<R>> matches = new ArrayList<>();
        for (RecipeHolder<?> holder : recipes) {
            if (holder.value().getType() != type) {
                continue;
            }
            @SuppressWarnings("unchecked")
            RecipeHolder<R> typed = (RecipeHolder<R>) holder;
            if (filter.test(typed.value()) && typed.value().matches(recipeInput, level)) {
                matches.add(typed);
            }
        }
        return matches;
    }

    /** Whether any matching recipe accepts {@code input}. Logical server only. */
    public static <R extends Recipe<SingleRecipeInput>> boolean isValidInput(
            Level level, RecipeType<R> type, Predicate<R> filter, ItemStack input) {

        if (input.isEmpty() || !(level.recipeAccess() instanceof RecipeManager recipes)) {
            return false;
        }

        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        for (RecipeHolder<?> holder : recipes.getRecipes()) {
            if (holder.value().getType() != type) {
                continue;
            }
            @SuppressWarnings("unchecked")
            R recipe = (R) holder.value();
            if (filter.test(recipe) && recipe.matches(recipeInput, level)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether any recipe of {@code type} passing {@code filter} produces {@code item}.
     *
     * <p>Lets a station recognise its own output coming back in: the blocks it made are things it
     * knows how to work, even though no recipe takes them as an ingredient.</p>
     */
    public static <R extends Recipe<SingleRecipeInput>> boolean produces(
            Level level, RecipeType<R> type, Predicate<R> filter, ItemStack item) {

        if (item.isEmpty() || !(level.recipeAccess() instanceof RecipeManager recipes)) {
            return false;
        }

        // A station's result is fixed by the recipe rather than the input, so an empty input is
        // enough to ask what it makes.
        SingleRecipeInput noInput = new SingleRecipeInput(ItemStack.EMPTY);
        for (RecipeHolder<?> holder : recipes.getRecipes()) {
            if (holder.value().getType() != type) {
                continue;
            }
            @SuppressWarnings("unchecked")
            R recipe = (R) holder.value();
            if (filter.test(recipe) && recipe.assemble(noInput).getItem() == item.getItem()) {
                return true;
            }
        }
        return false;
    }

    /**
     * One stonecutting step away from a station's own output: which of the parents a recipe accepts,
     * and the recipe that cuts it.
     */
    public record Cut(int parentIndex, RecipeHolder<?> holder, SingleItemRecipe recipe) {
    }

    /**
     * Every stonecutting recipe that accepts one of {@code parents}.
     *
     * <p>This is how a station reaches a whole block family without a recipe file per shape. Core
     * already generates stonecutting recipes from each family's parent to its slabs, stairs and
     * walls, so a station that can make the parent can offer everything cut from it by walking one
     * step further along the same graph. Datapacks that add or remove those cuts are picked up for
     * free, and vanilla families work without any Conquest data at all.</p>
     */
    public static List<Cut> cutsFrom(Level level, List<ItemStack> parents) {
        if (parents.isEmpty() || !(level.recipeAccess() instanceof RecipeManager recipes)) {
            return List.of();
        }
        return cutsFrom(recipes.getRecipes(), parents, level);
    }

    /**
     * The world-free half of {@link #cutsFrom(Level, List)}, so the expansion can be exercised
     * against a hand-built recipe list.
     *
     * <p>Walked in a single pass rather than one pass per parent, since a station with a dozen
     * options would otherwise re-scan every recipe in the game a dozen times.</p>
     *
     * @param level only handed to {@code matches}, which single-item recipes ignore
     */
    public static List<Cut> cutsFrom(Collection<RecipeHolder<?>> recipes, List<ItemStack> parents, Level level) {
        if (parents.isEmpty()) {
            return List.of();
        }

        List<SingleRecipeInput> inputs = new ArrayList<>(parents.size());
        for (ItemStack parent : parents) {
            inputs.add(new SingleRecipeInput(parent));
        }

        List<Cut> cuts = new ArrayList<>();
        for (RecipeHolder<?> holder : recipes) {
            // Anything registered as stonecutting counts, but only single-item recipes can be read
            // back the way this needs - which every stonecutting recipe in practice is.
            if (holder.value().getType() != RecipeType.STONECUTTING
                    || !(holder.value() instanceof SingleItemRecipe cut)) {
                continue;
            }
            for (int i = 0; i < inputs.size(); i++) {
                if (cut.matches(inputs.get(i), level)) {
                    cuts.add(new Cut(i, holder, cut));
                }
            }
        }
        return cuts;
    }
}
