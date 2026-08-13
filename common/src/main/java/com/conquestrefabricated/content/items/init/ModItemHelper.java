package com.conquestrefabricated.content.items.init;

import com.conquestrefabricated.content.items.item.ArmorItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Function;

public final class ModItemHelper {
    private final String namespace;

    public ModItemHelper(String namespace) {
        this.namespace = namespace;
    }

    public Item.Properties props(String name) {
        return new Item.Properties().setId(
                ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(this.namespace, name)));
    }

    public <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
        T item = factory.apply(props(name));

        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(this.namespace, name), item);
    }

    public ArmorItem armor(String name, ArmorMaterial material, ArmorType type) {
        return register(name, properties -> new ArmorItem(material, type, properties.stacksTo(1)));
    }
}