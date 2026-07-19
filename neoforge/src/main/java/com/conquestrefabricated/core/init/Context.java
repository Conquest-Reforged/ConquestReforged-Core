package com.conquestrefabricated.core.init;

import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static final Map<ModContainer, Context> contexts = new ConcurrentHashMap<>();

    private String namespace = "";

    public static Context getInstance() {
        return getCurrentContext();
    }

    public synchronized String getNamespace() {
        return namespace;
    }

    public synchronized Identifier newResourceLocation(String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public synchronized void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    private static Context getCurrentContext() {
        ModContainer current = ModLoadingContext.get().getActiveContainer();
        return contexts.computeIfAbsent(current, k -> {
            Context context = new Context();
            context.setNamespace(k.getNamespace());
            return context;
        });
    }

}
