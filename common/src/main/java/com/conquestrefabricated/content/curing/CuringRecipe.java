package com.conquestrefabricated.content.curing;

import com.conquestrefabricated.content.station.StationRecipeDisplay;
import com.conquestrefabricated.content.station.Stations;
import com.conquestrefabricated.content.station.TimedStationRecipe;
import com.conquestrefabricated.core.Namespaces;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * One thing a packing box can do: pack goods in a medium - salt, usually - or put them up in jars, and
 * leave them.
 *
 * <pre>{@code
 * {
 *   "type": "conquest:curing",
 *   "ingredient": "minecraft:beef",
 *   "medium": "#conquest:salts",
 *   "result": { "id": "conquest:salt_beef" },
 *   "time": 24000
 * }
 * }</pre>
 *
 * <p>{@code medium} is what the goods are packed in; any item can be one, so the same box does cold
 * storage in snow or drying in ash as well as salting. {@code items_per_medium} (default 1) is how many
 * items one of it will do. A recipe that names a {@code vessel} puts each item up in one of those - a
 * jar - which is used up and comes out as part of the {@code result}. {@code time} is in ticks.</p>
 *
 * <p>The box counts time by the world clock, so a batch keeps curing while its chunk is not loaded.</p>
 */
public class CuringRecipe extends TimedStationRecipe {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "curing");

    public static final RecipeType<CuringRecipe> TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return ID.toString();
        }
    };

    public static final MapCodec<CuringRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CuringRecipe::input),
            Ingredient.CODEC.fieldOf("medium").forGetter(CuringRecipe::medium),
            Ingredient.CODEC.optionalFieldOf("vessel").forGetter(recipe -> Optional.ofNullable(recipe.vessel)),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(CuringRecipe::templateResult),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(CuringRecipe::time),
            Codec.intRange(1, 64).optionalFieldOf("items_per_medium", 1).forGetter(CuringRecipe::itemsPerMedium)
    ).apply(instance, (info, input, medium, vessel, result, time, perMedium) ->
            new CuringRecipe(info, input, medium, vessel.orElse(null), result, time, perMedium)));

    public static final StreamCodec<RegistryFriendlyByteBuf, CuringRecipe> STREAM_CODEC = StreamCodec.of(
            (buffer, recipe) -> {
                Recipe.CommonInfo.STREAM_CODEC.encode(buffer, recipe.commonInfo);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input());
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.medium);
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.encode(buffer, Optional.ofNullable(recipe.vessel));
                ItemStackTemplate.STREAM_CODEC.encode(buffer, recipe.templateResult());
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.time());
                ByteBufCodecs.VAR_INT.encode(buffer, recipe.itemsPerMedium);
            },
            buffer -> new CuringRecipe(
                    Recipe.CommonInfo.STREAM_CODEC.decode(buffer),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                    Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC.decode(buffer).orElse(null),
                    ItemStackTemplate.STREAM_CODEC.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer),
                    ByteBufCodecs.VAR_INT.decode(buffer)));

    public static final RecipeSerializer<CuringRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Ingredient medium;
    private final @Nullable Ingredient vessel;
    private final int itemsPerMedium;

    public CuringRecipe(CommonInfo commonInfo, Ingredient input, Ingredient medium, @Nullable Ingredient vessel,
                        ItemStackTemplate result, int time, int itemsPerMedium) {
        super(commonInfo, input, result, time);
        this.medium = medium;
        this.vessel = vessel;
        this.itemsPerMedium = itemsPerMedium;
    }

    /** What the goods are packed in. */
    public Ingredient medium() {
        return this.medium;
    }

    public boolean acceptsMedium(ItemStack stack) {
        return this.medium.test(stack);
    }

    /** The jar each item goes into, if this is pickling. */
    public Optional<Ingredient> vessel() {
        return Optional.ofNullable(this.vessel);
    }

    public boolean needsVessel() {
        return this.vessel != null;
    }

    public boolean acceptsVessel(ItemStack stack) {
        return this.vessel != null && this.vessel.test(stack);
    }

    public int itemsPerMedium() {
        return this.itemsPerMedium;
    }

    /** How much of the medium it takes to do {@code items} items. */
    public int mediumFor(int items) {
        return (items + this.itemsPerMedium - 1) / this.itemsPerMedium;
    }

    /** What curing {@code count} of the ingredient at once makes. */
    public ItemStack resultFor(int count) {
        ItemStack one = this.assemble(new SingleRecipeInput(ItemStack.EMPTY));
        return one.copyWithCount(one.getCount() * count);
    }

    @Override
    public RecipeType<CuringRecipe> getType() {
        return TYPE;
    }

    @Override
    public RecipeSerializer<CuringRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of(new StationRecipeDisplay(
                this.vessel == null ? List.of(this.input().display(), this.medium.display())
                        : List.of(this.input().display(), this.medium.display(), this.vessel.display()),
                new SlotDisplay.ItemStackSlotDisplay(this.templateResult()),
                new SlotDisplay.ItemSlotDisplay(Stations.PACKING_BOX.displayItem()), Stations.PACKING_BOX.id()));
    }
}
