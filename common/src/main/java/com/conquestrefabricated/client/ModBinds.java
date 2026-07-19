package com.conquestrefabricated.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;


@Environment(EnvType.CLIENT)
public class ModBinds {

    public static KeyMapping palette;

    public static KeyMapping getPaletteBind() {
        return palette;
    }
}
