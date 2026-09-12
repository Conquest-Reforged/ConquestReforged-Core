package com.conquestrefabricated.content.station;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
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
 *
 * <p>Everything here is generic over the recipe's input type: most stations work a single item, but
 * a painter's kit takes a base and a paint, and the lookups are the same either way.</p>
 */
public final class StationRecipes {

    private StationRecipes() {
    }

    /** Every recipe of {@code type} that passes {@code filter} and accepts {@code input}, in datapack order. */
    public static <I extends RecipeInput, R extends Recipe<I>> List<RecipeHolder<R>> recipesFor(
            Level level, RecipeType<R> type, Predicate<R> filter, I input) {

        if (input.isEmpty() || !(level.recipeAccess() instanceof RecipeManager recipes)) {
            return List.of();
        }

        return recipesFor(recipes.getRecipes(), type, filter, input, level);
    }

    /**
     * The world-free half of {@link #recipesFor(Level, RecipeType, Predicate, RecipeInput)}, so the
     * lookup can be exercised against a hand-built recipe list.
     *
     * @param level only handed to {@code matches}, which single-item recipes ignore
     */
    public static <I extends RecipeInput, R extends Recipe<I>> List<RecipeHolder<R>> recipesFor(
            Collection<RecipeHolder<?>> recipes, RecipeType<R> type, Predicate<R> filter,
            I input, Level level) {

        List<RecipeHolder<R>> matches = new ArrayList<>();
        for (RecipeHolder<?> holder : recipes) {
            if (holder.value().getType() != type) {
                continue;
            }
            @SuppressWarnings("unchecked")
            RecipeHolder<R> typed = (RecipeHolder<R>) holder;
            if (filter.test(typed.value()) && typed.value().matches(input, level)) {
                matches.add(typed);
            }
        }
        return matches;
    }

    /**
     * Every recipe of {@code type} passing {@code filter}, whether or not it matches anything.
     *
     * <p>For a station that offers more than its input can currently make - a painter's kit listing
     * what a base could become before any paint is in - the menu does its own narrowing.</p>
     */
    public static <I extends RecipeInput, R extends Recipe<I>> List<RecipeHolder<R>> allOf(
            Level level, RecipeType<R> type, Predicate<R> filter) {

        if (!(level.recipeAccess() instanceof RecipeManager recipes)) {
            return List.of();
        }

        List<RecipeHolder<R>> matches = new ArrayList<>();
        for (RecipeHolder<?> holder : recipes.getRecipes()) {
            if (holder.value().getType() != type) {
                continue;
            }
            @SuppressWarnings("unchecked")
            RecipeHolder<R> typed = (RecipeHolder<R>) holder;
            if (filter.test(typed.value())) {
                matches.add(typed);
            }
        }
        return matches;
    }

    /** Whether any matching recipe accepts {@code input}. Logical server only. */
    public static <I extends RecipeInput, R extends Recipe<I>> boolean isValidInput(
            Level level, RecipeType<R> type, Predicate<R> filter, I input) {

        return !recipesFor(level, type, filter, input).isEmpty();
    }

    /**
     * Whether any recipe of {@code type} passing {@code filter} produces {@code item}.
     *
     * <p>Lets a station recognise its own output coming back in: the blocks it made are things it
     * knows how to work, even though no recipe takes them as an ingredient.</p>
     *
     * @param probe an empty input to assemble against - a station's result is fixed by the recipe
     *              rather than by what is in the slots, so an empty one is enough to ask what it makes
     */
    public static <I extends RecipeInput, R extends Recipe<I>> boolean produces(
            Level level, RecipeType<R> type, Predicate<R> filter, ItemStack item, I probe) {

        if (item.isEmpty()) {
            return false;
        }
        for (RecipeHolder<R> holder : allOf(level, type, filter)) {
            if (holder.value().assemble(probe).getItem() == item.getItem()) {
                return true;
            }
        }
        return false;
    }
}
