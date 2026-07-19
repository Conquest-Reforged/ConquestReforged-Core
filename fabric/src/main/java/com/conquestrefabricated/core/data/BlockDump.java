package com.conquestrefabricated.core.data;

import com.conquestrefabricated.content.blocks.block.Cube;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockDump {

    public static void run() {
        try (Writer blocks = newWriter("blocks-pls.txt"); Writer states = newWriter("states-pls.txt")) {
            BuiltInRegistries.BLOCK.keySet().stream()
                    .filter(block -> block.getNamespace().equals("conquest"))
                    .sorted(Comparator.comparing(Identifier::toString))
                    .forEach(block -> {
                        try {
                            appendBlock(BuiltInRegistries.BLOCK.get(block).get().value(), blocks);
                            blocks.append('\n');

                            for (BlockState state : BuiltInRegistries.BLOCK.get(block).get().value().getStateDefinition().getPossibleStates()) {
                                appendState(state, states);
                                states.append('\n');
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
        appendBlock(state.getBlock(), writer);

        if (!state.getProperties().isEmpty()) {
            writer.append('[');
            boolean[] first = {true};
            state.getValues().forEach(e -> {
                try {
                    if (!first[0]) {
                        writer.append(',');
                    }
                    first[0] = false;
                    writer.append(e.property().getName()).append('=').append(e.value().toString());
                } catch (IOException ex) {
                    throw new java.io.UncheckedIOException(ex);
                }
            });
            writer.append(']');
        }
    }
}
