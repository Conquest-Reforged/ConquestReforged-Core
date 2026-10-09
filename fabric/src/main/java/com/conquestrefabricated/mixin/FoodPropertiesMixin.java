package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.spoilage.SpoilageEating;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Food that has gone off feeds less and may make the eater ill. The rules are in {@link SpoilageEating}. */
@Mixin(FoodProperties.class)
public abstract class FoodPropertiesMixin {

    @WrapOperation(method = "onConsume", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void conquest$eatSpoiled(FoodData data, FoodProperties food, Operation<Void> eat,
                                     @Local(argsOnly = true) LivingEntity eater,
                                     @Local(argsOnly = true) ItemStack stack) {
        eat.call(data, SpoilageEating.scaled(food, stack));
        SpoilageEating.sicken(eater, stack);
    }
}
