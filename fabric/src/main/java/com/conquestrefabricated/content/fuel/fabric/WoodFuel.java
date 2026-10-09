package com.conquestrefabricated.content.fuel.fabric;

import com.conquestrefabricated.core.Namespaces;
import net.fabricmc.fabric.api.registry.FuelValueEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

/** Registers {@link com.conquestrefabricated.content.fuel.WoodFuel} as furnace fuel. */
public final class WoodFuel {

    private WoodFuel() {
    }

    public static void register() {
        FuelValueEvents.BUILD.register((builder, context) -> {
            var wood = com.conquestrefabricated.content.fuel.WoodFuel.woodTagged(context.registries());
            for (Block block : BuiltInRegistries.BLOCK) {
                if (!Namespaces.isRegistered(BuiltInRegistries.BLOCK.getKey(block).getNamespace())) {
                    continue;
                }
                Item item = block.asItem();
                int ticks = com.conquestrefabricated.content.fuel.WoodFuel.burnTime(block, wood);
                if (item != Items.AIR && ticks > 0) {
                    builder.add(item, ticks);
                }
            }
        });
    }
}
