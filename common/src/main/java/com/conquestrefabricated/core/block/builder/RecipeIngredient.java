package com.conquestrefabricated.core.block.builder;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * What goes into a crafting station's input slot, named in whichever way is handiest where the block
 * is declared.
 *
 * <p>Most Conquest blocks have no static field to point at - they are built from templates rather
 * than declared one by one - so an ingredient can be named by id or, more usefully, by the block tag
 * the whole family already carries:</p>
 *
 * <pre>{@code
 * .craftedWith(CraftingTools.MASON.id(), Blocks.GRANITE)   // a vanilla block
 * .craftedWith(CraftingTools.MASON.id(), ModTags.STONE)    // a block tag
 * .craftedWith(CraftingTools.MASON.id(), "granite_ashlar") // one of ours, by id
 * }</pre>
 *
 * <p>Nothing is resolved here. Blocks are built long before tags are bound and long before the
 * generator's registries exist, so each of these only turns into an {@link Ingredient} when
 * {@link #toIngredient} is called at data generation time.</p>
 *
 * @see ToolRecipeSpec
 * @see WeavingRecipeSpec
 */
public sealed interface RecipeIngredient {

    /**
     * Resolves this into a real ingredient.
     *
     * @param items the generator's item lookup, for resolving a tag
     * @throws IllegalStateException if it names something that does not exist
     */
    Ingredient toIngredient(HolderGetter<Item> items);

    /**
     * The block tag this names, if it names one.
     *
     * <p>A recipe can only ever match on an <i>item</i> tag, so a block tag is written out as the
     * item tag with the same id - and that item tag has to exist for the recipe to match anything.
     * Core's item tag generation reads this to know which ones to mirror.</p>
     */
    default Optional<TagKey<Block>> blockTag() {
        return Optional.empty();
    }

    static RecipeIngredient of(ItemLike item) {
        return new OfItem(item);
    }

    /**
     * A tag of either items or blocks.
     *
     * <p>Deliberately one method taking {@code TagKey<?>} rather than an overload per registry:
     * {@code TagKey<Item>} and {@code TagKey<Block>} erase to the same signature, so they cannot be
     * overloads, and a call site would otherwise have to say which it meant.</p>
     *
     * @throws IllegalArgumentException if the tag belongs to some other registry
     */
    static RecipeIngredient of(TagKey<?> tag) {
        if (tag.isFor(Registries.ITEM)) {
            return new OfItemTag(tag.cast(Registries.ITEM).orElseThrow());
        }
        if (tag.isFor(Registries.BLOCK)) {
            return new OfBlockTag(tag.cast(Registries.BLOCK).orElseThrow());
        }
        throw new IllegalArgumentException(
                "A crafting ingredient must be an item or block tag, got " + tag.registry().identifier() + " tag " + tag.location());
    }

    static RecipeIngredient of(Identifier id) {
        return new OfId(id);
    }

    /**
     * The item tag a block tag ingredient is written out as: the same id, in the item registry.
     *
     * <p>Kept in one place so the recipes and the item tags Core generates for them cannot drift
     * apart - the two have to agree exactly or the recipe matches nothing.</p>
     */
    static TagKey<Item> itemTagFor(TagKey<Block> blockTag) {
        return TagKey.create(Registries.ITEM, blockTag.location());
    }

    /**
     * An item or block by id. A bare path resolves against {@link Namespaces#DEFAULT}, so
     * {@code "granite_ashlar"} and {@code "conquest:granite_ashlar"} mean the same thing, while
     * {@code "minecraft:stone"} reaches outside.
     */
    static RecipeIngredient of(String id) {
        return of(Namespaces.id(id));
    }

    /** A single block or item there is a static reference for. */
    record OfItem(ItemLike item) implements RecipeIngredient {
        @Override
        public Ingredient toIngredient(HolderGetter<Item> items) {
            return Ingredient.of(this.item);
        }
    }

    /** Anything in an item tag. */
    record OfItemTag(TagKey<Item> tag) implements RecipeIngredient {
        @Override
        public Ingredient toIngredient(HolderGetter<Item> items) {
            return Ingredient.of(items.getOrThrow(this.tag));
        }
    }

    /**
     * Anything in a block tag, which is what most Conquest families are grouped by.
     *
     * <p>Written out as the item tag with the same id, since that is all a recipe can match on. For
     * a vanilla tag that counterpart already exists; for one of Conquest's own, Core generates it -
     * see {@link #blockTag()}.</p>
     */
    record OfBlockTag(TagKey<Block> tag) implements RecipeIngredient {
        @Override
        public Ingredient toIngredient(HolderGetter<Item> items) {
            return Ingredient.of(items.getOrThrow(itemTag()));
        }

        /** The item tag this is written out as. */
        public TagKey<Item> itemTag() {
            return itemTagFor(this.tag);
        }

        @Override
        public Optional<TagKey<Block>> blockTag() {
            return Optional.of(this.tag);
        }
    }

    /** A single block or item named by id, for the many that have no static reference. */
    record OfId(Identifier id) implements RecipeIngredient {
        @Override
        public Ingredient toIngredient(HolderGetter<Item> items) {
            // Loud rather than silent: an id that resolves to nothing would otherwise be written out
            // as a recipe that can never match, and nothing would say why.
            Item item = BuiltInRegistries.ITEM.getOptional(this.id).orElseThrow(() -> new IllegalStateException(
                    "No item '" + this.id + "' to craft from. Check the id, and that whatever registers"
                            + " it is on the classpath of the module running data generation."));
            return Ingredient.of(item);
        }
    }
}
