package com.conquestrefabricated.core.block.properties;

import net.minecraft.util.StringRepresentable;

public enum Third implements StringRepresentable {
    TOP("top"),
    MIDDLE("middle"),
    BOTTOM("bottom");

    private final String name;

    Third(String name) {
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
