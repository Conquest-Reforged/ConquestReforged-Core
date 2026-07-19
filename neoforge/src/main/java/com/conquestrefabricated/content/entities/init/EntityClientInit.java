package com.conquestrefabricated.content.entities.init;

import com.conquestrefabricated.content.entities.EntityTypes;
import com.conquestrefabricated.content.entities.seat.SeatRenderer;
import com.conquestrefabricated.content.entities.painting.PaintingEntityType;
import com.conquestrefabricated.content.entities.painting.render.PaintingRenderer;
import com.conquestrefabricated.content.entities.painting.render.VanillaPaintingRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;


@EventBusSubscriber(value = Dist.CLIENT)
public class EntityClientInit {

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        //custom vanilla painting renderer to allow cut-out rendering
        EntityRenderers.register(EntityType.PAINTING, VanillaPaintingRenderer::new);
        // conquest renderers
        EntityRenderers.register(PaintingEntityType.PAINTING, PaintingRenderer::new);
        EntityRenderers.register(EntityTypes.SEAT, SeatRenderer::new);
    }
}
