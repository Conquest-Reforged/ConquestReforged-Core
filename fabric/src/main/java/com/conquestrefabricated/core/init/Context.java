package com.conquestrefabricated.core.init;

import com.conquestrefabricated.core.Namespaces;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.Identifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

    private static final Map<String, Context> contexts = new ConcurrentHashMap<>();

    private String namespace = "";

    public static Context getInstance() {
        return getInstance(Namespaces.DEFAULT);
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
            // fall back to the requested id if the mod isn't loaded under that container
            context.setNamespace(FabricLoader.getInstance()
                    .getModContainer(id)
                    .map(container -> container.getMetadata().getId())
                    .orElse(id));
            return context;
        });
    }

}
