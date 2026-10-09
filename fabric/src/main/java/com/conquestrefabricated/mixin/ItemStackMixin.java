package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.spoilage.SpoilageMerge;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The three seams spoilage merging needs; every decision is in {@link SpoilageMerge}.
 *
 * <p>Vanilla merges stacks everywhere through {@code isSameItemSameComponents} and then {@code setCount}
 * ({@code grow} and {@code shrink} both end there), or - for items on the ground - a larger
 * {@code copyWithCount}.</p>
 */
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "isSameItemSameComponents", at = @At("RETURN"), cancellable = true)
    private static void conquest$sameIgnoringFreshness(ItemStack a, ItemStack b, CallbackInfoReturnable<Boolean> result) {
        if (!result.getReturnValue() && SpoilageMerge.relax(a, b)) {
            result.setReturnValue(true);
        }
    }

    @Inject(method = "setCount", at = @At("HEAD"))
    private void conquest$averageWhenGrowing(int count, CallbackInfo callback) {
        SpoilageMerge.growing((ItemStack) (Object) this, count);
    }

    @Inject(method = "copyWithCount", at = @At("RETURN"))
    private void conquest$averageWhenCopiedLarger(int count, CallbackInfoReturnable<ItemStack> result) {
        SpoilageMerge.copied((ItemStack) (Object) this, result.getReturnValue());
    }
}
