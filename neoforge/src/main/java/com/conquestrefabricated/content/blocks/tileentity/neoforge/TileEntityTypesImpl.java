package com.conquestrefabricated.content.blocks.tileentity.neoforge;


import com.conquestrefabricated.content.blocks.tileentity.TileEntityFactory;
import net.minecraft.core.Holder;
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

            // Just build the object — actual registration happens in BlockRegistrarEvent via RegisterEvent
            return new BlockEntityType<>(factory::create, blocks);
        }
    }
