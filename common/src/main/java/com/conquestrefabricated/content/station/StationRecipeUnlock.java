package com.conquestrefabricated.content.station;

import com.conquestrefabricated.content.arms.ArmsStation;
import com.conquestrefabricated.content.loom.LoomStation;
import com.conquestrefabricated.content.painting.PaintersKit;
import com.conquestrefabricated.content.pottery.PotteryWheelStation;
import com.conquestrefabricated.content.tools.CraftingTools;
import com.conquestrefabricated.core.util.log.Log;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Hands every station recipe to a player as they join.
 *
 * <p>This is what gets them to the client at all. A recipe's {@link Recipe#display()} is only sent
 * once the recipe is in the player's book - {@code ServerRecipeBook.addRecipes} is the one place that
 * resolves displays and sends them - and nothing else would ever put these there, since they have no
 * unlock advancements and are not meant to be discovered. Without this a recipe viewer sees nothing,
 * because a modded recipe has no other road to the client.</p>
 *
 * <p>They still do not show up in the vanilla recipe book: they are filed under a category of our own
 * that no vanilla screen draws. See {@link Stations#RECIPE_BOOK_CATEGORY}.</p>
 *
 * <p>Only new recipes are sent, so this costs nothing after the first join - the book is saved with
 * the player, and what is already known is re-sent by vanilla on its own.</p>
 */
public final class StationRecipeUnlock {

    private StationRecipeUnlock() {
    }

    /** Every recipe type whose recipes a station offers. */
    private static Set<RecipeType<?>> types() {
        return Set.of(
                CraftingTools.RECIPE_TYPE,
                LoomStation.RECIPE_TYPE,
                PotteryWheelStation.RECIPE_TYPE,
                PaintersKit.RECIPE_TYPE,
                ArmsStation.RECIPE_TYPE);
    }

    public static void register() {
        PlayerEvent.PLAYER_JOIN.register(StationRecipeUnlock::award);
    }

    private static void award(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return;
        }

        Set<RecipeType<?>> ours = types();
        int total = 0;
        List<RecipeHolder<?>> recipes = new ArrayList<>();
        for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
            total++;
            if (ours.contains(holder.value().getType())) {
                recipes.add(holder);
            }
        }

        int added = recipes.isEmpty() ? 0 : player.getRecipeBook().addRecipes(recipes, player);
        Log.debug("Stations: {} of {} recipes belong to a station, {} newly known by {}",
                recipes.size(), total, added, player.getGameProfile().name());
    }
}
