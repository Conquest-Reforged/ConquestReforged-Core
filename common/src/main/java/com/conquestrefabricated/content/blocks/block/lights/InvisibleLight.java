package com.conquestrefabricated.content.blocks.block.lights;

import com.conquestrefabricated.content.blocks.block.Cube;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class InvisibleLight extends Cube {

    public InvisibleLight(Properties properties) {
        super(ensureNotSolid(properties));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void animateTick(BlockState stateIn, Level world, BlockPos pos, RandomSource rand) {
        ItemStack itemstack = Minecraft.getInstance().player.getMainHandItem();
        boolean flag = !itemstack.isEmpty() && itemstack.getItem() == this.asItem();
        if (flag) {
            for (int i = 0; i < 2; i++) {
                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, stateIn), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.00001, 0);
            }
        }
    }

    private static Properties ensureNotSolid(Properties properties) {
        properties.noCollision();
        return properties;
    }
}
