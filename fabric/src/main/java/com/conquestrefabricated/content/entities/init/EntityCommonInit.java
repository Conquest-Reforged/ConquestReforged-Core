package com.conquestrefabricated.content.entities.init;

import com.conquestrefabricated.content.entities.EntityTypes;
import com.conquestrefabricated.content.entities.painting.ModPainting;
import com.conquestrefabricated.content.entities.painting.PaintingEntityType;
import java.util.stream.IntStream;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class EntityCommonInit {

    //@SubscribeEvent
    public static void entities() {
        registerPaintings();
    }

    private static void registerPaintings() {
        IntStream.range(0, 10).forEach(i -> ModPainting.register("painting" + i));
        EntityTypes.entityTypesInit();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, "conquest:painting", PaintingEntityType.PAINTING);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, "conquest:seat", EntityTypes.SEAT);
    }
}
