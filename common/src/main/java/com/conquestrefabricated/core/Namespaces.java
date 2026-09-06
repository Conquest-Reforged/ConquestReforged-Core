package com.conquestrefabricated.core;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.resources.Identifier;

/**
 * Registry of the namespaces that Conquest Reforged treats as its own content.
 * <p>
 * Everything in the core used to assume the single namespace {@code conquest}. Third party
 * addons register their own namespace here, after which the core's block builder, creative
 * tabs, asset generation and datagen will pick their content up alongside ours.
 * <p>
 * {@link #DEFAULT} is always registered and is always the namespace used when a caller
 * doesn't specify one, so the first-party submodules keep behaving exactly as before.
 */
public final class Namespaces {

    /**
     * The namespace assumed whenever a caller doesn't give one.
     */
    public static final String DEFAULT = "conquest";

    private static final Set<String> NAMESPACES = new LinkedHashSet<>();
    private static final Map<String, String> FALLBACK_TABS = new LinkedHashMap<>();

    static {
        NAMESPACES.add(DEFAULT);
        // The utility tab is where core's own leftovers land.
        FALLBACK_TABS.put(DEFAULT, "rr_utility");
        // Arms of Conquest predates this API and has no registration call of its own.
        register("conquest_armory", "pp_weapons_and_tools");
    }

    private Namespaces() {
    }

    /**
     * Registers an addon namespace. Safe to call more than once; call it before registering
     * any blocks or items, ie from the mod's initializer.
     *
     * @param namespace the addon's namespace, eg the mod id
     */
    public static synchronized void register(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            throw new IllegalArgumentException("Namespace must not be empty");
        }
        NAMESPACES.add(namespace);
    }

    /**
     * Registers an addon namespace and the creative tab that any of its items not listed in a
     * group file should fall back to.
     *
     * @param namespace the addon's namespace, eg the mod id
     * @param tabLabel  the label of a Conquest creative tab, eg {@code rr_utility}
     */
    public static synchronized void register(String namespace, String tabLabel) {
        register(namespace);
        FALLBACK_TABS.put(namespace, tabLabel);
    }

    public static synchronized boolean isRegistered(String namespace) {
        return NAMESPACES.contains(namespace);
    }

    /**
     * @return every registered namespace, {@link #DEFAULT} first, then in registration order
     */
    public static synchronized Collection<String> all() {
        return Collections.unmodifiableCollection(new LinkedHashSet<>(NAMESPACES));
    }

    public static Stream<String> stream() {
        return all().stream();
    }

    /**
     * @return the creative tab label items of this namespace fall back to, if one was declared
     */
    public static synchronized Optional<String> fallbackTab(String namespace) {
        return Optional.ofNullable(FALLBACK_TABS.get(namespace));
    }

    /**
     * @return the namespaces that fall back to the given tab label, {@link #DEFAULT} first
     */
    public static synchronized Collection<String> withFallbackTab(String tabLabel) {
        Set<String> found = new LinkedHashSet<>();
        for (String namespace : NAMESPACES) {
            if (tabLabel.equals(FALLBACK_TABS.get(namespace))) {
                found.add(namespace);
            }
        }
        return found;
    }

    /**
     * Splits the namespace off a {@code namespace:path} string, falling back to {@link #DEFAULT}.
     */
    public static String namespaceOf(String name) {
        return namespaceOf(name, DEFAULT);
    }

    public static String namespaceOf(String name, String defaultNamespace) {
        int i = name.indexOf(':');
        return i == -1 ? defaultNamespace : name.substring(0, i);
    }

    /**
     * Strips the namespace off a {@code namespace:path} string.
     */
    public static String pathOf(String name) {
        int i = name.indexOf(':');
        return i == -1 ? name : name.substring(i + 1);
    }

    /**
     * Parses {@code namespace:path}, defaulting the namespace to {@link #DEFAULT}.
     */
    public static Identifier id(String name) {
        return id(name, DEFAULT);
    }

    /**
     * Parses {@code namespace:path}, defaulting the namespace to the one given.
     */
    public static Identifier id(String name, String defaultNamespace) {
        return Identifier.fromNamespaceAndPath(namespaceOf(name, defaultNamespace), pathOf(name));
    }

    /**
     * Prefixes {@code name} with {@code namespace} unless it already carries one.
     */
    public static String qualify(String name, String namespace) {
        return name.indexOf(':') == -1 ? namespace + ':' + name : name;
    }
}
