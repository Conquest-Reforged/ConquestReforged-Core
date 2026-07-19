package com.conquestrefabricated.content.blocks.tileentity;

import com.conquestrefabricated.content.blocks.block.decor.Kiln;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class KilnTileEntity extends BlockEntity {

    public KilnTileEntity(BlockPos pos, BlockState state) {
        super(TileEntityTypes.KILN, pos, state);
    }


    public static void particleTick(Level world, BlockPos blockPos, BlockState state, KilnTileEntity blockEntity) {
        RandomSource random = world.getRandom();
        if (random.nextFloat() < 0.11F) {
            for(int i = 0; i < random.nextInt(2) + 2; ++i) {
                Kiln.spawnSmokeParticle(world, blockPos);
            }
        }
    }
}