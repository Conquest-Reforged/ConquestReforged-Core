package com.conquestrefabricated.mixin;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.block.trees.LeavesFruit;
import com.conquestrefabricated.content.blocks.block.trees.LeavesGround;
import com.conquestrefabricated.content.blocks.block.trees.LeavesGroundFruit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockBehaviour.BlockStateBase.class)
public class AbstractBlockStateMixin {

    private static final VoxelShape EMPTY_SHAPE = Shapes.empty();

    @Unique
    private BlockState self() {
        return (BlockState) (Object) this;
    }

    @Inject(
            method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void leaves1_getCollisionShape(BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> ci) {
        if (ConquestConfig.INSTANCE.passThroughLeaves.get()) {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (ConquestConfig.INSTANCE.passThroughLeaves.get()) {
                if (block instanceof LeavesBlock || block instanceof LeavesFruit || block instanceof LeavesGround || block instanceof LeavesGroundFruit) {
                    ci.setReturnValue(EMPTY_SHAPE);
                }
            }
        }
    }

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true)
    private void leaves_getCollisionShape(BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<VoxelShape> ci) {
        if (ConquestConfig.INSTANCE.passThroughLeaves.get()) {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (block instanceof LeavesBlock || block instanceof LeavesFruit || block instanceof LeavesGround || block instanceof LeavesGroundFruit) {
                ci.setReturnValue(EMPTY_SHAPE);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "isViewBlocking", cancellable = true)
    private void leaves_canOcclude(BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ConquestConfig.INSTANCE.passThroughLeaves.get()) {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (block instanceof LeavesBlock || block instanceof LeavesFruit) {
                cir.setReturnValue(false);
            }
        }
    }
}