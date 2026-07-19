package com.conquestrefabricated.content.blocks.util;

import com.conquestrefabricated.content.items.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;

public class Interactions {
    public static InteractionResult onUseToggleItem(Player player, Level level, BlockPos blockPos, BlockState state, Property<?> property) {
        if (!player.getAbilities().mayBuild) {
            return InteractionResult.FAIL;
        }

        if (player.getAbilities().instabuild) {
            level.setBlock(blockPos, state.cycle(property), 3);
            return InteractionResult.SUCCESS;
        }

        if (player.getMainHandItem().is(CYCLING_TOOLS)) {
            level.setBlock(blockPos, state.cycle(property), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
