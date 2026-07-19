package com.conquestrefabricated.core.init;

import com.conquestrefabricated.core.block.data.BlockData;
import com.conquestrefabricated.core.block.data.BlockDataRegistry;
import com.conquestrefabricated.core.block.data.ColorType;
import com.conquestrefabricated.core.client.color.BlockColors;
import com.conquestrefabricated.core.util.log.Log;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;

import java.util.List;


@Environment(EnvType.CLIENT)
public class InitClient {

    public static void blockColors() {
        Log.debug("Registering block colors");

        Log.debug("Registering block colors");
        for (BlockData data : BlockDataRegistry.getInstance()) {
            if (data.getProps().getColorType() == ColorType.GRASS) {
                BlockColorRegistry.register(List.of(BlockColors.GRASS), data.getBlock());
            } else if (data.getProps().getColorType() == ColorType.FOLIAGE) {
                BlockColorRegistry.register(List.of(BlockColors.FOLIAGE), data.getBlock());
            } else if (data.getProps().getColorType() == ColorType.WATER) {
                BlockColorRegistry.register(List.of(BlockColors.WATER), data.getBlock());
            }
        }
    }

    ///Item tints now data-driven ??
    public static void itemColors() {
//        Log.debug("Registering item colors");
//
//        for (BlockData data : BlockDataRegistry.getInstance()) {
//            if (data.getProps().getColorType() == ColorType.GRASS || data.getProps().getColorType() == ColorType.FOLIAGE) {
//                ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0x6c994b ,data.getItem());
//            }
//        }

    }
}
