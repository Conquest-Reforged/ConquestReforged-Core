package com.conquestrefabricated.content.blocks.block.grass;

import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.world.level.block.GrassBlock;

@Render(RenderLayer.CUTOUT_MIPPED_SOLID)
public class Grass extends GrassBlock {

    public Grass(Properties properties) {
        super(properties);
    }
}
