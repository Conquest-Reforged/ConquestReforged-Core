package com.conquestrefabricated.core.data;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.block.decor.*;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        //TagAppender<Block, Block> shovelMineableTagBuilder = valueLookupBuilder(BlockTags.MINEABLE_WITH_SHOVEL);
        //TagAppender<Block, Block> pickaxeMineableTagBuilder = valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE);
        //TagAppender<Block, Block> axeMineableTagBuilder = valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE);
        TagAppender<Block, Block> wallTagBuilder = valueLookupBuilder(BlockTags.WALLS);
        //TagAppender<Block, Block> bedTagBuilder = valueLookupBuilder(BlockTags.BEDS);
        TagAppender<Block, Block> railTagBuilder = valueLookupBuilder(BlockTags.RAILS);
        TagAppender<Block, Block> fenceTagBuilder = valueLookupBuilder(BlockTags.FENCES);
        //TagAppender<Block, Block> cropTagBuilder = valueLookupBuilder(BlockTags.CROPS);
        //TagAppender<Block, Block> logTagBuilder = valueLookupBuilder(BlockTags.LOGS);
        TagAppender<Block, Block> climbableTagBuilder = valueLookupBuilder(BlockTags.CLIMBABLE);
        //TagAppender<Block, Block> leafTagBuilder = valueLookupBuilder(BlockTags.LEAVES);
        //TagAppender<Block, Block> doorTagBuilder = valueLookupBuilder(BlockTags.DOORS);

        BlockDataRegistry.getInstance().getData("conquest").forEach(blockData -> {
            for (TagKey<Block> tag : blockData.getTags()) {
                valueLookupBuilder(tag).add(blockData.getBlock()).setReplace(false);
            }
        });

        BuiltInRegistries.BLOCK.stream()
                .forEach(block -> {
                    Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
                    SoundType soundGroup = block.getSoundType(block.defaultBlockState());
                    if (blockId.getNamespace().equals("conquest")) {
                        if (block instanceof WallNew || block instanceof WallOld) {
                            wallTagBuilder.add(block).setReplace(false);
                        }
                        if (block instanceof Rail) {
                            railTagBuilder.add(block).setReplace(false);
                        }
                        if (block instanceof Fence || block instanceof FenceVanilla || block instanceof FenceCross || block instanceof FenceToggle || block instanceof RusticFence || blockId.getPath().contains("fence")) {
                            fenceTagBuilder.add(block).setReplace(false);
                        }
                        if (block instanceof Scaffolding) {
                            climbableTagBuilder.add(block).setReplace(false);
                        }
                    }
                });
    }
}