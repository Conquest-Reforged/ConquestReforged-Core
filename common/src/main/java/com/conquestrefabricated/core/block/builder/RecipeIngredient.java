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
     * The item tag Core has to generate for this ingredient, if it needs one.
     *
     * <p>A recipe can only ever match on an <i>item</i> tag, so a block tag ingredient names one that
     * has to be filled in from the blocks carrying the block tag. {@code ModItemTagProvider} reads
     * this to know what to generate.</p>
     */
    default Optional<BlockTagMirror> mirror() {
        return Optional.empty();
    }

    /**
     * An item tag Core generates from a block tag, so a recipe can match on it.
     *
     * @param source    the block tag as it was declared
     * @param target    the item tag the recipe actually names
     * @param basesOnly whether only the families' parent blocks belong in it
     */
    record BlockTagMirror(TagKey<Block> source, TagKey<Item> target, boolean basesOnly) {
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
            return new OfBlockTag(tag.cast(Registries.BLOCK).orElseThrow(), false);
        }
        throw new IllegalArgumentException(
                "A crafting ingredient must be an item or block tag, got " + tag.registry().identifier() + " tag " + tag.location());
    }

    static RecipeIngredient of(Identifier id) {
        return new OfId(id);
    }

    /**
     * Only the blocks a family is built from - the cube, not the slab, stairs or wall cut from it.
     *
     * <p>{@code Props} are shared by every member of a family, so a block tag holds the whole family:
     * {@code ModTags.BRICKS} is every brick <i>shape</i>, not every brick. That is right for an
     * ingredient like "any log shape I have lying around", and wrong for one like "any brick",
     * where being able to feed a slab back in is just a way to lose material.</p>
     *
     * <pre>{@code
     * .craftedWith(CraftingTools.MASON.id(), RecipeIngredient.basesOf(ModTags.BRICKS))
     * }</pre>
     *
     * <p>Core generates a second item tag for these, alongside the full one, so both readings of a
     * block tag can be used by different recipes.</p>
     */
    static RecipeIngredient basesOf(TagKey<Block> blockTag) {
        return new OfBlockTag(blockTag, true);
    }

    /** Appended to a block tag's path for the parents-only item tag cut from it. */
    String BASES_SUFFIX = "/bases";

    /**
     * The item tag a block tag ingredient is written out as: the same id, in the item registry.
     *
     * <p>Kept in one place so the recipes and the item tags Core generates for them cannot drift
     * apart - the two have to agree exactly or the recipe matches nothing.</p>
     */
    static TagKey<Item> itemTagFor(TagKey<Block> blockTag) {
        return TagKey.create(Registries.ITEM, blockTag.location());
    }

    /** As {@link #itemTagFor}, for the {@link #basesOf} cut of a block tag. */
    static TagKey<Item> baseItemTagFor(TagKey<Block> blockTag) {
        Identifier id = blockTag.location();
        return TagKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(id.getNamespace(), id.getPath() + BASES_SUFFIX));
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
     * <p>Written out as an item tag, since that is all a recipe can match on. For a vanilla tag that
     * counterpart usually exists already; for one of Conquest's own, Core generates it - see
     * {@link #mirror()}.</p>
     *
     * @param basesOnly whether to take only the families' parent blocks - see {@link #basesOf}
     */
    record OfBlockTag(TagKey<Block> tag, boolean basesOnly) implements RecipeIngredient {
        @Override
        public Ingredient toIngredient(HolderGetter<Item> items) {
            return Ingredient.of(items.getOrThrow(itemTag()));
        }

        /** The item tag this is written out as. */
        public TagKey<Item> itemTag() {
            return this.basesOnly ? baseItemTagFor(this.tag) : itemTagFor(this.tag);
        }

        @Override
        public Optional<BlockTagMirror> mirror() {
            return Optional.of(new BlockTagMirror(this.tag, itemTag(), this.basesOnly));
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
