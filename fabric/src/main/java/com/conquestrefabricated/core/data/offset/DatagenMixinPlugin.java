package com.conquestrefabricated.core.data.offset;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Restricts the datagen mixin config to the data generation run. Fabric's datagen entrypoint sets
 * the {@code fabric-api.datagen} system property, which the {@code refabricatedDataGenClient} run
 * config passes as a VM arg.
 *
 * <p>Without this gate the offset mixin would ship to players and wrap the same
 * {@code SimpleModelState} codec that Polytone wraps at runtime.</p>
 */
public class DatagenMixinPlugin implements IMixinConfigPlugin {

    private static final boolean DATAGEN = System.getProperty("fabric-api.datagen") != null;

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return DATAGEN;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
