package com.conquestrefabricated.content.blocks.init.neoforge;

import com.conquestrefabricated.content.blocks.init.BlockFamilyInit;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class BlockFamilyInitEvent {

    @SubscribeEvent
    public static void onAddClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(
                Identifier.fromNamespaceAndPath("conquest", "block_family_loader"),
                new SimplePreparableReloadListener<Void>() {
                    @Override
                    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                        return null;
                    }

                    @Override
                    protected void apply(Void data, ResourceManager resourceManager, ProfilerFiller profiler) {
                        BlockFamilyInit.loadBlockFamilies(resourceManager);
                    }
                }
        );
    }
}