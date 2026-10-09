package com.conquestrefabricated.content.spoilage;

import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

/**
 * What going off does to a meal. Food that is merely ageing is as good as new; from halfway to rotten it
 * feeds less and less - down to under a third at the last stage - and risks making the eater ill, with the
 * risk rising to better than one in two.
 */
public final class SpoilageEating {

    /** The stage from which food starts to turn: halfway to rotten. */
    private static final int TURNS_AT = Spoilage.STAGES / 2;

    private SpoilageEating() {
    }

    /** How far past the turn the stack is: 0 up to the turn, rising to 1 at the last stage. */
    private static float spoiled(ItemStack stack) {
        return Math.clamp((Spoilage.stage(stack) - TURNS_AT + 1) / (float) (Spoilage.STAGES - TURNS_AT), 0.0F, 1.0F);
    }

    /** The food as it feeds in this state. */
    public static FoodProperties scaled(FoodProperties food, ItemStack stack) {
        float spoiled = Spoilage.enabled() ? spoiled(stack) : 0.0F;
        if (spoiled <= 0.0F) {
            return food;
        }
        float keeps = 1.0F - 0.7F * spoiled;
        return new FoodProperties(Math.max(1, Math.round(food.nutrition() * keeps)), food.saturation() * keeps,
                food.canAlwaysEat());
    }

    /** May make the eater ill, more likely the worse the food. */
    public static void sicken(LivingEntity eater, ItemStack stack) {
        float spoiled = Spoilage.enabled() ? spoiled(stack) : 0.0F;
        RandomSource random = eater.getRandom();
        if (spoiled <= 0.0F || eater.level().isClientSide() || random.nextFloat() >= 0.6F * spoiled) {
            return;
        }
        eater.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200 + Math.round(400 * spoiled), 0));
        if (spoiled > 0.5F) {
            eater.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0));
        }
    }
}
