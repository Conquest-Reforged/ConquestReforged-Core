package com.conquestrefabricated.mixin;

import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This Mixin changes the value which determines whether mobs can path through leaves
 */
@Mixin(PathType.class)
public class PathNodeTypeMixin {

    @Shadow @Final @Mutable
    private float malus;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyLeavesPenalty(String enumName, int ordinal, float defaultPenalty, CallbackInfo ci) {
        // Check if this is the LEAVES enum constant
        if (enumName.equals("LEAVES")) {
            // Change the penalty from -1.0F to 0.0F to make leaves traversable
            this.malus = 0.0F;
        }
    }
}