package com.conquestrefabricated.mixin;

import com.conquestrefabricated.core.interfaces.PackRepositoryAddPackFinder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryAddPackFinder {
    @Shadow @Final private Set<RepositorySource> sources;

    @Override
    public synchronized void addPackFinder(RepositorySource packFinder) {
        this.sources.add(packFinder);
    }
}
