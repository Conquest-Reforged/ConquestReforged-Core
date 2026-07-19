package com.conquestrefabricated.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

import static com.conquestrefabricated.content.effects.CustomSlownessEffect.MODIFIER_ID;

@Mixin(value = AbstractClientPlayer.class, priority = 500)
public abstract class AbstractClientPlayerEntityMixin {

    // This method will calculate the movement speed without your modifier's effect
    @ModifyReturnValue(
            method = "getFieldOfViewModifier",
            at = @At("RETURN")
    )
    private float adjustFovWithCustomEffect(float original) {
        AbstractClientPlayer player = (AbstractClientPlayer)(Object)this;
        AttributeInstance speedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

        // Only proceed if our modifier is active
        if (speedAttribute != null && speedAttribute.getModifier(MODIFIER_ID) != null) {
            return calculateAdjustedFov(player, speedAttribute);
        }

        return original;
    }

    private float calculateAdjustedFov(AbstractClientPlayer player, AttributeInstance speedAttribute) {
        // Get all modifiers and calculate what the speed would be without our modifier
        Collection<AttributeModifier> modifiers = speedAttribute.getModifiers();
        double baseValue = speedAttribute.getBaseValue();
        double valueWithoutOurModifier = baseValue;

        // First pass: Addition and MultiplyBase operations
        for (AttributeModifier modifier : modifiers) {
            if (!modifier.id().equals(MODIFIER_ID)) {
                if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                    valueWithoutOurModifier += modifier.amount();
                } else if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                    valueWithoutOurModifier += baseValue * modifier.amount();
                }
            }
        }

        // Second pass: MultiplyTotal operations
        double multiplierTotal = 1.0;
        for (AttributeModifier modifier : modifiers) {
            if (!modifier.id().equals(MODIFIER_ID) &&
                    modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                multiplierTotal += modifier.amount();
            }
        }
        valueWithoutOurModifier *= multiplierTotal;

        // Calculate FOV with flying effect (if active) and our adjusted speed value
        float f = 1.0F;
        if (player.getAbilities().flying) {
            f *= 1.1F;
        }

        f *= ((float)valueWithoutOurModifier / player.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
        if (player.getAbilities().getWalkingSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0F;
        }

        // Handle bow usage
        if (player.isUsingItem()) {
            if (player.getUseItem().is(net.minecraft.world.item.Items.BOW)) {
                int i = player.getTicksUsingItem();
                float g = (float)i / 20.0F;
                if (g > 1.0F) {
                    g = 1.0F;
                } else {
                    g *= g;
                }
                f *= 1.0F - g * 0.15F;
            } else if (net.minecraft.client.Minecraft.getInstance().options.getCameraType().isFirstPerson() &&
                    player.isScoping()) {
                return 0.1F;
            }
        }

        // Apply FOV effect scale from game options
        return net.minecraft.util.Mth.lerp(
                ((Double)net.minecraft.client.Minecraft.getInstance().options.fovEffectScale().get()).floatValue(),
                1.0F, f);
    }
}