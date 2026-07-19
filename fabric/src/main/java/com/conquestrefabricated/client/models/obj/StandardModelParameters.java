package com.conquestrefabricated.client.models.obj;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.UnbakedModel.GuiLight;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public record StandardModelParameters(@Nullable Identifier parent, TextureSlots.Data textures,
                                      @Nullable ItemTransforms itemTransforms, @Nullable Boolean ambientOcclusion,
                                      @Nullable GuiLight guiLight, @Nullable Transformation rootTransform,
                                      Map<String, Boolean> partVisibility) {
    public static StandardModelParameters parse(JsonObject jsonObject, JsonDeserializationContext context) {
        String parentName = GsonHelper.getAsString(jsonObject, "parent", "");
        Identifier parent = parentName.isEmpty() ? null : Identifier.parse(parentName);

        TextureSlots.Data textures = TextureSlots.Data.EMPTY;
        if (jsonObject.has("textures")) {
            textures = TextureSlots.parseTextureMap(GsonHelper.getAsJsonObject(jsonObject, "textures"));
        }

        ItemTransforms itemTransforms = null;
        if (jsonObject.has("display")) {
            itemTransforms = context.deserialize(GsonHelper.getAsJsonObject(jsonObject, "display"), ItemTransforms.class);
        }

        Boolean ambientOcclusion = jsonObject.has("ambientocclusion") ? GsonHelper.getAsBoolean(jsonObject, "ambientocclusion") : null;

        GuiLight guiLight = jsonObject.has("gui_light") ? GuiLight.getByName(GsonHelper.getAsString(jsonObject, "gui_light")) : null;

        Transformation rootTransform = ModProperties.deserializeRootTransform(jsonObject, context);
        Map<String, Boolean> partVisibility = ModProperties.deserializePartVisibility(jsonObject);

        return new StandardModelParameters(parent, textures, itemTransforms, ambientOcclusion, guiLight, rootTransform, partVisibility);
    }
}