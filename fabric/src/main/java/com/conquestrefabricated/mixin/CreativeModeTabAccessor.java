package com.conquestrefabricated.mixin;

import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(CreativeModeTab.class)
public interface CreativeModeTabAccessor {
    @Accessor("row")
    @Mutable
    void conquest$setRow(CreativeModeTab.Row row);

    @Accessor("column")
    @Mutable
    void conquest$setColumn(int column);

    @Accessor("iconGenerator")
    @Mutable
    void conquest$setIcon(Supplier<net.minecraft.world.item.ItemStack> icon);

    @Accessor("displayItemsGenerator")
    @Mutable
    void conquest$setDisplayItemsGenerator(CreativeModeTab.DisplayItemsGenerator generator);

    @Accessor("displayItemsGenerator")
    CreativeModeTab.DisplayItemsGenerator conquest$getDisplayItemsGenerator();

}