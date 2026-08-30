package com.conquestrefabricated.mixin;

import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.content.effects.Effects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TallFlowerBlock.class)
public abstract class TallFlowerBlockMixin extends Block {

    public TallFlowerBlockMixin(Properties settings) {
        super(settings);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (entity instanceof LivingEntity livingEntity && ConquestConfig.INSTANCE.plantSlowness.get()) {
            Holder<MobEffect> slowness = level.registryAccess()
                    .lookupOrThrow(Registries.MOB_EFFECT)
                    .wrapAsHolder(Effects.CUSTOM_SLOWNESS);

            if (livingEntity instanceof Player) {
                livingEntity.addEffect(new MobEffectInstance(slowness, 15, 6, false, false));
            } else {
                livingEntity.addEffect(new MobEffectInstance(slowness, 15, 3, false, false));
            }
        }
    }
}