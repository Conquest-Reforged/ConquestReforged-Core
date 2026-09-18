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
 * One thing a soaking barrel can do: leave an ingredient in the water, with an optional additive
 * dissolved in it first, and wait.
 *
 * <pre>{@code
 * {
 *   "type": "conquest:soaking",
 *   "ingredient": "conquest:raw_hide",
 *   "additive": "conquest:slaked_lime",
 *   "result": { "id": "conquest:limed_hide", "count": 1 },
 *   "time": 12000
 * }
 * }</pre>
 *
 * <p>{@code additive} is optional: without it the ingredient soaks in plain water. {@code time} is in
 * ticks and, as with every station recipe, defaults to {@link TimedStationRecipe#DEFAULT_TIME}. The
 * additive is used up when the soak starts; the water it was dissolved in is left as it was.</p>
 *
 * <p>Only the ingredient is single-item, so this is a {@link TimedStationRecipe} that carries one extra
 * field - the barrel matches recipes itself rather than through a {@link SingleRecipeInput}.</p>
 */
public class SoakingRecipe extends TimedStationRecipe {

    public static final MapCodec<SoakingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SoakingRecipe::input),
            Ingredient.CODEC.optionalFieldOf("additive").forGetter(recipe -> Optional.ofNullable(recipe.additive)),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(SoakingRecipe::templateResult),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(SoakingRecipe::time)
    ).apply(instance, (info, input, additive, result, time) ->
            new SoakingRecipe(info, input, additive.orElse(null), result, time)));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoakingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, SoakingRecipe::input,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, recipe -> Optional.ofNullable(recipe.additive),
            ItemStackTemplate.STREAM_CODEC, SoakingRecipe::templateResult,
            ByteBufCodecs.VAR_INT, SoakingRecipe::time,
            (info, input, additive, result, time) ->
                    new SoakingRecipe(info, input, additive.orElse(null), result, time));

    private final @Nullable Ingredient additive;

    public SoakingRecipe(CommonInfo commonInfo, Ingredient input, @Nullable Ingredient additive,
                         ItemStackTemplate result, int time) {
        super(commonInfo, input, result, time);
        this.additive = additive;
    }

    public SoakingRecipe(Ingredient input, @Nullable Ingredient additive, ItemStackTemplate result, int time) {
        this(new CommonInfo(false), input, additive, result, time);
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
}
