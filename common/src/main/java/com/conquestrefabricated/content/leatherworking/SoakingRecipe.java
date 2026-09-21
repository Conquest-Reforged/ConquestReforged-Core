package com.conquestrefabricated.content.leatherworking;

import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.Stations;
import com.conquestrefabricated.content.station.TimedStationRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
 * One thing a soaking barrel can do: leave an ingredient in the water, with an optional additive
 * dissolved in it first, and wait.
 *
 * <pre>{@code
 * {
 *   "type": "conquest:soaking",
 *   "ingredient": "conquest:raw_hide",
 *   "additive": "conquest:slaked_lime",
 *   "result": { "id": "conquest:limed_hide", "count": 1 },
 *   "time": 12000,
 *   "water_color": "#f2f1ea"
 * }
 * }</pre>
 *
 * <p>{@code additive} is optional: without it the ingredient soaks in plain water. {@code time} is in
 * ticks and, as with every station recipe, defaults to {@link TimedStationRecipe#DEFAULT_TIME}. The
 * additive is used up when the soak starts; the water it was dissolved in is left as it was.</p>
 *
 * <p>{@code water_color} is optional too, a {@code "#rrggbb"} string (or a plain integer). It is what the
 * barrel's water is tinted while this recipe is at work: for a recipe with an {@code additive} it is the
 * colour the water takes on as soon as that additive is dissolved and it keeps through the soak, and for
 * a recipe without one it is the colour the soak itself gives the water. The tint multiplies the water
 * texture, so it can only darken it - choose light colours.</p>
 *
 * <p>Only the ingredient is single-item, so this is a {@link TimedStationRecipe} that carries extra
 * fields - the barrel matches recipes itself rather than through a {@link SingleRecipeInput}.</p>
 */
public class SoakingRecipe extends TimedStationRecipe {

    /** A water colour of nothing: the barrel's water keeps whatever it already was. */
    public static final int NO_COLOR = -1;

    private static final Codec<Integer> COLOR_CODEC = Codec.withAlternative(
            Codec.STRING.comapFlatMap(SoakingRecipe::parseColor, SoakingRecipe::formatColor),
            Codec.INT);

    public static final MapCodec<SoakingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SoakingRecipe::input),
            Ingredient.CODEC.optionalFieldOf("additive").forGetter(recipe -> Optional.ofNullable(recipe.additive)),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(SoakingRecipe::templateResult),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(SoakingRecipe::time),
            COLOR_CODEC.optionalFieldOf("water_color").forGetter(recipe ->
                    recipe.waterColor == NO_COLOR ? Optional.empty() : Optional.of(recipe.waterColor))
    ).apply(instance, (info, input, additive, result, time, color) ->
            new SoakingRecipe(info, input, additive.orElse(null), result, time, color.orElse(NO_COLOR))));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoakingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, SoakingRecipe::input,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, recipe -> Optional.ofNullable(recipe.additive),
            ItemStackTemplate.STREAM_CODEC, SoakingRecipe::templateResult,
            ByteBufCodecs.VAR_INT, SoakingRecipe::time,
            ByteBufCodecs.INT, SoakingRecipe::waterColor,
            (info, input, additive, result, time, color) ->
                    new SoakingRecipe(info, input, additive.orElse(null), result, time, color));

    private final @Nullable Ingredient additive;
    private final int waterColor;

    public SoakingRecipe(CommonInfo commonInfo, Ingredient input, @Nullable Ingredient additive,
                         ItemStackTemplate result, int time, int waterColor) {
        super(commonInfo, input, result, time);
        this.additive = additive;
        this.waterColor = waterColor;
    }

    public SoakingRecipe(Ingredient input, @Nullable Ingredient additive, ItemStackTemplate result, int time) {
        this(new CommonInfo(false), input, additive, result, time, NO_COLOR);
    }

    /** What has to be dissolved in the water first, if anything. */
    public Optional<Ingredient> additive() {
        return Optional.ofNullable(this.additive);
    }

    public boolean needsAdditive() {
        return this.additive != null;
    }

    /** Whether {@code stack} is the additive this recipe wants. False for a recipe with none. */
    public boolean acceptsAdditive(ItemStack stack) {
        return this.additive != null && this.additive.test(stack);
    }

    /** The colour the water takes on, as {@code 0xRRGGBB}, or {@link #NO_COLOR}. */
    public int waterColor() {
        return this.waterColor;
    }

    public boolean hasWaterColor() {
        return this.waterColor != NO_COLOR;
    }

    /** Everything a viewer should list on the input side: the ingredient, then the additive if any. */
    public List<Ingredient> ingredients() {
        return this.additive == null ? List.of(this.input()) : List.of(this.input(), this.additive);
    }

    /** What soaking {@code count} of the ingredient at once makes. */
    public ItemStack resultFor(int count) {
        ItemStack one = this.assemble(new SingleRecipeInput(ItemStack.EMPTY));
        return one.copyWithCount(one.getCount() * count);
    }

    @Override
    public RecipeType<SoakingRecipe> getType() {
        return LeatherworkingStations.SOAKING_TYPE;
    }

    @Override
    public RecipeSerializer<SoakingRecipe> getSerializer() {
        return LeatherworkingStations.SOAKING_SERIALIZER;
    }

    /**
     * What a recipe viewer is shown. Modded recipes never cross to the client, so this is the only
     * description of it that gets there.
     */
    @Override
    public List<RecipeDisplay> display() {
        return this.additive == null
                ? StationRecipeDisplay.of(this.input(), this.templateResult(), Stations.SOAKING)
                : StationRecipeDisplay.of(this.input(), this.additive, this.templateResult(), Stations.SOAKING);
    }

    private static DataResult<Integer> parseColor(String text) {
        String hex = text.startsWith("#") ? text.substring(1) : text;
        if (hex.length() != 6) {
            return DataResult.error(() -> "Not a #rrggbb colour: " + text);
        }
        try {
            return DataResult.success(Integer.parseInt(hex, 16));
        } catch (NumberFormatException e) {
            return DataResult.error(() -> "Not a #rrggbb colour: " + text);
        }
    }

    private static String formatColor(int color) {
        return String.format("#%06x", color & 0xFFFFFF);
    }
}
