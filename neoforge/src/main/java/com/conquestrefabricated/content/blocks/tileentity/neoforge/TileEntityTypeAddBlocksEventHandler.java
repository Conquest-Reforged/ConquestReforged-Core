package com.conquestrefabricated.content.blocks.tileentity.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

@EventBusSubscriber(modid = "conquest")
public class TileEntityTypeAddBlocksEventHandler {

    @SubscribeEvent
    public static void onAddBlocks(BlockEntityTypeAddBlocksEvent event) {
        //addVanilla(event, BlockEntityType.BED, "conquest:some_bed_block", "conquest:another_bed_block");
        // add further addVanilla(...) calls here for each vanilla BlockEntityType you extend
    }

    private static void addVanilla(BlockEntityTypeAddBlocksEvent event, BlockEntityType<?> vanillaBlockEntity, String... blockNames) {
        Block[] blocks = new Block[blockNames.length];
        for (int i = 0; i < blockNames.length; i++) {
            blocks[i] = resolveBlock(blockNames[i]);
        }
        event.modify(vanillaBlockEntity, blocks);
    }

    private static Block resolveBlock(String name) {
        Block block = BuiltInRegistries.BLOCK.get(Identifier.parse(name))
                .map(net.minecraft.core.Holder.Reference::value)
                .orElse(Blocks.AIR);
        if (block == Blocks.AIR) {
            throw new IllegalArgumentException("Block passed into tile entity registration is not registered correctly: " + name);
        }
        return block;
    }
}