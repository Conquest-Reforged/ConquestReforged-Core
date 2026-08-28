package com.conquestrefabricated.content.blocks.init.neoforge;


import com.conquestrefabricated.content.blocks.init.BlockRegistrar;
import com.conquestrefabricated.content.blocks.tileentity.TileEntityTypes;
import com.conquestrefabricated.core.util.log.Log;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "conquest")
public class BlockRegistrarEvent {

    @SubscribeEvent
    public static void blocks(RegisterEvent event) {
        event.register(Registries.BLOCK, blockRegisterHelper -> {
            Log.info("Registering blocks");
            BlockRegistrar.blocks();
            //lilies and scaffolding are registered manually
        });

        event.register(Registries.BLOCK_ENTITY_TYPE, blockEntityTypeRegisterHelper -> {
            Log.info("Registering block entities");
            blockEntityTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", "loom"), TileEntityTypes.LOOM);
            blockEntityTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", "campfire"), TileEntityTypes.CAMPFIRE);
            blockEntityTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", "enchanter"), TileEntityTypes.ENCHANTING_TABLE);
            blockEntityTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", "oven"), TileEntityTypes.FURNACE);
            blockEntityTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", "animal"), TileEntityTypes.ANIMAL);
            blockEntityTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", "seat"), TileEntityTypes.SEAT);
            blockEntityTypeRegisterHelper.register(Identifier.fromNamespaceAndPath("conquest", "kiln"), TileEntityTypes.KILN);
        });
    }

    @SubscribeEvent
    public static void items(RegisterEvent event) {
        event.register(Registries.ITEM, itemRegisterHelper -> {
            Log.info("Registering block items");
        });
    }
}