package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.blocks.util.PlantPickHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityPickMixin {

    @Inject(method = "pick", at = @At("RETURN"), cancellable = true)
    private void conquest$pickLoweredPlants(double range, float partialTick, boolean fluids, CallbackInfoReturnable<HitResult> cir) {
        cir.setReturnValue(PlantPickHelper.refine((Entity) (Object) this, range, partialTick, cir.getReturnValue()));
    }
}
