package com.conquestrefabricated.core.interfaces;

import net.minecraft.server.packs.repository.RepositorySource;

public interface PackRepositoryAddPackFinder {

    default void addPackFinder(RepositorySource packFinder) {}

}
