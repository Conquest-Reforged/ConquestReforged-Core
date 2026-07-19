package com.conquestrefabricated.client.models;

import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.model.standalone.UnbakedStandaloneModel;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class LoomStandaloneModels {

    public static final String[] LOOM_TYPES = {
            "simple_warp_weighted_loom", "warp_weighted_loom", "two_beam_loom", "standing_two_beam_loom"
    };

    public static final Map<String, StandaloneModelKey<BlockStateModelPart>> KEYS = new HashMap<>();

    static {
        for (String loomType : LOOM_TYPES) {
            for (int size = 1; size <= 3; size++) {
                for (boolean rotated : new boolean[]{false, true}) {
                    String lookupKey = lookupKey(loomType, size, rotated);
                    KEYS.put(lookupKey, new StandaloneModelKey<>(() -> "conquest:loom_weave/" + lookupKey));
                }
            }
        }
    }

    public static String lookupKey(String loomType, int size, boolean rotated) {
        return loomType + ":" + size + (rotated ? ":45" : ":0");
    }

    private static String modelFileName(String loomType, int size, boolean rotated) {
        String sizeSuffix = switch (size) {
            case 1 -> "_small";
            case 3 -> "_large";
            default -> "";
        };
        String rotSuffix = rotated ? "_45" : "";
        return loomType + "_weave" + sizeSuffix + rotSuffix;
    }

    @SubscribeEvent
    public static void registerStandalone(ModelEvent.RegisterStandalone event) {
        for (String loomType : LOOM_TYPES) {
            for (int size = 1; size <= 3; size++) {
                for (boolean rotated : new boolean[]{false, true}) {
                    String lookupKey = lookupKey(loomType, size, rotated);
                    Identifier modelId = Identifier.fromNamespaceAndPath("conquest", "block/looms/" + modelFileName(loomType, size, rotated));

                    event.register(KEYS.get(lookupKey), new UnbakedStandaloneModel<BlockStateModelPart>() {
                        @Override
                        public void resolveDependencies(ResolvableModel.Resolver resolver) {
                            resolver.markDependency(modelId);
                        }

                        @Override
                        public BlockStateModelPart bake(ModelBaker baker, ModelDebugName debugName) {
                            return SimpleModelWrapper.bake(baker, modelId, BlockModelRotation.IDENTITY);
                        }
                    });
                }
            }
        }
    }
}