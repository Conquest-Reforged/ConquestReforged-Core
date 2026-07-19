package com.conquestrefabricated.core.asset.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.minecraft.world.item.BlockItem;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Item {

    Class<? extends net.minecraft.world.item.Item> value() default BlockItem.class;
}
