package com.conquestrefabricated.client.bind;

import com.conquestrefabricated.api.painting.Painting;
import com.conquestrefabricated.api.painting.PaintingHolder;
import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.api.painting.vanilla.VanillaArt;
import com.conquestrefabricated.api.painting.vanilla.VanillaPainting;
import com.conquestrefabricated.client.gui.painting.PaintingScreen;
import com.conquestrefabricated.core.client.input.BindEvent;
import com.conquestrefabricated.core.client.input.BindListener;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.decoration.painting.PaintingVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class PaintingBindListener implements BindListener {

    @Override
    public void onPress(BindEvent e) {
        if (!e.inGame || e.inGui || !e.player.isPresent()) {
            return;
        }

        e.player.map(Player::getMainHandItem).ifPresent(stack -> {
            if (stack.getItem() instanceof PaintingHolder) {
                PaintingHolder holder = (PaintingHolder) stack.getItem();
                Art<?> art = holder.getArt(stack);
                Painting type = holder.getType(stack);
                if (art == null || type == null) {
                    return;
                }
                PaintingScreen<?> screen = new PaintingScreen<>(stack, type, art);
                Minecraft.getInstance().setScreen(screen);
                return;
            }

            if (stack.getItem() == Items.PAINTING) {
                Registry<PaintingVariant> registry = registry();
                PaintingVariant variant = registry.get(PaintingVariants.ALBAN.identifier())
                        .map(Holder.Reference::value)
                        .orElse(null);
                if (variant == null) {
                    return;
                }
                Art<?> art = Art.find(variant, VanillaArt.all());
                Painting type = VanillaPainting.INSTANCE;
                if (art == null) {
                    return;
                }
                PaintingScreen<?> screen = new PaintingScreen<>(stack, type, art);
                Minecraft.getInstance().setScreen(screen);
            }
        });
    }

    private static Registry<PaintingVariant> registry() {
        RegistryAccess access = Minecraft.getInstance().level.registryAccess();
        return access.lookupOrThrow(Registries.PAINTING_VARIANT);
    }
}