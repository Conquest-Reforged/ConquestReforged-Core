package com.conquestrefabricated.content.entities;

import com.conquestrefabricated.content.entities.seat.SeatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityTypes {

    public static void entityTypesInit() {

    }

    public static final EntityType<SeatEntity> SEAT = build(
            "seat",
            EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0, 0)
    );

    private static <T extends Entity> EntityType<T> build(String name, EntityType.Builder<T> builder) {
        ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("conquest", name));
        return builder.build(key);
    }
}
