package com.conquestrefabricated.core.block.properties;

import net.minecraft.util.StringRepresentable;

public enum VerticalConnectionShape2 implements StringRepresentable {
    SINGLE("single"),
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    private final String name;

    VerticalConnectionShape2(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
