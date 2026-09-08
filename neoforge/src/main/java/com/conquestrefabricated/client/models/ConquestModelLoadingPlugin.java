package com.conquestrefabricated.client.models;

import com.conquestrefabricated.content.blocks.block.decor.Loom;
import com.conquestrefabricated.content.loom.LoomWeaves;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class ConquestModelLoadingPlugin {

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        Map<BlockState, BlockStateModel> blockStateModels = event.getBakingResult().blockStateModels();

        for (Map.Entry<BlockState, BlockStateModel> entry : blockStateModels.entrySet()) {
            BlockState state = entry.getKey();
            BlockStateModel model = entry.getValue();
            Block block = state.getBlock();

            if (block instanceof Loom) {
                int size = state.getValue(Loom.SIZE);
                TextureAtlasSprite[] sprites = resolveLoomSprites(event, size);
                entry.setValue(new LoomBlockStateModel(model, sprites));
                continue;
            }

            boolean hasOffsetToggle = state.hasProperty(ModBlockProperties.OFFSET_TOGGLE);
            boolean hasExtensionToggle = state.hasProperty(ModBlockProperties.EXTENSION_TOGGLE);
            if (!hasOffsetToggle && !hasExtensionToggle) {
                continue;
            }

            SpecialOffset specialOffset = block.getClass().getAnnotation(SpecialOffset.class);
            if (specialOffset == null) {
                continue;
            }

            switch (specialOffset.offsetType()) {
                case DUPLICATE_DOWN -> entry.setValue(new DuplicateDownBlockstateModel(model));
                default -> {}
            }
        }
    }

    private static final Map<Integer, TextureAtlasSprite[]> SPRITE_CACHE = new HashMap<>();

    private static TextureAtlasSprite[] resolveLoomSprites(ModelEvent.ModifyBakingResult event, int size) {
        return SPRITE_CACHE.computeIfAbsent(size, s -> {
            String folder = sizeFolderName(s);
            TextureAtlasSprite[] sprites = new TextureAtlasSprite[LoomWeaves.TEXTURE_NAMES.length];
            for (int i = 0; i < LoomWeaves.TEXTURE_NAMES.length; i++) {
                sprites[i] = event.getTextureGetter().apply(
                        Identifier.parse("conquest:block/7_tools/3_utility/loom/weaves/" + folder + "/" + LoomWeaves.TEXTURE_NAMES[i])
                );
            }
            return sprites;
        });
    }

    private static String sizeFolderName(int size) {
        return switch (size) {
            case 1 -> "small";
            case 2 -> "medium";
            case 3 -> "large";
            default -> throw new IllegalArgumentException("Unknown loom size: " + size);
        };
    }
}