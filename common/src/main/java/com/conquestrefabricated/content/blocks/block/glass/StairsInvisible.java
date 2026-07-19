package com.conquestrefabricated.content.blocks.block.glass;

import com.conquestrefabricated.content.blocks.block.Stairs;
import com.conquestrefabricated.core.asset.annotation.Assets;
import com.conquestrefabricated.core.asset.annotation.Model;
import com.conquestrefabricated.core.asset.annotation.Render;
import com.conquestrefabricated.core.asset.annotation.State;
import com.conquestrefabricated.core.util.RenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Assets(
        state = @State(name = "%s_stairs", template = "parent_stairs_cutout"),
        item = @Model(name = "item/%s_stairs", parent = "block/%s_stairs", template = "item/acacia_stairs"),
        render = @Render(RenderLayer.TRANSLUCENT),
        block = {
                @Model(name = "block/%s_stairs", template = "block/parent_stairs_cutout"),
                @Model(name = "block/%s_stairs_outer", template = "block/parent_stairs_outer_cutout"),
                @Model(name = "block/%s_stairs_inner", template = "block/parent_stairs_inner_cutout"),
        }
)
public class StairsInvisible extends Stairs {

    public StairsInvisible(BlockState parent, Properties properties) {
        super(parent, properties);
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
}