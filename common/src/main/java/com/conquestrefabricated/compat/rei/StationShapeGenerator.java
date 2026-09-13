package com.conquestrefabricated.compat.rei;

import com.conquestrefabricated.content.station.StationFamilies;
import com.conquestrefabricated.content.station.StationMenu;
import com.conquestrefabricated.content.station.Stations;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The shape toggle, as a recipe viewer sees it.
 *
 * <p>Family shapes are not recipes and there are no files behind them - a station reads the block
 * family directly, see {@link StationFamilies}. So there is nothing for a filler to convert, and
 * these are generated on demand instead: when a player asks how a slab is made, or what a block can
 * be turned into. That also keeps the cost at nothing until asked, which matters because writing one
 * display per shape of every family would run to tens of thousands of them.</p>
 *
 * <p>A station only offers shapes for material it would actually work - what its own recipes use,
 * plus whatever its {@code /shapes} tag opts in - so a set of woodworking tools does not offer to
 * cut granite. A painter's kit is the exception, and shapes only what it painted itself.</p>
 */
public class StationShapeGenerator implements DynamicDisplayGenerator<StationDisplay> {

    private final Stations.Station station;
    private final CategoryIdentifier<StationDisplay> category;
    private final boolean shapesItsOwnOutput;

    public StationShapeGenerator(Stations.Station station, boolean shapesItsOwnOutput) {
        this.station = station;
        this.category = StationCategory.identifierFor(station.id());
        this.shapesItsOwnOutput = shapesItsOwnOutput;
    }

    /** "How is this made?" - a shape is made by working the parent of its family. */
    @Override
    public Optional<List<StationDisplay>> getRecipeFor(EntryStack<?> entry) {
        ItemStack shape = asStack(entry);
        ItemStack parent = StationFamilies.parentOf(shape);
        if (parent.isEmpty() || !this.worksWith(parent)) {
            return Optional.empty();
        }
        // The yield is the family's, not this shape's stack size, so ask for it properly.
        for (ItemStack offered : StationFamilies.shapesOf(parent)) {
            if (offered.getItem() == shape.getItem()) {
                return Optional.of(List.of(this.display(parent, offered)));
            }
        }
        return Optional.empty();
    }

    /** "What can I do with this?" - everything else in its family. */
    @Override
    public Optional<List<StationDisplay>> getUsageFor(EntryStack<?> entry) {
        ItemStack parent = asStack(entry);
        if (parent.isEmpty() || !this.worksWith(parent)) {
            return Optional.empty();
        }
        List<ItemStack> shapes = StationFamilies.shapesOf(parent);
        if (shapes.isEmpty()) {
            return Optional.empty();
        }
        List<StationDisplay> displays = new ArrayList<>(shapes.size());
        for (ItemStack shape : shapes) {
            displays.add(this.display(parent, shape));
        }
        return Optional.of(displays);
    }

    private StationDisplay display(ItemStack parent, ItemStack shape) {
        return new StationDisplay(
                List.of(EntryIngredients.of(parent)),
                List.of(EntryIngredients.of(shape)),
                this.station.id(),
                Optional.empty());
    }

    /**
     * Whether this station would shape {@code material} at all.
     *
     * <p>Mirrors {@code StationMenu.worksWith} from the client side: the tag is synced, and what the
     * station's recipes make and take is read back off the displays registered for its category,
     * which is the only sight of them the client has.</p>
     */
    private boolean worksWith(ItemStack material) {
        if (material.is(StationMenu.shapesTagFor(this.station.id()))) {
            return true;
        }
        for (StationDisplay display : DisplayRegistry.getInstance().<StationDisplay>get(this.category)) {
            // What the station makes always counts; what it consumes counts too, unless the station
            // only shapes its own work - a painter's kit has no business cutting the cobblestone it
            // paints onto.
            List<EntryIngredient> side = new ArrayList<>(display.getOutputEntries());
            if (!this.shapesItsOwnOutput) {
                side.addAll(display.getInputEntries());
            }
            for (EntryIngredient ingredient : side) {
                for (EntryStack<?> stack : ingredient) {
                    if (EntryStacks.equalsFuzzy(stack, EntryStacks.of(material))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static ItemStack asStack(EntryStack<?> entry) {
        Object value = entry.getValue();
        return value instanceof ItemStack stack ? stack : ItemStack.EMPTY;
    }
}
