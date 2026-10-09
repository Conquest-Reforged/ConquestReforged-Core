package com.conquestrefabricated.content.spoilage.fabric;

import com.conquestrefabricated.content.cauldron.CauldronMenu;
import com.conquestrefabricated.content.cauldron.CauldronRecipe;
import com.conquestrefabricated.content.cauldron.CauldronStore;
import com.conquestrefabricated.content.curing.CuringRecipe;
import com.conquestrefabricated.content.curing.PackingBoxMenu;
import dev.architectury.event.events.common.TickEvent;
import com.conquestrefabricated.content.spoilage.Spoilage;
import com.conquestrefabricated.content.spoilage.SpoilageStamps;
import com.conquestrefabricated.content.spoilage.SpoilageStorage;
import com.conquestrefabricated.content.spoilage.StoragePace;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import com.conquestrefabricated.content.spoilage.SpoilageTicker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/** Fabric-side registration for food spoilage: the component, the merge recipe, and the container tracking. */
public final class SpoilageInit {

    private SpoilageInit() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Spoilage.ID, Spoilage.FRESHNESS);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, com.conquestrefabricated.content.spoilage.Ripening.INTO_ID,
                com.conquestrefabricated.content.spoilage.Ripening.INTO);
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, com.conquestrefabricated.content.spoilage.Ripening.RIPENESS_ID,
                com.conquestrefabricated.content.spoilage.Ripening.RIPENESS);

        Registry.register(BuiltInRegistries.RECIPE_TYPE, CuringRecipe.ID, CuringRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CuringRecipe.ID, CuringRecipe.SERIALIZER);

        Registry.register(BuiltInRegistries.RECIPE_TYPE, CauldronRecipe.ID, CauldronRecipe.TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, CauldronRecipe.ID, CauldronRecipe.SERIALIZER);
        Registry.register(BuiltInRegistries.MENU, CauldronMenu.ID, CauldronMenu.create());
        Registry.register(BuiltInRegistries.MENU, PackingBoxMenu.ID, PackingBoxMenu.create());
        TickEvent.SERVER_LEVEL_POST.register(level -> CauldronStore.of(level).tick(level));

        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((entity, world) -> SpoilageTicker.track(entity));
        ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((entity, world) -> SpoilageTicker.untrack(entity));
        SpoilageStamps.use(new FabricSpoilageStamps());
        SpoilageTicker.register();
        PayloadTypeRegistry.clientboundPlay().register(StoragePace.ID, StoragePace.CODEC);
        SpoilageStorage.useSender(ServerPlayNetworking::send);
        SpoilageStorage.register();
    }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(StoragePace.ID,
                (payload, context) -> context.client().execute(() -> com.conquestrefabricated.content.spoilage.SpoilageClient.setStorage(payload)));
    }
}
