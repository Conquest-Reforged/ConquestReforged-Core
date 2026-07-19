package com.conquestrefabricated.mixin;

import net.minecraft.client.resources.model.ModelBakery;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ModelBakery.class)
public class ModelLoaderMixin {

//    @Final
//    private static final ModelResourceLocation WARP_WEIGHTED_LOOM = new ModelResourceLocation (ResourceLocation.fromNamespaceAndPath("conquest", "warp_weighted_loom"), "");
//    @Final
//    private static final ModelResourceLocation  SIMPLE_WARP_WEIGHTED_LOOM = new ModelResourceLocation (ResourceLocation.fromNamespaceAndPath("conquest", "simple_warp_weighted_loom"), "");
//    @Final
//    private static final ModelResourceLocation  TWO_BEAM_LOOM = new ModelResourceLocation (ResourceLocation.fromNamespaceAndPath("conquest", "two_beam_loom"), "");
//    @Final
//    private static final ModelResourceLocation  STANDING_TWO_BEAM_LOOM = new ModelResourceLocation (ResourceLocation.fromNamespaceAndPath("conquest", "standing_two_beam_loom"), "");
//
//    @Shadow
//    @Final
//    private Map<ModelResourceLocation, UnbakedModel> topLevelModels;
//
//    @Shadow
//    UnbakedModel getModel(ResourceLocation location) {
//        throw new AssertionError();
//    }
//
//    private UnbakedModel getDependentModel(String path) {
//        return this.getModel(ResourceLocation.fromNamespaceAndPath("conquest", path));
//    }
//
//    @Inject(method = "registerModel", at = @At("HEAD"), cancellable = true)
//    private void onPutModel(ModelResourceLocation modelId, UnbakedModel unbakedModel, CallbackInfo ci) {
//        if (!modelId.getVariant().equals("inventory") && (modelId.getVariant().contains("offset_toggle=true") || modelId.getVariant().contains("extension_toggle=true"))) {
//            Block block = BuiltInRegistries.BLOCK.get(modelId.id());
//
//            SpecialOffset specialOffset = block.getClass().getAnnotation(SpecialOffset.class);
//            if (specialOffset != null) {
//                SpecialOffsetType specialOffsetType = specialOffset.offsetType();
//                if (specialOffsetType == SpecialOffsetType.DUPLICATE_DOWN) {
//                    UnbakedModel newModel = new DuplicateDownUnbakedModel(unbakedModel);
//                    this.topLevelModels.put(modelId, newModel);
//                    ci.cancel();
//                    return;
//                }
//            }
//        }
//
//
//        String idString = modelId.toString();
//        if (modelId.id().getPath().contains("simple_warp_weighted_loom") && idString.contains("has_thread=true")) {
//            this.topLevelModels.put(modelId, handleLoomModel(idString, "simple_warp_weighted_loom"));
//            ci.cancel();
//        } else if (modelId.id().getPath().contains("standing_two_beam_loom") && idString.contains("has_thread=true")) {
//            this.topLevelModels.put(modelId, handleLoomModel(idString, "standing_two_beam_loom"));
//            ci.cancel();
//        } else if (modelId.id().getPath().contains("two_beam_loom") && idString.contains("has_thread=true")) {
//            this.topLevelModels.put(modelId, handleLoomModel(idString, "two_beam_loom"));
//            ci.cancel();
//        } else if (modelId.id().getPath().contains("warp_weighted_loom") && idString.contains("has_thread=true")) {
//            this.topLevelModels.put(modelId, handleLoomModel(idString, "warp_weighted_loom"));
//            ci.cancel();
//        }
//    }
//
//    private UnbakedModel handleLoomModel(String idString, String loomType) {
//        UnbakedModel baseModelSmall = getDependentModel("block/looms/" + loomType + "_weave_small");
//        UnbakedModel extraModelSmall = getDependentModel("block/looms/" + loomType + "_threaded_small");
//        UnbakedModel baseModelSmall45 = getDependentModel("block/looms/" + loomType + "_weave_small_45");
//        UnbakedModel extraModelSmall45 = getDependentModel("block/looms/" + loomType + "_threaded_small_45");
//
//        UnbakedModel baseModel = getDependentModel("block/looms/" + loomType + "_weave");
//        UnbakedModel extraModel = getDependentModel("block/looms/" + loomType + "_threaded");
//        UnbakedModel baseModel45 = getDependentModel("block/looms/" + loomType + "_weave_45");
//        UnbakedModel extraModel45 = getDependentModel("block/looms/" + loomType + "_threaded_45");
//
//        UnbakedModel baseModelLarge = getDependentModel("block/looms/" + loomType + "_weave_large");
//        UnbakedModel extraModelLarge = getDependentModel("block/looms/" + loomType + "_threaded_large");
//        UnbakedModel baseModelLarge45 = getDependentModel("block/looms/" + loomType + "_weave_large_45");
//        UnbakedModel extraModelLarge45 = getDependentModel("block/looms/" + loomType + "_threaded_large_45");
//
//        if (idString.contains("size=1")) {
//            if (idString.contains("position=1")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y0);
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y90);
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y180);
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y270);
//                } else {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=2")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelSmall45, extraModelSmall45, "small", BlockModelRotation.X0_Y0);
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelSmall45, extraModelSmall45, "small", BlockModelRotation.X0_Y90);
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelSmall45, extraModelSmall45, "small", BlockModelRotation.X0_Y180);
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelSmall45, extraModelSmall45, "small", BlockModelRotation.X0_Y270);
//                } else {
//                    return new LoomUnbakedModel(baseModelSmall45, extraModelSmall45, "small", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=3")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y0, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y90, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y180, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y270, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=4")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y0, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y90, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y180, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y270, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else {
//                    return new LoomUnbakedModel(baseModelSmall, extraModelSmall, "small", BlockModelRotation.X0_Y0);
//                }
//            }
//        } else if (idString.contains("size=3")) {
//            if (idString.contains("position=1")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y0);
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y90);
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y180);
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y270);
//                } else {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=2")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelLarge45, extraModelLarge45, "large", BlockModelRotation.X0_Y0);
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelLarge45, extraModelLarge45, "large", BlockModelRotation.X0_Y90);
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelLarge45, extraModelLarge45, "large", BlockModelRotation.X0_Y180);
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelLarge45, extraModelLarge45, "large", BlockModelRotation.X0_Y270);
//                } else {
//                    return new LoomUnbakedModel(baseModelLarge45, extraModelLarge45, "large", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=3")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y0, new Vector3f(0.9f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y90, new Vector3f(0.9f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y180, new Vector3f(0.9f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y270, new Vector3f(0.9f, 0.0f, 0.0f));
//                } else {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=4")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y0, new Vector3f(-0.9f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y90, new Vector3f(-0.9f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y180, new Vector3f(-0.9f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y270, new Vector3f(-0.9f, 0.0f, 0.0f));
//                } else {
//                    return new LoomUnbakedModel(baseModelLarge, extraModelLarge, "large", BlockModelRotation.X0_Y0);
//                }
//            }
//        } else if (idString.contains("size=2")) {
//            if (idString.contains("position=1")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y0);
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y90);
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y180);
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y270);
//                } else {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=2")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModel45, extraModel45, "medium", BlockModelRotation.X0_Y0);
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModel45, extraModel45, "medium", BlockModelRotation.X0_Y90);
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModel45, extraModel45, "medium", BlockModelRotation.X0_Y180);
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModel45, extraModel45, "medium", BlockModelRotation.X0_Y270);
//                } else {
//                    return new LoomUnbakedModel(baseModel45, extraModel45, "medium", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=3")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y0, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y90, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y180, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y270, new Vector3f(0.5f, 0.0f, 0.0f));
//                } else {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y0);
//                }
//            } else if (idString.contains("position=4")) {
//                if (idString.contains("facing=north")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y0, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=east")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y90, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=south")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y180, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else if (idString.contains("facing=west")) {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y270, new Vector3f(-0.5f, 0.0f, 0.0f));
//                } else {
//                    return new LoomUnbakedModel(baseModel, extraModel, "medium", BlockModelRotation.X0_Y0);
//                }
//            }
//        }
//
//        return getDependentModel("cube_all");
//    }
}
