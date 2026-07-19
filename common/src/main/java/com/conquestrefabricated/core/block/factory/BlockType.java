package com.conquestrefabricated.core.block.factory;

import com.conquestrefabricated.core.block.builder.Props;
import java.lang.reflect.Constructor;
import net.minecraft.world.level.block.Block;

public interface BlockType {

    Block create(Props props) throws InitializationException;

    interface Factory {

        BlockType create(Constructor<? extends Block> constructor);
    }
}
