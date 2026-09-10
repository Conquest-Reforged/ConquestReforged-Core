package com.conquestrefabricated.core;

import dev.architectury.platform.Platform;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Which Conquest module a block came from, and what to call it in a tooltip.
 * <p>
 * This is deliberately <b>not</b> the same thing as a {@link Namespaces namespace}. Every
 * first-party submodule — Classical, Medieval, Modern and the rest — registers its blocks into the
 * single {@code conquest} namespace while shipping under its own mod id, so a block's registry name
 * cannot tell you which module built it. A module has to say so itself:
 * <pre>{@code
 * Modules.register("conquest_modern", "Modern");
 * Modules.scope("conquest_modern", BlockRegistrar::blocks);
 * }</pre>
 * Everything registered inside the scope is tagged, and the tag is shown on the item's advanced
 * tooltip (F3+H). Third party addons whose namespace and mod id match still need this, because the
 * two concepts are tracked separately.
 */
public final class Modules {

    /** Core's own module id, and the tag blocks get when nothing else claims them. */
    public static final String CORE = "conquest";

    /** Prefix for the optional lang key that overrides a module's display name. */
    public static final String KEY_PREFIX = "module.";

    /** Suffix for that lang key: {@code module.<id>.name}. */
    public static final String KEY_SUFFIX = ".name";

    /** Styling of the module line on the advanced tooltip. */
    public static final ChatFormatting[] TOOLTIP_STYLE = {ChatFormatting.BLUE, ChatFormatting.ITALIC};

    private static final Set<String> MODULES = new LinkedHashSet<>();
    private static final Map<String, String> DISPLAY_NAMES = new LinkedHashMap<>();
    private static final Map<String, String> RESOLVED = new LinkedHashMap<>();

    /** Package every Core class lives under; frames here are skipped when inferring. */
    private static final String CORE_PACKAGE = "com.conquestrefabricated";

    /** Caller class name to module id, "" meaning "not a module". Keeps the stack walk cheap. */
    private static final Map<String, String> CALLERS = new LinkedHashMap<>();

    /** Set only inside {@link #scope}; null means "infer from the caller". */
    private static String scoped = null;

    static {
        register(CORE, "Conquest Reforged");
        register("conquest_classical", "Classical");
        register("conquest_medieval", "Medieval");
        register("conquest_modern", "Modern");
        register("conquest_earlymodern", "Early Modern");
        register("conquest_asian", "Asian");
        register("conquest_main", "Main");
    }

    private Modules() {
    }

    /**
     * Registers a module, taking its display name from the mod loader's metadata.
     */
    public static synchronized void register(String moduleId) {
        if (moduleId == null || moduleId.isEmpty()) {
            throw new IllegalArgumentException("Module id must not be empty");
        }
        MODULES.add(moduleId);
    }

    /**
     * Registers a module with an explicit display name, which wins over the loader's metadata.
     *
     * @param moduleId    the module's mod id
     * @param displayName what to call it in a tooltip, eg {@code "Modern"}
     */
    public static synchronized void register(String moduleId, String displayName) {
        register(moduleId);
        DISPLAY_NAMES.put(moduleId, displayName);
        RESOLVED.remove(moduleId);
    }

    /**
     * Runs {@code registrations} with every block created inside tagged as belonging to
     * {@code moduleId}. The previous module is restored afterwards, so modules that register
     * before or after this one are unaffected.
     */
    public static synchronized void scope(String moduleId, Runnable registrations) {
        register(moduleId);
        String previous = scoped;
        scoped = moduleId;
        try {
            registrations.run();
        } finally {
            scoped = previous;
        }
    }

    /**
     * The module blocks are currently being registered under.
     * <p>
     * Inside a {@link #scope} that is whatever the scope named. Outside one it is inferred from
     * the call stack, so a module that never calls {@code scope} is still credited correctly —
     * see {@link #infer()}. Core itself is the last resort.
     */
    public static synchronized String current() {
        if (scoped != null) {
            return scoped;
        }
        return infer();
    }

    /**
     * Works out which module is registering by finding the first frame outside Core and reading
     * the mod id out of its package — {@code com.conquest_classical.content...} is
     * {@code conquest_classical}.
     * <p>
     * This exists so the submodules don't each need a {@link #scope} call, and so a third party
     * addon that forgets one isn't miscredited to Core. The guess is only accepted if a mod with
     * that id is actually loaded, so a package that doesn't follow the convention simply falls
     * through to {@link #CORE} rather than inventing a module.
     */
    private static String infer() {
        try {
            return StackWalker.getInstance().walk(frames -> frames
                    .map(StackWalker.StackFrame::getClassName)
                    .filter(name -> !name.startsWith(CORE_PACKAGE))
                    .map(Modules::moduleIdOf)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(CORE));
        } catch (Throwable ignored) {
            return CORE;
        }
    }

    /**
     * @return the mod id in a class's package root, or null if no such mod is loaded
     */
    private static String moduleIdOf(String className) {
        String cached = CALLERS.get(className);
        if (cached != null) {
            return cached.isEmpty() ? null : cached;
        }

        String candidate = packageRoot(className);
        String result = candidate != null && isLoaded(candidate) ? candidate : "";
        CALLERS.put(className, result);
        return result.isEmpty() ? null : result;
    }

    private static String packageRoot(String className) {
        int first = className.indexOf('.');
        if (first < 0) {
            return null;
        }
        int second = className.indexOf('.', first + 1);
        if (second < 0) {
            return null;
        }
        return className.substring(first + 1, second);
    }

    private static boolean isLoaded(String modId) {
        try {
            return Platform.isModLoaded(modId);
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static synchronized boolean isRegistered(String moduleId) {
        return MODULES.contains(moduleId);
    }

    /**
     * @return every registered module, {@link #CORE} first, then in registration order
     */
    public static synchronized Collection<String> all() {
        return Collections.unmodifiableCollection(new LinkedHashSet<>(MODULES));
    }

    /**
     * Resolves a module's display name: an explicit one if given, else the name the mod loader
     * reports, else the id itself.
     */
    public static synchronized String displayName(String moduleId) {
        return RESOLVED.computeIfAbsent(moduleId, Modules::resolve);
    }

    /**
     * @return the explicit display name, if one was registered
     */
    public static synchronized Optional<String> explicitName(String moduleId) {
        return Optional.ofNullable(DISPLAY_NAMES.get(moduleId));
    }

    /**
     * @return the lang key that overrides a module's display name
     */
    public static String key(String moduleId) {
        return KEY_PREFIX + moduleId + KEY_SUFFIX;
    }

    /**
     * Writes the module line onto an advanced tooltip. Callers gate this on
     * {@code tooltipFlag.isAdvanced()}.
     */
    public static void appendTooltip(String moduleId, Consumer<Component> out) {
        out.accept(Component.translatableWithFallback(key(moduleId), displayName(moduleId))
                .withStyle(TOOLTIP_STYLE));
    }

    private static String resolve(String moduleId) {
        String explicit = DISPLAY_NAMES.get(moduleId);
        if (explicit != null) {
            return explicit;
        }
        try {
            if (Platform.isModLoaded(moduleId)) {
                String name = Platform.getMod(moduleId).getName();
                if (name != null && !name.isEmpty()) {
                    return name;
                }
            }
        } catch (Throwable ignored) {
            // loader unavailable (datagen, tests) — fall through to the id
        }
        return moduleId;
    }
}
