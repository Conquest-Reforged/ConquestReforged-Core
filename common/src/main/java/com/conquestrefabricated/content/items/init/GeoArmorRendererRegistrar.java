package com.conquestrefabricated.content.items.init;

import com.conquestrefabricated.content.items.item.ArmorItem;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.model.DefaultedGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.llamalad7.mixinextras.lib.apache.commons.mutable.MutableObject;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class GeoArmorRendererRegistrar {
    private GeoArmorRendererRegistrar() {}

    private static final Map<Identifier, String> MODEL_OVERRIDES = new HashMap<>();

    /// Called by any submod's client init to register a model override for one of its items.
    /// Must be called before registerAll().
    public static void registerModelOverride(Identifier itemId, String modelName) {
        MODEL_OVERRIDES.put(itemId, modelName);
    }

    public static void registerAll() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof ArmorItem)
                .forEach(item -> registerOne(BuiltInRegistries.ITEM.getKey(item), (ArmorItem) item));
    }

    private static void registerOne(Identifier itemId, ArmorItem item) {
        String modelName = MODEL_OVERRIDES.getOrDefault(itemId, defaultModelNameFor(item));
        register(item.geoRenderProvider, item, itemId.getNamespace(), modelName);
    }

    private static String defaultModelNameFor(ArmorItem item) {
        return switch (item.getArmorType()) {
            case HELMET -> "helmet_generic";
            case BODY -> "chestplate_generic";
            case CHESTPLATE -> "chestplate_generic";
            case LEGGINGS -> "leggings_generic";
            case BOOTS -> "boots_generic";
        };
    }

    private static <T extends Item & GeoItem> void register(
            MutableObject<GeoRenderProvider> slot, T item, String assetNamespace, String modelName) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);

        DefaultedGeoModel<T> model = new DefaultedGeoModel<T>(itemId) {
            @Override
            protected String subtype() {
                return "armor";
            }
        }.withAltModel(Identifier.fromNamespaceAndPath(assetNamespace, modelName));

        slot.setValue(new GeoRenderProvider() {
            private final GeoArmorRenderer<T, HumanoidRenderState> renderer = new GeoArmorRenderer<>(model);

            @Override
            public @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack stack, EquipmentSlot slot) {
                return this.renderer;
            }
        });
    }
}