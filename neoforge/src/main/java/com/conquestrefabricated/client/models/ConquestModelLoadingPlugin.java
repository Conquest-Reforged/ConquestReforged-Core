package com.conquestrefabricated.client.models;

import com.conquestrefabricated.content.blocks.block.decor.Loom;
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

    private static final String[] TEXTURE_NAMES = {
            "loom_weave_white", "loom_weave_red", "loom_weave_black", "loom_weave_gray",
            "loom_weave_light_gray", "loom_weave_white", "loom_weave_brown", "loom_weave_yellow",
            "loom_weave_orange", "loom_weave_pink", "loom_weave_magenta", "loom_weave_purple",
            "loom_weave_blue", "loom_weave_light_blue", "loom_weave_cyan", "loom_weave_green",
            "loom_weave_lime", "loom_weave_baotao", "loom_weave_berber", "loom_weave_black_persian",
            "loom_weave_blue_nain", "loom_weave_brown_oriental", "loom_weave_celtic_knot",
            "loom_weave_kashmiri", "loom_weave_kazakh", "loom_weave_kilim", "loom_weave_nahavand",
            "loom_weave_red_and_blue_sarouk", "loom_weave_red_oriental", "loom_weave_red_pazyryk",
            "loom_weave_shirishabad", "loom_weave_william_morris", "loom_weave_yellow_red_persian"
    };

    private static final Map<Integer, TextureAtlasSprite[]> SPRITE_CACHE = new HashMap<>();

    private static TextureAtlasSprite[] resolveLoomSprites(ModelEvent.ModifyBakingResult event, int size) {
        return SPRITE_CACHE.computeIfAbsent(size, s -> {
            String folder = sizeFolderName(s);
            TextureAtlasSprite[] sprites = new TextureAtlasSprite[TEXTURE_NAMES.length];
            for (int i = 0; i < TEXTURE_NAMES.length; i++) {
                sprites[i] = event.getTextureGetter().apply(
                        Identifier.parse("conquest:block/7_tools/3_utility/loom/weaves/" + folder + "/" + TEXTURE_NAMES[i])
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