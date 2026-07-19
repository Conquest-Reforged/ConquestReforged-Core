package com.conquestrefabricated;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@EventBusSubscriber(modid = "conquest", value = Dist.CLIENT)
public class ModBuiltinRP {
    private static @Nullable Path resolveResourcePackPath(String packId) {
        var contents = ModList.get().getModFileById("conquest").getFile().getContents();
        for (Path root : contents.getContentRoots()) {
            Path candidate = root.resolve("resourcepacks").resolve(packId);
            if (java.nio.file.Files.isDirectory(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onGatherData(AddPackFindersEvent event) {
        // Register resource pack
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            ModList.get().getModContainerById("conquest").ifPresent(modContainer -> {
                event.addRepositorySource((packConsumer) -> {
                    String packId = "rp_crrp";
                    String fullPackId = "conquest" + ":" + packId;

                    Path resourcePath = resolveResourcePackPath(packId);
                    if (resourcePath == null) {
                        return; // pack not found in this mod's jar/contents
                    }

                    Pack.ResourcesSupplier resourceSupplier = new Pack.ResourcesSupplier() {
                        @Override
                        public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo locationInfo) {
                            return new PathPackResources(locationInfo, resourcePath);
                        }

                        @Override
                        public net.minecraft.server.packs.PackResources openFull(PackLocationInfo locationInfo, Pack.Metadata metadata) {
                            return new PathPackResources(locationInfo, resourcePath);
                        }
                    };
                    PackLocationInfo locationInfo = new PackLocationInfo(
                            fullPackId,
                            Component.literal("CRRP"),
                            PackSource.BUILT_IN,
                            java.util.Optional.empty()
                    );

                    Pack pack = Pack.readMetaAndCreate(
                            locationInfo,
                            resourceSupplier,
                            PackType.CLIENT_RESOURCES,
                            new PackSelectionConfig(true, Pack.Position.TOP, false)
                    );

                    if (pack != null) {
                        packConsumer.accept(pack);
                    }
                });
            });

            //JEI COMPAT
            ModList.get().getModContainerById("jei").ifPresent(modContainer -> {
                event.addRepositorySource((packConsumer) -> {
                    String packId = "conquest_jei";
                    String fullPackId = "conquest" + ":" + packId;

                    Path resourcePath = resolveResourcePackPath(packId);
                    if (resourcePath == null) {
                        return; // pack not found in this mod's jar/contents
                    }

                    Pack.ResourcesSupplier resourceSupplier = new Pack.ResourcesSupplier() {
                        @Override
                        public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo locationInfo) {
                            return new PathPackResources(locationInfo, resourcePath);
                        }

                        @Override
                        public net.minecraft.server.packs.PackResources openFull(PackLocationInfo locationInfo, Pack.Metadata metadata) {
                            return new PathPackResources(locationInfo, resourcePath);
                        }
                    };
                    PackLocationInfo locationInfo = new PackLocationInfo(
                            fullPackId,
                            Component.literal("Conquest JEI Compat"),
                            PackSource.BUILT_IN,
                            java.util.Optional.empty()
                    );

                    Pack pack = Pack.readMetaAndCreate(
                            locationInfo,
                            resourceSupplier,
                            PackType.CLIENT_RESOURCES,
                            new PackSelectionConfig(true, Pack.Position.TOP, false)
                    );

                    if (pack != null) {
                        packConsumer.accept(pack);
                    }
                });
            });

            //REI COMPAT
            ModList.get().getModContainerById("roughlyenoughitems").ifPresent(modContainer -> {
                event.addRepositorySource((packConsumer) -> {
                    String packId = "conquest_rei";
                    String fullPackId = "conquest" + ":" + packId;

                    Path resourcePath = resolveResourcePackPath(packId);
                    if (resourcePath == null) {
                        return; // pack not found in this mod's jar/contents
                    }

                    Pack.ResourcesSupplier resourceSupplier = new Pack.ResourcesSupplier() {
                        @Override
                        public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo locationInfo) {
                            return new PathPackResources(locationInfo, resourcePath);
                        }

                        @Override
                        public net.minecraft.server.packs.PackResources openFull(PackLocationInfo locationInfo, Pack.Metadata metadata) {
                            return new PathPackResources(locationInfo, resourcePath);
                        }
                    };
                    PackLocationInfo locationInfo = new PackLocationInfo(
                            fullPackId,
                            Component.literal("Conquest REI Compat"),
                            PackSource.BUILT_IN,
                            java.util.Optional.empty()
                    );

                    Pack pack = Pack.readMetaAndCreate(
                            locationInfo,
                            resourceSupplier,
                            PackType.CLIENT_RESOURCES,
                            new PackSelectionConfig(true, Pack.Position.TOP, false)
                    );

                    if (pack != null) {
                        packConsumer.accept(pack);
                    }
                });
            });
        }
    }
}
