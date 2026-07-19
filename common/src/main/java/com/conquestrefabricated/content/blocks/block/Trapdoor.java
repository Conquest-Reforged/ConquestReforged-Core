package com.conquestrefabricated.content.blocks.block;

import com.conquestrefabricated.core.block.builder.Props;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class Trapdoor extends TrapDoorBlock {

    public Trapdoor(Props properties, BlockSetType type) {
        super(type, properties.toSettings());
    }
}
