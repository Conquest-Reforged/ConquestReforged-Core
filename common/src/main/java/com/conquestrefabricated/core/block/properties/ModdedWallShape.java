package com.conquestrefabricated.core.block.properties;

import net.minecraft.util.StringRepresentable;

public enum ModdedWallShape implements StringRepresentable {
    NONE("false"),
    LOW("low"),
    TALL("true");

    private final String name;

    private ModdedWallShape(String name) {
        this.name = name;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return this.name;
    }
}
