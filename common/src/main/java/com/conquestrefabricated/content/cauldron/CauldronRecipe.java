package com.conquestrefabricated.content.cauldron;

import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.Stations;
import com.conquestrefabricated.core.Namespaces;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * One thing a cauldron can cook: up to three ingredients, optionally some of what is in the cauldron,
 * over a fire, for a time, into something.
 *
 * <pre>{@code
 * {
 *   "type": "conquest:cauldron",
 *   "fluid": "sea_water",
 *   "fluid_use": 1,
 *   "result": { "id": "conquest:salt", "count": 2 },
 *   "time": 400
 * }
 * }</pre>
 *
 * <p>{@code ingredients} (zero to three, in any order, one of each used up) and {@code fluid} are each
 * optional but a recipe needs at least one. {@code fluid} is {@code water} or {@code sea_water}: what the
 * cauldron has to hold, {@code fluid_use} levels of which (default 1, of the three a cauldron holds) are
 * boiled away. {@code time} is in ticks of burning fuel.</p>
 *
 * <p>This is data, so a datapack adds to it: stews, rendering, dyeing, whatever a pot over a fire does.</p>
 */
public class CauldronRecipe implements Recipe<CauldronRecipe.Input> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "cauldron");

    public static final RecipeType<CauldronRecipe> TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return ID.toString();
        }
    };

    public static final int MAX_INGREDIENTS = 3;
    public static final int DEFAULT_TIME = 200;

    /** What a recipe wants in the cauldron besides items. */
    public enum Fluid implements StringRepresentable {
        NONE("none"),
        WATER("water"),
        SEA_WATER("sea_water");

        public static final Codec<Fluid> CODEC = StringRepresentable.fromEnum(Fluid::values);

        private final String name;

        Fluid(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    /**
     * What the cauldron holds, as a recipe sees it.
     *
     * @param stacks     the ingredient slots
     * @param fluidLevel how full it is, 0 to 3
     * @param brine      whether that is sea water
     */
    public record Input(List<ItemStack> stacks, int fluidLevel, boolean brine) implements RecipeInput {
        @Override
        public ItemStack getItem(int index) {
            return this.stacks.get(index);
        }

        @Override
        public int size() {
            return this.stacks.size();
        }
    }

    public static final MapCodec<CauldronRecipe> MAP_CODEC = RecordCodecBuilder.<CauldronRecipe>mapCodec(instance -> instance.group(
            Ingredient.CODEC.listOf(0, MAX_INGREDIENTS).optionalFieldOf("ingredients", List.of()).forGetter(CauldronRecipe::ingredients),
            Fluid.CODEC.optionalFieldOf("fluid", Fluid.NONE).forGetter(CauldronRecipe::fluid),
            Codec.intRange(0, 3).optionalFieldOf("fluid_use", 1).forGetter(CauldronRecipe::fluidUse),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(CauldronRecipe::template),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(CauldronRecipe::time)
    ).apply(instance, CauldronRecipe::new)).validate(recipe ->
            recipe.ingredients.isEmpty() && recipe.fluid == Fluid.NONE
                    ? DataResult.error(() -> "A cauldron recipe needs an ingredient or a fluid")
                    : DataResult.success(recipe));

    public static final StreamCodec<RegistryFriendlyByteBuf, CauldronRecipe> STREAM_CODEC = StreamCodec.of(
            (buffer, recipe) -> {
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, recipe.ingredients);
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.fluid.ordinal());
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.fluidUse);
                ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.result);
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.time);
            },
            buffer -> new CauldronRecipe(
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
                    Fluid.values()[ByteBufCodecs.VAR_INT.decode(buffer)],
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ItemStackTemplate.STREAM_CODEC.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer)));

    public static final RecipeSerializer<CauldronRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final List<Ingredient> ingredients;
    private final Fluid fluid;
    private final int fluidUse;
    private final ItemStackTemplate result;
    private final int time;

    public CauldronRecipe(List<Ingredient> ingredients, Fluid fluid, int fluidUse, ItemStackTemplate result, int time) {
        this.ingredients = List.copyOf(ingredients);
        this.fluid = fluid;
        this.fluidUse = fluid == Fluid.NONE ? 0 : fluidUse;
        this.result = result;
        this.time = Math.max(1, time);
    }

    public List<Ingredient> ingredients() {
        return this.ingredients;
    }

    public Fluid fluid() {
        return this.fluid;
    }

    public int fluidUse() {
        return this.fluidUse;
    }

    public int time() {
        return this.time;
    }

    private ItemStackTemplate template() {
        return this.result;
    }

    /** What one batch makes. */
    public ItemStack resultStack() {
        return this.result.create();
    }

    /**
     * Which slot supplies each ingredient - every ingredient needs a slot of its own - or null if the
     * contents do not make this.
     */
    public int @Nullable [] assign(Input input) {
        if (this.fluid != Fluid.NONE) {
            boolean right = this.fluid == Fluid.SEA_WATER ? input.brine() : !input.brine();
            if (input.fluidLevel() < Math.max(1, this.fluidUse) || !right) {
                return null;
            }
        }
        int[] slots = new int[this.ingredients.size()];
        boolean[] used = new boolean[input.size()];
        for (int wanted = 0; wanted < slots.length; wanted++) {
            slots[wanted] = -1;
            for (int slot = 0; slot < input.size(); slot++) {
                if (!used[slot] && !input.getItem(slot).isEmpty() && this.ingredients.get(wanted).test(input.getItem(slot))) {
                    used[slot] = true;
                    slots[wanted] = slot;
                    break;
                }
            }
            if (slots[wanted] < 0) {
                return null;
            }
        }
        return slots;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return this.assign(input) != null;
    }

    @Override
    public ItemStack assemble(Input input) {
        return this.resultStack();
    }

    @Override
    public RecipeType<CauldronRecipe> getType() {
        return TYPE;
    }

    @Override
    public RecipeSerializer<CauldronRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return Stations.RECIPE_BOOK_CATEGORY;
    }

    @Override
    public List<RecipeDisplay> display() {
        List<SlotDisplay> shown = new ArrayList<>();
        for (Ingredient ingredient : this.ingredients) {
            shown.add(ingredient.display());
        }
        return List.of(new StationRecipeDisplay(shown, new SlotDisplay.ItemStackSlotDisplay(this.result),
                new SlotDisplay.ItemSlotDisplay(Stations.CAULDRON.displayItem()), Stations.CAULDRON.id()));
    }
}
