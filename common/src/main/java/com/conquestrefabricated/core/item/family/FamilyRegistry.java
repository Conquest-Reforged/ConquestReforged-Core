package com.conquestrefabricated.core.item.family;

import com.conquestrefabricated.core.item.family.block.BlockFamily;
import com.conquestrefabricated.core.item.family.item.ItemFamily;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class FamilyRegistry<T extends ItemLike> {

    public static final FamilyRegistry<Block> BLOCKS = new FamilyRegistry<>(BlockFamily.EMPTY);
    public static final FamilyRegistry<Item> ITEMS = new FamilyRegistry<>(ItemFamily.EMPTY);

    private final Family<T> empty;
    private final Map<Identifier, Family<T>> families = new HashMap<>();

    public FamilyRegistry(Family<T> empty) {
        this.empty = empty;
    }

    public void register(Family<T> family) {
        for (T member : family.getMembers()) {
            register(member, family);
        }
    }

    public void register(T member, Family<T> family) {
        if (family.isAbsent()) {
            return;
        }
        families.put(BuiltInRegistries.ITEM.getKey(member.asItem()), family);
    }

    public void registerToFamily(Identifier parent, T child) {
        Family<T> family = getFamily(parent);
        if (family.isPresent()) {
            family.add(child);
        }
    }

    public Family<T> getFamily(T member) {
        return getFamily(BuiltInRegistries.ITEM.getKey(member.asItem()));
    }

    public Family<T> getFamily(Identifier name) {
        return families.getOrDefault(name, empty);
    }

    public Stream<Family<T>> values() {
        return families.values().stream().distinct();
    }

    /**
     * Trims all registered families to size to save on unallocated array slots
     */
    public static void bake() {
        BLOCKS.values().forEach(Family::trim);
        ITEMS.values().forEach(Family::trim);
    }
}
