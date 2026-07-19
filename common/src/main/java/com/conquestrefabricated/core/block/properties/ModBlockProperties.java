package com.conquestrefabricated.core.block.properties;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockProperties {
    public static final IntegerProperty TOGGLE_2 = IntegerProperty.create("toggle", 1, 2);
    public static final BooleanProperty OFFSET_TOGGLE = BooleanProperty.create("offset_toggle");
    public static final IntegerProperty LIGHT_0_3 = IntegerProperty.create("light", 0, 3);
    public static final EnumProperty<Half> TYPE_UPDOWN = EnumProperty.create("type", Half.class);
    public static final BooleanProperty EXTENSION_TOGGLE = BooleanProperty.create("extension_toggle");
    public static final IntegerProperty LAYERS_4 = IntegerProperty.create("layer", 1, 4);
}
