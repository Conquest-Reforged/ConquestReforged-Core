package com.conquestrefabricated.mixin;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.blocks.block.trees.LeavesFruit;
import com.conquestrefabricated.content.blocks.block.trees.LeavesGround;
import com.conquestrefabricated.content.blocks.block.trees.LeavesGroundFruit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockStateBase.class, priority = 1500)
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
    private void leaves_getCollisionShape(BlockGetter worldIn, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> ci) {
        if (FabricLoader.getInstance().getModContainer("passablefoliage").isEmpty()) {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (ConquestConfig.INSTANCE.passThroughLeaves.get()) {
                if (block instanceof LeavesBlock || block instanceof LeavesFruit || block instanceof LeavesGround || block instanceof LeavesGroundFruit) {
                    ci.setReturnValue(EMPTY_SHAPE);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "isViewBlocking", cancellable = true)
    private void leaves_canOcclude(BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Block block = worldIn.getBlockState(pos).getBlock();
        if (ConquestConfig.INSTANCE.passThroughLeaves.get()) {
            if (block instanceof LeavesBlock || block instanceof LeavesFruit || block instanceof LeavesGround || block instanceof LeavesGroundFruit) {
                cir.setReturnValue(false);
            }
        }
    }




//    @Override
//    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
//        return VoxelShapes.fullCube();
//    }
//
//    @Override
//    public VoxelShape getCollisionShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
//        return EMPTY_SHAPE;
//    }
//
//    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        return VoxelShapes.fullCube();
//    }
//
//    @Override
//    public VoxelShape getRaycastShape(BlockState state, BlockView worldIn, BlockPos pos) {
//        return VoxelShapes.fullCube();
//    }
//
//    @Override
//    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
//        if (entity instanceof PlayerEntity) {
//            entity.slowMovement(state, new Vec3d(0.9F, 0.95F, 0.9F));
//        } else {
//            entity.slowMovement(state, new Vec3d(0.9F, 1.0F, 0.9F));
//        }
//    }
//
////    @Override
////    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
////        return 1.0f;
////    }
//
//    @Override
//    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
//        return true;
//    }
//
//    public boolean isSolidBlock(BlockView world, BlockPos pos) {
//        return false;
//    }
//
//    @Override
//    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
//        return true;
//    }
//
////    @Override
////    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
////        return true;
////    }
//
//    //@Override
//    //public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
//    //    return 0;
//    //}
}