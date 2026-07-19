package com.conquestrefabricated.core.data;

import com.conquestrefabricated.content.blocks.block.*;
import com.conquestrefabricated.content.blocks.block.decor.Bed;
import com.conquestrefabricated.content.blocks.block.decor.DoubleHorizontalDirectional;
import com.conquestrefabricated.content.blocks.block.decor.Plough;
import com.conquestrefabricated.content.blocks.block.decor.PotteryWheel;
import com.conquestrefabricated.content.blocks.block.directional.LayerDirectional;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootSubProvider {

    public ModLootTableProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generate() {
        BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("conquest")).forEach(block -> {
            if (block.asItem() == net.minecraft.world.item.Items.AIR) {
                return; // no item form — nothing to generate a loot table for
            }

            if (block instanceof Sphere) {
                dropSelf(block);
            }
            else if (block instanceof VerticalSlab) {
                add(block, buildLayerDrops(block, VerticalSlab.LAYERS, 4));
            }
            else if (block instanceof VerticalSlabLessLayers) {
                add(block, buildLayerDrops(block, VerticalSlab.LAYERS, 3));
            }
            else if (block instanceof Layer || block instanceof Slab || block instanceof LayerDirectional) {
                add(block, buildLayerDrops(block, Layer.LAYERS, 8));
            }
            else if (block instanceof VerticalCorner) {
                add(block, buildLayerDrops(block, VerticalCorner.LAYERS, 4));
            }
            else if (block instanceof VerticalCornerLessLayers) {
                add(block, buildLayerDrops(block, VerticalCornerLessLayers.LAYERS, 3));
            }
            else if (block instanceof VerticalQuarter) {
                add(block, buildLayerDrops(block, VerticalQuarter.LAYERS, 4));
            }
            else if (block instanceof VerticalQuarterLessLayers) {
                add(block, buildLayerDrops(block, VerticalQuarterLessLayers.LAYERS, 3));
            }
            else if (block instanceof Pillar) {
                add(block, buildLayerDrops(block, Pillar.LAYERS, 3));
            }
            else if (block instanceof SlabLessLayers) {
                add(block, buildLayerDrops(block, SlabLessLayers.LAYERS, 4));
            }
            else if (block instanceof SlabQuarter) {
                add(block, buildLayerDrops(block, SlabQuarter.LAYERS, 3));
            }
            else if (block instanceof DoubleHorizontalDirectional || block instanceof Door) {
                add(block, createDoorTable(block));
            }
            else if (block instanceof Bed || block instanceof Plough || block instanceof PotteryWheel) {
                add(block, buildBedDrops(block));
            }
            else {
                dropSelf(block);
            }
        });
    }

    public LootTable.Builder buildLayerDrops(Block drop, IntegerProperty property, int amount) {
        LootPoolSingletonContainer.Builder<?> builder = LootItem.lootTableItem(drop);
        for (int i = 2; i <= amount; i++) {
            builder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(i))
                    .when(LootItemBlockStatePropertyCondition
                            .hasBlockStateProperties(drop).setProperties(StatePropertiesPredicate.Builder
                                    .properties().hasProperty(property, i))));
        }

        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(this.applyExplosionDecay(drop, builder)));

    }

    private LootTable.Builder buildBedDrops(Block drop) {
        return this.createSinglePropConditionTable(drop, BedBlock.PART, BedPart.HEAD);
    }
}
