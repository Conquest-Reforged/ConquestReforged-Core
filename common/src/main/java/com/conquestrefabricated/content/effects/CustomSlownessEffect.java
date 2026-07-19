package com.conquestrefabricated.content.effects;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class CustomSlownessEffect extends MobEffect {// Create a unique ID for your effect's attribute modifier
    public static final Identifier MODIFIER_ID = Identifier.fromNamespaceAndPath("conquest", "custom_slowness");

    public CustomSlownessEffect() {
        // Use the HARMFUL category like vanilla Slowness
        super(MobEffectCategory.HARMFUL, 0x5A6C81); // Same color as vanilla Slowness
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        AttributeInstance instance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null && instance.getModifier(MODIFIER_ID) == null) {
            instance.addTransientModifier(
                    new AttributeModifier(
                            MODIFIER_ID,
                            getMovementSpeedModifierValue(amplifier),
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
            );
        }
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributes) {
        AttributeInstance instance = attributes.getInstance(Attributes.MOVEMENT_SPEED);
        if (instance != null) {
            instance.removeModifier(MODIFIER_ID);
        }
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity entity, int amplifier) {
        // Handle jump and sprint penalties
        if (entity instanceof Player player) {
            // Modify jump behavior

            if (player.jumping && player.isSprinting()) {
                double sprintPenalty = getSprintPenaltyValue(amplifier);
                if (player.getDeltaMovement().y > 0) {
                    player.setDeltaMovement(
                            player.getDeltaMovement().x * (1.0 - sprintPenalty),
                            player.getDeltaMovement().y,
                            player.getDeltaMovement().z * (1.0 - sprintPenalty)
                    );
                }
            }
            else if (player.jumping) {
                // Reduce jump velocity
                double jumpReduction = getJumpReductionValue(amplifier);
                if (player.getDeltaMovement().y > 0) {
                    player.setDeltaMovement(
                            player.getDeltaMovement().x * (1.0 - jumpReduction),
                            player.getDeltaMovement().y,
                            player.getDeltaMovement().z * (1.0 - jumpReduction)
                    );
                }
            }
            return true;
//
//            // Modify sprint behavior
//            if (player.isSprinting()) {
//                // Apply extra slowdown when sprinting
//                double sprintPenalty = getSprintPenaltyValue(amplifier);
//                player.setVelocity(
//                        player.getVelocity().x * (1.0 - sprintPenalty),
//                        player.getVelocity().y,
//                        player.getVelocity().z * (1.0 - sprintPenalty)
//                );
//            }
        }
        return false;
    }

    // Returns the speed modifier value based on the effect's amplifier
    private double getMovementSpeedModifierValue(int amplifier) {
        // Matches vanilla slowness values:
        // Slowness I = -0.5
        // Slowness II = -0.10
        // Slowness III = -0.15
        return -0.05 * (amplifier + 1);
    }

    // Returns the jump height reduction (0.0 to 1.0) based on amplifier
    private double getJumpReductionValue(int amplifier) {
        // Jump height reduction:
        // Level I = 20% reduction
        // Level II = 35% reduction
        // Level III = 50% reduction
        return 0.1 * (amplifier);
    }

    // Returns the sprint speed penalty (0.0 to 1.0) based on amplifier
    private double getSprintPenaltyValue(int amplifier) {
        // Sprint penalty:
        // Level I = 10% extra reduction
        // Level II = 20% extra reduction
        // Level III = 30% extra reduction
        return 0.1 * (amplifier);
    }
}