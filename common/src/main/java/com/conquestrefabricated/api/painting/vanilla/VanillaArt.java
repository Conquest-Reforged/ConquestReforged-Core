package com.conquestrefabricated.api.painting.vanilla;

import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.api.painting.art.ArtRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class VanillaArt implements Art<PaintingVariant> {

    private final PaintingVariant art;

    private VanillaArt(PaintingVariant art) {
        this.art = art;
    }

    private static Registry<PaintingVariant> registry() {
        return Minecraft.getInstance().level.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT);
    }

    public static List<Art<PaintingVariant>> all() {
        List<Art<PaintingVariant>> list = new ArrayList<>();
        registry().forEach(variant -> list.add(new VanillaArt(variant)));
        return list;
    }

    @Override
    public List<Art<PaintingVariant>> getAll() {
        return all();
    }

    @Override
    public String getName() {
        return registry().getKey(art) + "";
    }

    @Override
    public String getDisplayName(String parent) {
        return registry().getResourceKey(art).get().registry().getPath();
    }

    public static Art<PaintingVariant> fromName(String name) {
        PaintingVariant type = registry().get(Identifier.parse(name)).get().value();
        return Art.find(type, all());
    }

    @Override
    public int u() {
        return 0;
    }

    @Override
    public int v() {
        return 0;
    }

    @Override
    public int width() {
        return art.width();
    }

    @Override
    public int height() {
        return art.height();
    }

    @Override
    public int textureWidth() {
        return art.width();
    }

    @Override
    public int textureHeight() {
        return art.height();
    }

    @Override
    public PaintingVariant getReference() {
        return art;
    }

    @Override
    public ArtRenderer getRenderer() {
        return ArtRenderer.VANILLA;
    }
}
