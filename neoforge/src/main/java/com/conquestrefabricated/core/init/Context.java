package com.conquestrefabricated.core.init;

import com.conquestrefabricated.core.Namespaces;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static final Map<String, Context> contexts = new ConcurrentHashMap<>();

    private String namespace = "";

    public static Context getInstance() {
        ModContainer current = ModLoadingContext.get().getActiveContainer();
        String namespace = current == null ? Namespaces.DEFAULT : current.getNamespace();
        return getInstance(namespace);
    }

    /**
     * @param namespace the mod id to build resource locations for; addons pass their own
     */
    public static Context getInstance(String namespace) {
        return getCurrentContext(namespace);
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

    private static Context getCurrentContext(String namespace) {
        return contexts.computeIfAbsent(namespace, id -> {
            Context context = new Context();
            context.setNamespace(id);
            return context;
        });
    }

}
