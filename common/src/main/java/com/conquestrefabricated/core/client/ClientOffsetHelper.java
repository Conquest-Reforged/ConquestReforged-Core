package com.conquestrefabricated.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * This class is used in BlockSettingsOffsetMixin to help prevent Client side code (ie: Minecraft class) from being accessed on the server. 
 */
public class ClientOffsetHelper {
    public static Level getLevel(BlockPos pos) {
        Level world = Minecraft.getInstance().level;
        if (world == null) return null;
        return world;
    }
}