package com.conquestrefabricated.content.blocks.tileentity.fabric;

import com.conquestrefabricated.content.blocks.tileentity.TileEntityFactory;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TileEntityTypesImpl {
    private static Block resolveBlock(String name) {
        Block block = BuiltInRegistries.BLOCK.get(Identifier.parse(name))
                .map(Holder.Reference::value)
                .orElse(Blocks.AIR);
        if (block == Blocks.AIR) {
            throw new IllegalArgumentException("Block passed into tile entity registration is not registered correctly: " + name);
        }
        return block;
    }

    public static <T extends BlockEntity> BlockEntityType<T> create(TileEntityFactory<T> factory, String name, String... blockNames) {
        Block[] blocks = new Block[blockNames.length];
        for (int i = 0; i < blockNames.length; i++) {
            blocks[i] = resolveBlock(blockNames[i]);
        }

        BlockEntityType<T> type = FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, type);
    }

    public static void addVanilla(BlockEntityType vanillaBlockEntity, String... blockNames) {
        for (String blockName : blockNames) {
            Block block = BuiltInRegistries.BLOCK.get(Identifier.parse(blockName)).get().value();
            if (block == null || block == Blocks.AIR) {
                throw new IllegalArgumentException("Block passed into tile entity registration is not registered correctly");
            }
            BlockEntityType.BED.addValidBlock(block);
        }
    }
}