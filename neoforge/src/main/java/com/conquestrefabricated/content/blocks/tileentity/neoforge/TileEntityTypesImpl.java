package com.conquestrefabricated.content.blocks.tileentity.neoforge;


import com.conquestrefabricated.content.blocks.tileentity.TileEntityFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

    @EventBusSubscriber(modid = "conquest")
    public class TileEntityTypesImpl {

        // NeoForge's BlockEntityType has no addValidBlock-style mutator (that's Fabric API only);
        // extra blocks must be registered via BlockEntityTypeAddBlocksEvent, which only fires once,
        // so calls to add() made anywhere during modloading are queued here and flushed on that event.
        private record PendingAddition(BlockEntityType<?> type, Block[] blocks) {}

        private static final List<PendingAddition> PENDING_ADDITIONS = new ArrayList<>();

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
            // Use the Set overload: the Block... vararg overload now hard-fails on a zero-length array,
            // which happens here whenever the backing block(s) aren't wired up in BlockRegistrar yet.
            return new BlockEntityType<>(factory::create, Set.of(blocks));
        }

        public static void add(BlockEntityType blockEntity, String... blockNames) {
            Block[] blocks = new Block[blockNames.length];
            for (int i = 0; i < blockNames.length; i++) {
                blocks[i] = resolveBlock(blockNames[i]);
            }
            PENDING_ADDITIONS.add(new PendingAddition(blockEntity, blocks));
        }

        @SubscribeEvent
        static void onAddBlocks(BlockEntityTypeAddBlocksEvent event) {
            for (PendingAddition pending : PENDING_ADDITIONS) {
                event.modify(pending.type(), pending.blocks());
            }
            PENDING_ADDITIONS.clear();
        }
    }
