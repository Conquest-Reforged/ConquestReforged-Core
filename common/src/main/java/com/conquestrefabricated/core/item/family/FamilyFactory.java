package com.conquestrefabricated.core.item.family;

import com.conquestrefabricated.core.block.factory.TypeList;
import java.util.function.BiFunction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;

public interface FamilyFactory<T> {

    Family<T> create(Identifier name, CreativeModeTab group, TypeList types);

    static <T> FamilyFactory<T> of(BiFunction<CreativeModeTab, TypeList, Family<T>> func) {
        return (n, g, t) -> func.apply(g, t);
    }
}
