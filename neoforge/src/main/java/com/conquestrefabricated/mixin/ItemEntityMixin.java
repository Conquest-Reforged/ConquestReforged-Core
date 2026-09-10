package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.lime.Lime;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Lets quicklime slake where it lies.
 *
 * <p>An item entity has no hook of its own for "something happened to me", and the two loaders do not
 * agree on one, so this is the seam. It stays a one-liner: everything it decides lives in
 * {@link Lime#slakeInWater}, shared by both loaders.</p>
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void conquest$slakeQuicklime(CallbackInfo callback) {
        Lime.slakeInWater((ItemEntity) (Object) this);
    }
}
