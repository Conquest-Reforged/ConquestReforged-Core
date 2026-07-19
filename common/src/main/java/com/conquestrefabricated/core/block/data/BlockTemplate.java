package com.conquestrefabricated.core.block.data;

import com.conquestrefabricated.core.asset.annotation.*;
import com.conquestrefabricated.core.asset.override.EmptyOverride;
import com.conquestrefabricated.core.asset.override.MapOverride;
import com.conquestrefabricated.core.asset.override.SingleOverride;
import com.conquestrefabricated.core.asset.template.JsonOverride;
import com.conquestrefabricated.core.block.builder.BlockName;
import com.conquestrefabricated.core.util.RenderLayer;
import com.conquestrefabricated.core.util.log.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BlockTemplate {

    private final State state;
    private final Model itemModel;
    private final Model[] blockModels;
    private final Recipe[] recipes;
    private final Render render;
    private final boolean plural;

    BlockTemplate(Class<?> type) {
        Assets assets = type.getAnnotation(Assets.class);
        this.state = assets != null ? assets.state() : null;
        this.itemModel = assets != null ? assets.item() : null;
        this.blockModels = assets != null ? assets.block() : null;
        this.recipes = assets != null ? assets.recipe() : null;
        this.render = BlockTemplate.getRender(type, assets);
        this.plural = state != null && state.plural();
    }

    public RenderLayer getRenderLayer() {
        if (render == null) {
            return RenderLayer.UNDEFINED;
        }
        return render.value();
    }

    public Identifier getRegistryName(BlockName name) {
        if (state == null) {
            return Identifier.fromNamespaceAndPath(name.getNamespace(), name.format("%s", plural));
        }
        return Identifier.fromNamespaceAndPath(name.getNamespace(), name.format(state.name(), plural));
    }

    private JsonOverride getOverrides(BlockName name, Model[] replacements) {
        if (replacements.length == 0) {
            return EmptyOverride.EMPTY;
        }

        if (replacements.length == 1) {
            String find = replacements[0].template();
            String replace = name.namespaceFormat(replacements[0].name(), plural);
            return new SingleOverride("model", new JsonPrimitive(find), new JsonPrimitive(replace));
        }

        Map<JsonElement, JsonElement> overrides = new HashMap<>(replacements.length);
        for (Model model : replacements) {
            String find = model.template();
            String replace = name.namespaceFormat(model.name(), model.plural());
            overrides.put(new JsonPrimitive(find), new JsonPrimitive(replace));
        }

        return new MapOverride("model", overrides);
    }

    private JsonOverride getOverrides(BlockName name, Ingredient[] ingredients) {
        if (ingredients.length == 0) {
            return EmptyOverride.EMPTY;
        }

        if (ingredients.length == 1) {
            String find = ingredients[0].template();
            String replace = getIngredient(name, ingredients[0]);
            if (replace.isEmpty()) {
                return EmptyOverride.EMPTY;
            }
            return new SingleOverride("item", new JsonPrimitive(find), new JsonPrimitive(replace));
        }

        Map<JsonElement, JsonElement> overrides = new HashMap<>(ingredients.length);
        for (Ingredient ingredient : ingredients) {
            String find = Identifier.parse(ingredient.template()).toString();
            String replace = getIngredient(name, ingredient);
            if (replace.isEmpty()) {
                return EmptyOverride.EMPTY;
            }
            overrides.put(new JsonPrimitive(find), new JsonPrimitive(replace));
        }

        return new MapOverride("item", overrides);
    }

    private String getIngredient(BlockName name, Ingredient ingredient) {
        String itemName = name.format(ingredient.name(), ingredient.plural());
        if (BuiltInRegistries.ITEM.containsKey(Identifier.fromNamespaceAndPath(name.getNamespace(), itemName))) {
            Log.debug(" Found ingredient {}:{}", name.getNamespace(), itemName);
            return name.getNamespace() + ':' + itemName;
        }
        if (BuiltInRegistries.ITEM.containsKey(Identifier.parse(itemName))) {
            Log.debug(" Found vanilla ingredient minecraft:{}", itemName);
            return "minecraft:" + itemName;
        }
        Log.error(" Unknown ingredient {}", itemName);
        return "";
    }

    private <T> T[] push(T[] t, T value) {
        T[] array = Arrays.copyOf(t, t.length + 1);
        array[array.length - 1] = value;
        return array;
    }

    private static Render getRender(Class<?> type, @Nullable Assets assets) {
        while (type != Object.class) {
            // annotation on the class overrides any in the assets annotation
            Render render = type.getAnnotation(Render.class);
            if (render != null) {
                return render;
            }

            // if assets exists and render has been defined, use it
            if (assets != null && assets.render().value() != RenderLayer.UNDEFINED) {
                return assets.render();
            }

            // get super class & it's Asset annotation
            type = type.getSuperclass();
            assets = type.getAnnotation(Assets.class);
        }
        return null;
    }

    private static Ingredient createIngredient(String name, String template, boolean plrual) {
        return new Ingredient() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Ingredient.class;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String template() {
                return template;
            }

            @Override
            public boolean plural() {
                return plrual;
            }
        };
    }
}
