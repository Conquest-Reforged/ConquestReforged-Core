package com.conquestrefabricated.content.blocks;

import net.minecraft.world.level.block.state.BlockBehaviour;

public interface BlockSettingsAccessor {
    BlockBehaviour.Properties setCustomOffsetter(CustomOffsetType offsetter);
}