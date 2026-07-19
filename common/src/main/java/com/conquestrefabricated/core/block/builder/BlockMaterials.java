package com.conquestrefabricated.core.block.builder;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockMaterials {
    public static final BlockBehaviour.Properties WOOD_SPECIAL = (BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().replaceable().pushReaction(PushReaction.NORMAL));

    public static final BlockBehaviour.Properties ROCK_SPECIAL = (BlockBehaviour.Properties.of().mapColor(MapColor.STONE).forceSolidOn().pushReaction(PushReaction.NORMAL));
}
