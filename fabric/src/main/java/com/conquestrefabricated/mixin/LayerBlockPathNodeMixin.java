package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.blocks.block.Layer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkNodeEvaluator.class)
public class LayerBlockPathNodeMixin {

    @Inject(method = "getPathTypeOfMob",
            at = @At("HEAD"),
            cancellable = true)
    private void checkLayerBlock(PathfindingContext context, int x, int y, int z, Mob mob, CallbackInfoReturnable<PathType> cir) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = context.level().getBlockState(pos);

        if (blockState.getBlock() instanceof Layer) {
            int layers = blockState.getValue(Layer.LAYERS);

            if (layers == 8) {
                cir.setReturnValue(PathType.BLOCKED);
            }
        }
    }
}