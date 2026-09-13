package com.conquestrefabricated.compat.rei;

import com.conquestrefabricated.content.arms.ArmsStation;
import com.conquestrefabricated.content.arms.ArmsStationRecipe;
import com.conquestrefabricated.content.loom.LoomStation;
import com.conquestrefabricated.content.loom.WeavingRecipe;
import com.conquestrefabricated.content.painting.PaintersKit;
import com.conquestrefabricated.content.painting.PaintingInput;
import com.conquestrefabricated.content.painting.PaintingRecipe;
import com.conquestrefabricated.content.pottery.PotteryRecipe;
import com.conquestrefabricated.content.pottery.PotteryWheelStation;
import com.conquestrefabricated.content.station.Stations;
import com.conquestrefabricated.content.tools.CraftingTool;
import com.conquestrefabricated.content.tools.CraftingTools;
import com.conquestrefabricated.content.tools.ToolCraftingRecipe;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.List;

/**
 * Turns Conquest's station recipes into REI displays, on the server.
 *
 * <p>This is the half that matters. REI builds its displays server-side, straight from the recipes,
 * and ships them to the client over its own sync channel - the client never sees a modded recipe, so
 * a client-only plugin has nothing to work from. {@link ConquestReiPlugin} draws them; this is what
 * produces them.</p>
 *
 * <p>Everything is read from {@link Stations}, so an addon registering its own set of crafting tools
 * is picked up with no code of its own.</p>
 */
public class ConquestReiCommonPlugin implements REICommonPlugin {

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        // A tool set is told apart by the id on the recipe, so all four share one filler.
        registry.<ToolCraftingRecipe, StationDisplay>beginRecipeFiller(ToolCraftingRecipe.class)
                .filterType(CraftingTools.RECIPE_TYPE)
                .fill(holder -> StationDisplay.of(holder, holder.value().input(),
                        holder.value().assemble(emptyInput()),
                        CraftingTools.get(holder.value().tool())
                                .map(Stations::of)
                                .orElseGet(() -> Stations.of(CraftingTool.of(holder.value().tool())))));

        registry.<WeavingRecipe, StationDisplay>beginRecipeFiller(WeavingRecipe.class)
                .filterType(LoomStation.RECIPE_TYPE)
                .fill(holder -> StationDisplay.of(holder, holder.value().input(),
                        holder.value().assemble(emptyInput()), Stations.LOOM));

        registry.<PotteryRecipe, StationDisplay>beginRecipeFiller(PotteryRecipe.class)
                .filterType(PotteryWheelStation.RECIPE_TYPE)
                .fill(holder -> StationDisplay.of(holder, holder.value().input(),
                        holder.value().assemble(emptyInput()), Stations.POTTERY_WHEEL));

        registry.<ArmsStationRecipe, StationDisplay>beginRecipeFiller(ArmsStationRecipe.class)
                .filterType(ArmsStation.RECIPE_TYPE)
                .fill(holder -> StationDisplay.of(holder, holder.value().input(),
                        holder.value().assemble(emptyInput()), Stations.ARMS));

        // The only station with two ingredients: a base and what is painted onto it.
        registry.<PaintingRecipe, StationDisplay>beginRecipeFiller(PaintingRecipe.class)
                .filterType(PaintersKit.RECIPE_TYPE)
                .fill(holder -> StationDisplay.of(holder,
                        List.of(holder.value().base(), holder.value().paint()),
                        holder.value().assemble(PaintingInput.EMPTY), Stations.PAINTERS_KIT));
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        // Without this the displays cannot cross to the client and are dropped in silence.
        registry.register(StationDisplay.SERIALIZER_ID, StationDisplay.SERIALIZER);
    }

    /** A station's result is fixed by the recipe, so an empty input is enough to ask what it makes. */
    private static SingleRecipeInput emptyInput() {
        return new SingleRecipeInput(net.minecraft.world.item.ItemStack.EMPTY);
    }
}
