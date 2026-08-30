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
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TallGrassBlock.class)
public abstract class FernBlockMixin extends Block {

    public FernBlockMixin(Properties settings) {
        super(settings);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);

        if (entity instanceof LivingEntity livingEntity) {
            Holder<MobEffect> slowness = level.registryAccess()
                    .lookupOrThrow(Registries.MOB_EFFECT)
                    .wrapAsHolder(Effects.CUSTOM_SLOWNESS);

            if (livingEntity instanceof Player && ConquestConfig.INSTANCE.plantSlowness.get()) {
                livingEntity.addEffect(new MobEffectInstance(slowness, 15, 1, false, false));
            }
        }
    }
}
