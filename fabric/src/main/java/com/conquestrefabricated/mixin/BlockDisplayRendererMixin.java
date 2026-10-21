package com.conquestrefabricated.mixin;

import com.conquestrefabricated.content.treefelling.TreeFelling;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.state.BlockDisplayEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Display;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * A block display is tinted with each tint source's colour out of the world (the plain default foliage green,
 * say), not the colour the block had where it stood. A felled tree's displays carry where their block stood
 * (see {@code FallingTree#marker}), so its leaves keep the colour they had on the tree.
 */
@Mixin(DisplayRenderer.BlockDisplayRenderer.class)
public class BlockDisplayRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Display$BlockDisplay;Lnet/minecraft/client/renderer/entity/state/BlockDisplayEntityRenderState;F)V",
            at = @At("TAIL"))
    private void conquest$tintFelledBlock(Display.BlockDisplay display, BlockDisplayEntityRenderState state, float partialTick, CallbackInfo ci) {
        int marker = display.getGlowColorOverride();
        if ((marker & 0xFF000000) != TreeFelling.DISPLAY_MARKER || display.blockRenderState() == null
                || !(display.level() instanceof ClientLevel level)) {
            return;
        }
        IntList tints = state.blockModel.tintLayers();
        if (tints.isEmpty()) {
            return;
        }
        BlockState block = display.blockRenderState().blockState();
        BlockPos pos = display.blockPosition().offset((byte) (marker >> 16), (byte) (marker >> 8), (byte) marker);
        List<BlockTintSource> sources = Minecraft.getInstance().getBlockColors().getTintSources(block);
        for (int i = 0; i < tints.size() && i < sources.size(); i++) {
            tints.set(i, sources.get(i).colorInWorld(block, level, pos));
        }
    }
}
