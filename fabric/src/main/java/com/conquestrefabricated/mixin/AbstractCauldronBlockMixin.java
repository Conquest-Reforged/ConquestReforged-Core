package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.cauldron.CauldronBlocks;
import com.conquestrefabricated.content.salt.Salt;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes vanilla's cauldron cook, as Conquest's pots do. Everything it decides is in
 * {@link CauldronBlocks}: this is only the two seams, a right-click with something in hand (sea water in,
 * brine kept in) and an empty-handed one (opens the screen).
 *
 * <p>Vanilla's is a different block empty and with water, so what is cooking in it is kept by position,
 * not on a block entity - see {@code CauldronStore}. Lava and powder snow cauldrons are left alone.</p>
 */
@Mixin(AbstractCauldronBlock.class)
public abstract class AbstractCauldronBlockMixin extends BlockBehaviour {

    protected AbstractCauldronBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void conquest$pourSea(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                  InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> result) {
        if (Salt.enabled()) {
            InteractionResult handled = CauldronBlocks.useItem(stack, state, level, pos, player, hand);
            if (handled != null) {
                result.setReturnValue(handled);
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        return CauldronBlocks.open(state, level, pos, player);
    }
}
