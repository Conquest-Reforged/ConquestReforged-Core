package com.conquestrefabricated.content.station;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.SingleItemRecipe;

/**
 * A recipe for a workstation that takes time: one thing in, one thing out, and how long it takes.
 *
 * <p>Every workstation has its own recipe type - a loom weaves, a pottery wheel throws, and neither
 * should offer the other's recipes - but the shape on disk is the same, so it is written once here.
 * A subclass is little more than a name:</p>
 *
 * <pre>{@code
 * public class PotteryRecipe extends TimedStationRecipe {
 *     public static final MapCodec<PotteryRecipe> MAP_CODEC = mapCodec(PotteryRecipe::new);
 *     ...
 * }
 * }</pre>
 *
 * <p>{@code time} is optional and omitted when it is the default, so an ordinary recipe writes just
 * its ingredient and result. A time of zero makes a craft instant, which is what the picker's family
 * shapes use - reshaping is not making.</p>
 */
public abstract class TimedStationRecipe extends SingleItemRecipe {

    /** Ticks a craft takes when the recipe doesn't say. Five seconds, as an iron ingot smelts. */
    public static final int DEFAULT_TIME = 100;

    /** How a subclass is rebuilt from the fields on disk. */
    @FunctionalInterface
    public interface Factory<T extends TimedStationRecipe> {
        T create(CommonInfo commonInfo, Ingredient input, ItemStackTemplate result, int time);
    }

    public static <T extends TimedStationRecipe> MapCodec<T> mapCodec(Factory<T> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(TimedStationRecipe::input),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(TimedStationRecipe::templateResult),
                Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(TimedStationRecipe::time)
        ).apply(instance, factory::create));
    }

    public static <T extends TimedStationRecipe> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(Factory<T> factory) {
        return StreamCodec.composite(
                Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
                Ingredient.CONTENTS_STREAM_CODEC, TimedStationRecipe::input,
                ItemStackTemplate.STREAM_CODEC, TimedStationRecipe::templateResult,
                ByteBufCodecs.VAR_INT, TimedStationRecipe::time,
                factory::create);
    }

    private final int time;

    protected TimedStationRecipe(CommonInfo commonInfo, Ingredient input, ItemStackTemplate result, int time) {
        super(commonInfo, input, result);
        this.time = time;
    }

    /** How many ticks one craft takes. Zero is instant. */
    public int time() {
        return this.time;
    }

    /** {@link SingleItemRecipe#result()} is protected; the codecs above need it from out here. */
    protected ItemStackTemplate templateResult() {
        return this.result();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean isSpecial() {
        // Kept out of the recipe book: the station's own picker is where these are found.
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.STONECUTTER;
    }
}
