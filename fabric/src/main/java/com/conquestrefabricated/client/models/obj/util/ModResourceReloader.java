package com.conquestrefabricated.client.models.obj.util;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.Identifier;

public abstract class ModResourceReloader implements IdentifiableResourceReloadListener, SimpleSynchronousResourceReloadListener {
    private static final String MOD_ID = "conquest";

    private final Identifier id;

    public ModResourceReloader(String id) {
        this.id = Identifier.fromNamespaceAndPath(MOD_ID, id);
    }

    public ModResourceReloader(Identifier id) {
        this.id = id;
    }

    @Override
    public Identifier getFabricId() {
        return id;
    }
}