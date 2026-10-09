package com.conquestrefabricated.content.items.init;

import com.conquestrefabricated.content.items.item.ArmorItem;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.model.DefaultedGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.llamalad7.mixinextras.lib.apache.commons.mutable.MutableObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        String override = MODEL_OVERRIDES.get(itemId);
        if (override == null && item.getArmorType() == ArmorType.HELMET) {
            registerHelmet(item.geoRenderProvider, item, itemId.getNamespace());
            return;
        }
        String modelName = override != null ? override : defaultModelNameFor(item);
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

        setRenderer(slot, model);
    }

    /// Helmets come in two texture layouts: the vanilla 64x16-UV layout (textures 4:1, e.g. 128x32), which needs
    /// `helmet_vanilla`, and the square layout used by `helmet_generic`. The texture's aspect ratio decides,
    /// read once on first render since resources aren't loaded at registration time.
    private static <T extends Item & GeoItem> void registerHelmet(
            MutableObject<GeoRenderProvider> slot, T item, String assetNamespace) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
        Identifier genericModel = Identifier.fromNamespaceAndPath(assetNamespace, "helmet_generic");
        Identifier vanillaModel = Identifier.fromNamespaceAndPath(assetNamespace, "helmet_vanilla");

        DefaultedGeoModel<T> model = new DefaultedGeoModel<T>(itemId) {
            private @Nullable Identifier resolvedModel;

            @Override
            protected String subtype() {
                return "armor";
            }

            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                if (this.resolvedModel == null) {
                    Identifier modelId = isWideTexture(getTextureResource(renderState)) ? vanillaModel : genericModel;
                    this.resolvedModel = buildFormattedModelPath(modelId);
                }
                return this.resolvedModel;
            }
        };

        setRenderer(slot, model);
    }

    private static boolean isWideTexture(Identifier texture) {
        return Minecraft.getInstance().getResourceManager().getResource(texture).map(resource -> {
            try (InputStream in = resource.open()) {
                // PNG: 8-byte signature, 8-byte IHDR chunk header, then big-endian width and height.
                DataInputStream data = new DataInputStream(in);
                data.skipNBytes(16);
                int width = data.readInt();
                int height = data.readInt();
                return width >= height * 2;
            } catch (IOException e) {
                return false;
            }
        }).orElse(false);
    }

    private static <T extends Item & GeoItem> void setRenderer(MutableObject<GeoRenderProvider> slot, DefaultedGeoModel<T> model) {
        slot.setValue(new GeoRenderProvider() {
            private final GeoArmorRenderer<T, HumanoidRenderState> renderer = new GeoArmorRenderer<>(model);

            @Override
            public @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(ItemStack stack, EquipmentSlot slot) {
                return this.renderer;
            }
        });
    }
}