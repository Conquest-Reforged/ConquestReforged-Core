package com.conquestrefabricated.content.entities.init;

import com.conquestrefabricated.content.entities.EntityTypes;
import com.conquestrefabricated.content.entities.painting.ModPainting;
import com.conquestrefabricated.content.entities.painting.PaintingEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.stream.IntStream;

@EventBusSubscriber(modid = "conquest")
public class EntityCommonInit {

    @SubscribeEvent
    public static void entities(RegisterEvent event) {
        event.register(BuiltInRegistries.ENTITY_TYPE.key(), EntityCommonInit::registerPaintings);

    }

    private static void registerPaintings(RegisterEvent.RegisterHelper<EntityType<?>> entityTypeRegisterHelper) {
        IntStream.range(0, 10).forEach(i -> ModPainting.register("painting" + i));
        EntityTypes.entityTypesInit();
        PaintingEntityType.paintingEntityTypesInit();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, "conquest:painting", PaintingEntityType.PAINTING);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, "conquest:seat", EntityTypes.SEAT);

        //entityTypeRegisterHelper.register("painting", PaintingEntityType.PAINTING);
        //entityTypeRegisterHelper.register("seat", EntityTypes.SEAT);
    }
}
