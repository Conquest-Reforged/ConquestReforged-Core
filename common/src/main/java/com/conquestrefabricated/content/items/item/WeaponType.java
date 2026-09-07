package com.conquestrefabricated.content.items.item;

import net.minecraft.core.HolderGetter;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * What kind of weapon a registered item is, and what you feed the arms station to get one.
 *
 * <p>Armour carries its slot on {@link ArmorItem}, but weapons are plain {@code Item}s built out of
 * component-laden {@code Item.Properties}, and components are not bound while data is being
 * generated - {@code Item.components()} throws there. So the kind is recorded at registration
 * instead, by {@code ModItemHelper}'s weapon methods, and read back by the recipe datagen.</p>
 */
public enum WeaponType {

    /** Any sword reforges into one. */
    SWORD(ItemTags.SWORDS),
    /** Any axe reforges into one. */
    AXE(ItemTags.AXES),
    /** Polearms, from vanilla's spear tag. */
    SPEAR(ItemTags.SPEARS),
    BOW(Items.BOW),
    CROSSBOW(Items.CROSSBOW),
    SHIELD(Items.SHIELD);

    private static final Map<Item, WeaponType> DECLARED = new IdentityHashMap<>();

    private final @Nullable TagKey<Item> tag;
    private final @Nullable ItemLike item;

    WeaponType(TagKey<Item> tag) {
        this.tag = tag;
        this.item = null;
    }

    WeaponType(ItemLike item) {
        this.tag = null;
        this.item = item;
    }

    /**
     * Records that {@code item} is a weapon of this kind. Called for you by {@code ModItemHelper};
     * call it directly only if you register weapons some other way.
     */
    public static void declare(Item item, WeaponType type) {
        DECLARED.put(item, type);
    }

    /** What kind of weapon {@code item} was registered as, if it was registered as one at all. */
    public static Optional<WeaponType> of(Item item) {
        return Optional.ofNullable(DECLARED.get(item));
    }

    /**
     * The arms station input that makes this kind of weapon.
     *
     * @param items the generator's item lookup, for resolving a tag
     */
    public Ingredient input(HolderGetter<Item> items) {
        return this.tag != null ? Ingredient.of(items.getOrThrow(this.tag)) : Ingredient.of(this.item);
    }
}
