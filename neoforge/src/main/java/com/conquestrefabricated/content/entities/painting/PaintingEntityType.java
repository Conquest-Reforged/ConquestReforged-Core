package com.conquestrefabricated.content.entities.painting;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class PaintingEntityType {
    public static void paintingEntityTypesInit() {

    }

    public static final EntityType<EntityPainting> PAINTING = build(
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("conquest", "painting")),
            EntityType.Builder.<EntityPainting>of(EntityPainting::new, MobCategory.MISC)
    );

    private static <T extends Entity> EntityType<T> build(ResourceKey<EntityType<?>> key, EntityType.Builder<T> builder) {
        return builder.build(key);
    }
}
