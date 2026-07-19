package com.conquestrefabricated.core.data;

import com.conquestrefabricated.content.blocks.block.Cube;
import com.conquestrefabricated.content.blocks.block.VerticalCorner;
import com.conquestrefabricated.content.blocks.block.VerticalQuarter;
import com.conquestrefabricated.content.blocks.block.plants.Bush;
import com.conquestrefabricated.content.blocks.block.trees.Branch;
import com.conquestrefabricated.content.blocks.block.trees.BranchLarge;
import com.conquestrefabricated.content.blocks.block.trees.BranchSmall;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.util.RenderLayer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class WorldPainterGenerator {

    public static void run() {
        try (Writer blocks = newWriter("conquest_wp.csv")) {
            blocks.append("name,discriminator,properties,opacity,receivesLight,insubstantial,resource,tileEntity,tileEntityId,treeRelated,vegetation,blockLight,natural,watery,colour,horizontal_orientation_schemes,vertical_orientation_scheme\n");
            BlockDataRegistry.getInstance().getData("conquest").forEach(blockData -> {
                Block block = BuiltInRegistries.BLOCK.get(blockData.getRegistryName()).get().value();

                {
                    try {
                        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
                        //Block ID
                        appendBlock(block, blocks);
                        blocks.append(',');
                        //discriminator
                        blocks.append(',');
                        //properties
                        appendState(block.defaultBlockState(), blocks);
                        blocks.append(',');
                        //opacity
                        int opacity = block.defaultBlockState().canOcclude() ? 15 : 0;
                        blocks.append(opacity + ",");
                        //receivesLight
                        boolean receivesLight = blockData.getProps().getRenderLayer() == RenderLayer.SOLID && !(block instanceof Cube);
                        blocks.append(receivesLight + ",");
                        //insubstantial
                        boolean isVegetation = !(block instanceof BranchLarge) && (block.defaultBlockState().canBeReplaced() || blockId.getPath().contains("rocks") || (blockId.getPath().contains("leaves") || (block.defaultBlockState().getSoundType() == SoundType.GRASS && blockId.getPath().contains("leaf") || blockId.getPath().contains("needles") || blockId.getPath().contains("blossoms")  || blockId.getPath().contains("lilac") || blockId.getPath().contains("mistletoe")  || blockId.getPath().contains("tree")  || blockId.getPath().contains("cypress") || blockId.getPath().contains("branch"))));
                        blocks.append(isVegetation + ",");
                        //resource
                        blocks.append("false,");
                        //tileEntity
                        blocks.append("false,");
                        //tileEntityId
                        blocks.append("false,");
                        //treeRelated
                        boolean isTreeRelated = (blockId.getPath().contains("log") || blockId.getPath().contains("branch") || blockId.getPath().contains("tree") || blockId.getPath().contains("sapling")) ||
                                (block.defaultBlockState().getSoundType() == SoundType.GRASS && (blockId.getPath().contains("leaves") || blockId.getPath().contains("leaf") || blockId.getPath().contains("needles") || blockId.getPath().contains("blossoms")  || blockId.getPath().contains("lilac")  || blockId.getPath().contains("mistletoe")  || blockId.getPath().contains("tree")  || blockId.getPath().contains("cypress")));
                        blocks.append(isTreeRelated + ",");
                        //vegetation
                        blocks.append((isTreeRelated || isVegetation) + ",");
                        //blockLight
                        blocks.append(block.defaultBlockState().getLightEmission() + ",");
                        //natural
                        blocks.append(true + ",");
                        //watery
                        blocks.append("false,");
                        //colour
                        block.defaultMapColor();
                        int colorId = block.defaultMapColor().col;
                        blocks.append(colorId + ",");
                        //horizontal_orientation_schemes
                        blocks.append(block instanceof VerticalQuarter || block instanceof VerticalCorner ? "facing_asym," : ",");
                        //vertical_orientation_schemes
                        blocks.append('\n');

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Writer newWriter(String name) throws IOException {
        return new BufferedWriter(new FileWriter(name));
    }

    private static void appendBlock(Block block, Writer writer) throws IOException {
        if (BuiltInRegistries.BLOCK.getKey(block) != null) {
            writer.append(BuiltInRegistries.BLOCK.getKey(block).toString());
        }
    }

    private static void appendState(BlockState state, Writer writer) throws IOException {

        if (!state.getProperties().isEmpty()) {
            writer.append('"');
            boolean first = true;

            for (Property<?> e: state.getProperties()) {
                if (!first) {
                    writer.append(',');
                }
                if (e.getValueClass() == Boolean.class) {
                    writer.append(e.getName()).append(":b");
                }
                else if (e.getValueClass() == Integer.class) {
                    int min = Collections.min((Collection<Integer>) e.getPossibleValues());
                    int max = Collections.max((Collection<Integer>) e.getPossibleValues());
                    writer.append(e.getName()).append(":i[" + min + "-" + max + "]");
                }
                else {
                    writer.append(e.getName()).append(":e" + e.getPossibleValues().toString().replace(", ",";"));
                }
                first = false;
            }
            writer.append('"');
        }
    }
}
