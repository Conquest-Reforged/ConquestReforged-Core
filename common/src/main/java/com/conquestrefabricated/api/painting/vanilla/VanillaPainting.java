package com.conquestrefabricated.api.painting.vanilla;

import com.conquestrefabricated.api.painting.Painting;
import com.conquestrefabricated.api.painting.art.Art;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;

public class VanillaPainting implements Painting {

    public static final Painting INSTANCE = new VanillaPainting();

    private final Identifier name = Identifier.parse("conquest:vanilla_painting");
    private final Identifier itemName = Identifier.withDefaultNamespace("painting");

    @Override
    public String getName() {
        return "Vanilla";
    }

    @Override
    public String getTranslationKey() {
        return "";
    }

    @Override
    public Identifier getRegistryName() {
        return name;
    }

    @Override
    public Identifier getItemName() {
        return itemName;
    }

    @Override
    public ItemStack createStack(Art art, int count) {
        ItemStack stack = new ItemStack(Items.PAINTING, count);

        PaintingVariant variant = (PaintingVariant) art.getReference();
        Registry<PaintingVariant> registry = Minecraft.getInstance().level
                .registryAccess()
                .lookupOrThrow(Registries.PAINTING_VARIANT);
        Identifier variantId = registry.getKey(variant);

        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", "minecraft:painting");
        nbt.putString("variant", variantId.toString());

        stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(EntityType.PAINTING, nbt));
        return stack;
    }

    public static Painting fromName(String name) {
        return INSTANCE;
    }
}