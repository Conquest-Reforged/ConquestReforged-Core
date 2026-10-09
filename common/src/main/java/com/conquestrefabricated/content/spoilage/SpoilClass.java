package com.conquestrefabricated.content.spoilage;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * What kind of perishable an item is, which decides how long it keeps and where it keeps best. An item is
 * a member by being in the tag {@code conquest:spoilage/<name>}, so packs can add to or remove from any
 * class without touching code.
 */
public enum SpoilClass {
    /** Fruit, vegetables, mushrooms. Keep best somewhere cool. */
    PRODUCE("produce", 10, false),
    /** Wheat, barley, flour. Keeps for months if it stays dry and aired - an attic - and goes bad in a damp cellar. */
    GRAIN("grain", 60, true),
    /** Raw and cooked meat. */
    MEAT("meat", 4, false),
    /** Fish and shellfish. */
    FISH("fish", 3, false),
    /** Milk, cheese, eggs. */
    DAIRY("dairy", 8, false),
    /** Bread, pies, cakes. */
    BAKED("baked", 7, false),
    /** Cured, pickled, fermented: salt did the work. */
    PRESERVED("preserved", 90, false);

    public static final long DAY = 24000L;

    private final TagKey<Item> tag;
    private final long shelfLife;
    private final boolean prefersAttic;

    SpoilClass(String name, int days, boolean prefersAttic) {
        this.tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "spoilage/" + name));
        this.shelfLife = days * DAY;
        this.prefersAttic = prefersAttic;
    }

    public TagKey<Item> tag() {
        return this.tag;
    }

    /** Ticks it takes to rot completely in ordinary conditions: a temperate biome, in the open. */
    public long shelfLife() {
        return this.shelfLife;
    }

    /** True for a dry, aired store (an attic) as the best place; false for a cool one (a cellar). */
    public boolean prefersAttic() {
        return this.prefersAttic;
    }
}
