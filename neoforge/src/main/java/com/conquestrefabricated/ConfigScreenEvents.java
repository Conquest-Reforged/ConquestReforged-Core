package com.conquestrefabricated;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.gui.config.ConquestConfigScreen;
import com.conquestrefabricated.client.tutorial.neoforge.TutorialRenderEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

import static com.conquestrefabricated.RefabricatedMod.*;

@EventBusSubscriber(modid = "conquest", value = {Dist.CLIENT})
public class ConfigScreenEvents {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (container, screen) -> new ConquestConfigScreen(screen, ConquestConfig.INSTANCE)
        );

        NeoForge.EVENT_BUS.register(ForgeEventListener.class);
        NeoForge.EVENT_BUS.register(new TutorialRenderEvent());
    }

    public static class ForgeEventListener {
        @SubscribeEvent
        public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
            if (localPlantSlowness != null) {
                ConquestConfig.INSTANCE.plantSlowness.set(localPlantSlowness);
                ConquestConfig.INSTANCE.plantBreaking.set(localPlantBreaking);
                ConquestConfig.INSTANCE.passThroughLeaves.set(localPassThroughLeaves);

                ConquestConfig.INSTANCE.save();

                localPlantSlowness = null;
                localPlantBreaking = null;
                localPassThroughLeaves = null;
            }
        }
    }
}