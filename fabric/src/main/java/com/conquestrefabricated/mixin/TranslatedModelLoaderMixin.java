package com.conquestrefabricated.mixin;

import com.conquestrefabricated.client.models.DuplicateDownUnbakedModel;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.builder.SpecialOffsetType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.model.ModelBakery;

@Mixin(value = ModelBakery.class)
public class TranslatedModelLoaderMixin {

    /*@Shadow
    @Final
    private Map<Identifier, UnbakedModel> unbakedModels;

    @Shadow
    @Final
    private Set<Identifier> modelsToLoad;

    @Inject(method = "putModel", at = @At("HEAD"), cancellable = true)
    private void onPutModel(Identifier id, UnbakedModel unbakedModel, CallbackInfo ci) {
        if (id instanceof ModelIdentifier modelId) {
            if (!modelId.getVariant().equals("inventory") && (modelId.getVariant().contains("offset_toggle=true") || modelId.getVariant().contains("extension_toggle=true"))) {
//                Block block = Registries.BLOCK.get(Identifier.of(modelId.getNamespace() + ":" + modelId.getPath()));


                Identifier blockId = Identifier.of(modelId.getNamespace(), modelId.getPath());
                Block block = Registries.BLOCK.get(blockId);

                SpecialOffset specialOffset = block.getClass().getAnnotation(SpecialOffset.class);
                if (specialOffset != null) {
                    SpecialOffsetType specialOffsetType = specialOffset.offsetType();
                    UnbakedModel newModel;
                    switch (specialOffsetType) {
//                        case Y:
//                            newModel = new DecorUnbakedModel(unbakedModel);
//                            this.unbakedModels.put(id, newModel);
//                            this.modelsToLoad.addAll(newModel.getModelDependencies());
//                            ci.cancel();
//                            break;
//                        case XYZ:
//                            newModel = new XYZUnbakedModel(unbakedModel);
//                            this.unbakedModels.put(id, newModel);
//                            this.modelsToLoad.addAll(newModel.getModelDependencies());
//                            ci.cancel();
//                            break;
//                        case XZ:
//                            newModel = new XZUnbakedModel(unbakedModel);
//                            this.unbakedModels.put(id, newModel);
//                            this.modelsToLoad.addAll(newModel.getModelDependencies());
//                            ci.cancel();
//                            break;
                        case DUPLICATE_DOWN:
                            newModel = new DuplicateDownUnbakedModel(unbakedModel);
                            this.unbakedModels.put(id, newModel);
                            this.modelsToLoad.addAll(newModel.getModelDependencies());
                            ci.cancel();
                            break;
                    }
                }
            }
        }
    }*/

}
