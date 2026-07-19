package com.conquestrefabricated.core.block.factory.constructor;

import com.conquestrefabricated.core.block.builder.Props;
import com.conquestrefabricated.core.block.factory.InitializationException;
import java.lang.reflect.Constructor;
import net.minecraft.world.level.block.Block;

public class DyeConstructor extends PropsConstructor {

    public DyeConstructor(Constructor<? extends Block> constructor) {
        super(constructor);
    }

    @Override
    public Block create(Props props) throws InitializationException {
        return newInstance(props.dye(), props.toSettings());
    }
}
