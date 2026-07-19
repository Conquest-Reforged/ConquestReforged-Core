package com.conquestrefabricated.mixin;

import com.conquestrefabricated.core.interfaces.PackRepositoryAddPackFinder;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin implements PackRepositoryAddPackFinder {
    @Shadow @Final private Set<RepositorySource> providers;

    @Override
    public synchronized void addPackFinder(RepositorySource packFinder) {
        this.providers.add(packFinder);
    }
}
