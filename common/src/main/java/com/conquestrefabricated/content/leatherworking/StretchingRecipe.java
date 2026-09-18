package com.conquestrefabricated.content.leatherworking;

import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.Stations;
import com.conquestrefabricated.content.station.TimedStationRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * One step of working something on a tanning frame. A step is either applied by hand or runs on its
 * own, and the presence of a {@code tool} says which:
 *
 * <pre>{@code
 * // a manual step: mount the hide, then use a scraper on the frame. Instant; the tool takes wear.
 * {
 *   "type": "conquest:stretching",
 *   "ingredient": "conquest:limed_hide",
 *   "tool": "#conquest:hide_scrapers",
 *   "result": { "id": "conquest:scraped_hide", "count": 1 }
 * }
 *
 * // a timed step: nothing to do but wait. It starts the moment the ingredient is on the frame.
 * {
 *   "type": "conquest:stretching",
 *   "ingredient": "conquest:scraped_hide",
 *   "result": { "id": "conquest:parchment", "count": 1 },
 *   "time": 12000
 * }
 * }</pre>
 *
 * <p>A manual step ignores {@code time}. Steps chain: what one makes is what the next looks for, so a
 * finished timed step can go straight on to another, or stop and wait for a tool.</p>
 */
public class StretchingRecipe extends TimedStationRecipe {

    public static final MapCodec<StretchingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(StretchingRecipe::input),
            Ingredient.CODEC.optionalFieldOf("tool").forGetter(recipe -> Optional.ofNullable(recipe.tool)),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(StretchingRecipe::templateResult),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(StretchingRecipe::time)
    ).apply(instance, (info, input, tool, result, time) ->
            new StretchingRecipe(info, input, tool.orElse(null), result, time)));

    public static final StreamCodec<RegistryFriendlyByteBuf, StretchingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, StretchingRecipe::input,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, recipe -> Optional.ofNullable(recipe.tool),
            ItemStackTemplate.STREAM_CODEC, StretchingRecipe::templateResult,
            ByteBufCodecs.VAR_INT, StretchingRecipe::time,
            (info, input, tool, result, time) ->
                    new StretchingRecipe(info, input, tool.orElse(null), result, time));

    private final @Nullable Ingredient tool;

    public StretchingRecipe(CommonInfo commonInfo, Ingredient input, @Nullable Ingredient tool,
                            ItemStackTemplate result, int time) {
        super(commonInfo, input, result, time);
        this.tool = tool;
    }

    public StretchingRecipe(Ingredient input, @Nullable Ingredient tool, ItemStackTemplate result, int time) {
        this(new CommonInfo(false), input, tool, result, time);
    }

    /** The tool that applies this step, or empty for one that runs on its own. */
    public Optional<Ingredient> tool() {
        return Optional.ofNullable(this.tool);
    }

    /** Whether this step is applied with a tool rather than left to run. */
    public boolean isManual() {
        return this.tool != null;
    }

    /** Whether {@code stack} applies this step. False for a timed step. */
    public boolean acceptsTool(ItemStack stack) {
        return this.tool != null && this.tool.test(stack);
    }

    /** What one step makes. */
    public ItemStack made() {
        return this.assemble(new SingleRecipeInput(ItemStack.EMPTY));
    }

    /** Everything a viewer should list on the input side: the ingredient, then the tool if any. */
    public List<Ingredient> ingredients() {
        return this.tool == null ? List.of(this.input()) : List.of(this.input(), this.tool);
    }

    @Override
    public RecipeType<StretchingRecipe> getType() {
        return LeatherworkingStations.STRETCHING_TYPE;
    }

    @Override
    public RecipeSerializer<StretchingRecipe> getSerializer() {
        return LeatherworkingStations.STRETCHING_SERIALIZER;
    }

    @Override
    public List<RecipeDisplay> display() {
        return this.tool == null
                ? StationRecipeDisplay.of(this.input(), this.templateResult(), Stations.STRETCHING)
                : StationRecipeDisplay.of(this.input(), this.tool, this.templateResult(), Stations.STRETCHING);
    }
}
