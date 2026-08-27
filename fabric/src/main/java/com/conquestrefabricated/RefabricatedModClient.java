package com.conquestrefabricated;

import com.conquestrefabricated.api.painting.PaintingHolder;
import com.conquestrefabricated.api.painting.art.Art;
import com.conquestrefabricated.client.events.BlockPicker;
import com.conquestrefabricated.client.gui.config.ConfigSyncPacket;
import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.gui.palette.fabric.PaletteGuiEvents;
import com.conquestrefabricated.client.models.ConquestModelLoadingPlugin;
import com.conquestrefabricated.client.models.DuplicateDownUnbakedModel;
import com.conquestrefabricated.client.models.obj.ObjLoader;
import com.conquestrefabricated.client.tutorial.fabric.TutorialRenderEvent;
import com.conquestrefabricated.content.blocks.group.ModGroups;
import com.conquestrefabricated.content.blocks.init.BlockFamilyInit;
import com.conquestrefabricated.content.blocks.init.fabric.BlockClientInit;
import com.conquestrefabricated.content.blocks.init.fabric.QParticleFactory;
import com.conquestrefabricated.content.entities.init.EntityClientInit;
import com.conquestrefabricated.content.entities.painting.ModPainting;
import com.conquestrefabricated.content.entities.painting.art.ArtType;
import com.conquestrefabricated.content.entities.painting.art.ModArt;
import com.conquestrefabricated.core.asset.annotation.SpecialOffset;
import com.conquestrefabricated.core.block.properties.ModBlockProperties;
import com.conquestrefabricated.core.client.input.fabric.BindInit;
import com.conquestrefabricated.core.client.input.fabric.Bindings;
import com.conquestrefabricated.core.group.fabric.FamilyGroup;
import com.conquestrefabricated.core.init.InitClient;
import com.conquestrefabricated.core.item.group.manager.ItemGroupManager;
import com.conquestrefabricated.core.item.group.sort.ItemList;
import com.conquestrefabricated.core.item.group.sort.Sorter;
import com.conquestrefabricated.core.util.Provider;
import com.conquestrefabricated.mixin.CreativeModeTabAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.player.PlayerPickItemEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.client.screen.ScreenEventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class RefabricatedModClient implements ClientModInitializer {

    /// These store the player's original local (disk) values so we can restore them later
    private static Boolean localPlantSlowness;
    private static Boolean localPlantBreaking;
    private static Boolean localPassThroughLeaves;

    private static boolean hasShownIntro = false;

    @Override
    public void onInitializeClient() {
        System.out.println("Attempting to register resource pack: rp_crrp");
        ModContainer container = FabricLoader.getInstance()
                .getModContainer("conquest")
                .orElseThrow(() -> new RuntimeException("Cannot find mod container for " + "conquest"));
        System.out.println("Found mod container: " + container.getMetadata().getId());
        boolean success = ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.fromNamespaceAndPath("conquest", "rp_crrp"), // Resource pack ID
                container, // Mod ID
                Component.literal("CRRP"), // Display name
                ResourcePackActivationType.DEFAULT_ENABLED // Activation type
        );
//        MinecraftClient mcclient = MinecraftClient.getInstance();
//        if (!mcclient.options.resourcePacks.contains("rp_crrp")) {
//            mcclient.options.resourcePacks.add("rp_crrp");
//        }
        System.out.println("Resource pack registration success: " + success);

        if (FabricLoader.getInstance().isModLoaded("jei")) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.fromNamespaceAndPath("conquest", "conquest_jei"), // Resource pack ID
                    container, // Mod ID
                    Component.literal("Conquest of JEI Compat"), // Display name
                    ResourcePackActivationType.DEFAULT_ENABLED // Activation type
            );
        }
        if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            ResourceManagerHelper.registerBuiltinResourcePack(
                    Identifier.fromNamespaceAndPath("conquest", "conquest_rei"), // Resource pack ID
                    container, // Mod ID
                    Component.literal("Conquest of REI Compat"), // Display name
                    ResourcePackActivationType.DEFAULT_ENABLED // Activation type
            );
        }


        BlockClientInit.clientBlockEntities();
        BlockClientInit.blockColors();
        InitClient.blockColors();
        InitClient.itemColors();
        ItemGroupManager.init();

        QParticleFactory.onParticleFactoryRegistration();

        //ClientLifecycleEvents.CLIENT_STARTED.register(ItemClientInit::setup); //this was for manually adding painting item models via code
        EntityClientInit.setup();

        ModelLoadingPlugin.register(new ConquestModelLoadingPlugin());
        ModelLoadingPlugin.register(pluginContext ->
                pluginContext.modifyBlockModelOnLoad().register(
                        ModelModifier.WRAP_LAST_PHASE,
                        (model, context) -> {
                            if (model == null) return model;

                            BlockState state = context.state();
                            if (state == null) return model;

                            boolean hasOffsetToggle = state.hasProperty(ModBlockProperties.OFFSET_TOGGLE) && state.getValue(ModBlockProperties.OFFSET_TOGGLE);
                            boolean hasExtensionToggle = state.hasProperty(ModBlockProperties.EXTENSION_TOGGLE)  && state.getValue(ModBlockProperties.EXTENSION_TOGGLE);

                            if (!hasOffsetToggle && !hasExtensionToggle) {
                                return model;
                            }

                            Block block = state.getBlock();

                            SpecialOffset specialOffset =
                                    block.getClass().getAnnotation(SpecialOffset.class);

                            if (specialOffset == null) {
                                return model;
                            }

                            return switch (specialOffset.offsetType()) {
                                case DUPLICATE_DOWN ->
                                        new DuplicateDownUnbakedModel(model);

                                default ->
                                        model;
                            };
                        }
                )
        );
        BlockClientInit.common();



        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return Identifier.fromNamespaceAndPath("conquest", "block_family_loader");
                    }

                    @Override
                    public void onResourceManagerReload(ResourceManager manager) {
                        BlockFamilyInit.loadBlockFamilies(manager);
                    }
                }
        );
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ObjLoader.INSTANCE);

        //configBuildEvent = ConfigInit.setup();
        //Secrets.config(configBuildEvent);
        //PaletteSettings.config(configBuildEvent);
        //PaletteTutorial.config(configBuildEvent);
        //TutorialRenderEvent.config(configBuildEvent);
        //configBuildEvent.forEach((type, builder) -> {
        //    Config config = new Config(type, builder.build());
        //    ConfigInit.manager.addConfig(config);
        //    Log.info(ConfigInit.marker, "Registered config: {}, empty: {}", type, config.getRoot().isEmpty());
        //});
        BlockClientInit.client();
        BindInit.init();

        //InitCommon.complete();

        BlockClientInit.complete();


        /* TODO: setup the following as its own init class/methods? */
        FamilyGroup.FAMILY_GROUPS.forEach(familyGroup -> {
            CreativeModeTabEvents.modifyOutputEvent(BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(familyGroup).get()).register(entries -> {
                if (familyGroup.cached.isEmpty()) {
                    NonNullList<ItemStack> list = NonNullList.create();
                    familyGroup.populate(list);
                    familyGroup.sorter.apply(list);
                    familyGroup.sorter.sort(list);
                    familyGroup.cached = new ArrayList<>(list);
                }

                for (ItemStack item : familyGroup.cached) {
                    if (item.getItem() != Items.AIR) {
                        if (item.getItem() instanceof PaintingHolder) {
                            Art<?> art = ModArt.of(ArtType.A1x1_0);
                            ModPainting.getIds().distinct().sorted().forEach(name -> {
                                ModPainting type = ModPainting.fromName(name);
                                ItemStack stack = type.createStack(art);
                                entries.accept(stack);
                            });
                            continue;
                        }

                        if (item.getItem() instanceof TippedArrowItem) {
                            BuiltInRegistries.POTION.listElements().forEach(potionEntry -> {
                                Potion potion = potionEntry.value();
                                if (!potion.getEffects().isEmpty()) {
                                    ItemStack arrow = new ItemStack(item.getItem());
                                    arrow.set(DataComponents.POTION_CONTENTS, new PotionContents(potionEntry));
                                    entries.accept(arrow);
                                }
                            });
                            continue;
                        }

                        entries.accept(item.getItem());
                    }
                }
            });
        });

        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.BUILDING_BLOCKS);
        CreativeModeTabAccessor accessor = (CreativeModeTabAccessor) tab;
        accessor.conquest$setIcon(ModGroups.iconWithFallback("conquest:lime_mortar_masonry", "minecraft:bricks"));

        CreativeModeTab tab1 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.COLORED_BLOCKS);
        CreativeModeTabAccessor accessor1 = (CreativeModeTabAccessor) tab1;
        accessor1.conquest$setIcon(ModGroups.iconWithFallback("conquest:schist_dragon_head", "minecraft:chiseled_quartz_block"));

        CreativeModeTab tab2 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.NATURAL_BLOCKS);
        CreativeModeTabAccessor accessor2 = (CreativeModeTabAccessor) tab2;
        accessor2.conquest$setIcon(() -> Provider.block("minecraft:chiseled_stone_bricks").toStack().get());

        CreativeModeTab tab4 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.FUNCTIONAL_BLOCKS);
        CreativeModeTabAccessor accessor4 = (CreativeModeTabAccessor) tab4;
        accessor4.conquest$setIcon(ModGroups.iconWithFallback("conquest:andalusian_mosaic", "minecraft:red_glazed_terracotta"));

        CreativeModeTab tab5 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.REDSTONE_BLOCKS);
        CreativeModeTabAccessor accessor5 = (CreativeModeTabAccessor) tab5;
        accessor5.conquest$setIcon(ModGroups.iconWithFallback("conquest:etruscan_wall_design_1", "minecraft:pink_glazed_terracotta"));

        CreativeModeTab tab6 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.TOOLS_AND_UTILITIES);
        CreativeModeTabAccessor accessor6 = (CreativeModeTabAccessor) tab6;
        accessor6.conquest$setIcon(ModGroups.iconWithFallback("conquest:tudor_cross_frame", "minecraft:barrier"));

        CreativeModeTab tab7 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.COMBAT);
        CreativeModeTabAccessor accessor7 = (CreativeModeTabAccessor) tab7;
        accessor7.conquest$setIcon(ModGroups.iconWithFallback("conquest:terracotta_imbrices_and_tegulae", "minecraft:pink_terracotta"));

        CreativeModeTab tab8 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.FOOD_AND_DRINKS);
        CreativeModeTabAccessor accessor8 = (CreativeModeTabAccessor) tab8;
        accessor8.conquest$setIcon(() -> Provider.block("minecraft:oak_planks").toStack().get());

        CreativeModeTab tab9 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.INGREDIENTS);
        CreativeModeTabAccessor accessor9 = (CreativeModeTabAccessor) tab9;
        accessor9.conquest$setIcon(ModGroups.iconWithFallback("conquest:carved_oak_wood", "minecraft:oak_door"));

        CreativeModeTab tab10 = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.SPAWN_EGGS);
        CreativeModeTabAccessor accessor10 = (CreativeModeTabAccessor) tab10;
        accessor10.conquest$setIcon(() -> Provider.block("minecraft:iron_block").toStack().get());

        replaceVanillaTab(CreativeModeTabs.BUILDING_BLOCKS, "a_cobble_and_brick");
        replaceVanillaTab(CreativeModeTabs.COLORED_BLOCKS, "aa_advanced_masonry");
        replaceVanillaTab(CreativeModeTabs.NATURAL_BLOCKS, "bb_columns");
        replaceVanillaTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, "c_mosaics_tiles_and_floors");
        replaceVanillaTab(CreativeModeTabs.REDSTONE_BLOCKS, "cc_plaster_stucco_and_paint");
        replaceVanillaTab(CreativeModeTabs.TOOLS_AND_UTILITIES, "d_half_timbered_walls");
        replaceVanillaTab(CreativeModeTabs.COMBAT, "dd_roofing");
        replaceVanillaTab(CreativeModeTabs.FOOD_AND_DRINKS, "e_planks_and_beams");
        replaceVanillaTab(CreativeModeTabs.INGREDIENTS, "ee_advanced_carpentry");
        replaceVanillaTab(CreativeModeTabs.SPAWN_EGGS, "f_metal");

        ResourceKey<CreativeModeTab> groupKeyUtility = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("conquest", "rr_utility"));
        BuiltInRegistries.ITEM.stream().filter(block -> BuiltInRegistries.ITEM.getKey(block).getNamespace().equals("conquest")).forEach(block -> {
            CreativeModeTabEvents.modifyOutputEvent(groupKeyUtility).register(entries -> {
                entries.accept(block);
            });
        });
        BuiltInRegistries.ITEM.stream().filter(block -> BuiltInRegistries.ITEM.getKey(block).getNamespace().equals("conquest_armory")).forEach(block -> {
            ResourceKey<CreativeModeTab> groupKey = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath("conquest", "pp_weapons_and_tools"));
            CreativeModeTabEvents.modifyOutputEvent(groupKey).register(entries -> {
                entries.accept(block);
            });
        });

        //InitClient.init();
        ClientTickEvents.START_CLIENT_TICK.register(Bindings::tick);

        PlayerPickItemEvents.BLOCK.register((player, pos, state, includeData) -> {
            ItemStack stack = BlockPicker.onPick(player, pos, state);
            return stack;
        });

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof CreativeModeInventoryScreen) {
                ScreenKeyboardEvents.beforeKeyPress(screen).register(PaletteGuiEvents::onKeyPress);
            }
        });


        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {

            //ConfigSection section = configBuildEvent.client("tutorials", "Intro").getSection();

            new TutorialRenderEvent().render(screen, client);

//            boolean introBool = section.getOrElse("ignore_intro", false);
//            if (screen instanceof TitleScreen && !hasShownIntro && !introBool) {
//                IntroScreen introScreen = new IntroScreen(screen, section);
//                client.setScreen(introScreen);
//                hasShownIntro = true;
//            }
        });

        // === SERVER → CLIENT CONFIG SYNC ===
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                Minecraft client = context.client();

                if (client.isSingleplayer()) {
                    return;
                }

                // Snapshot the player's local values
                localPlantSlowness = ConquestConfig.INSTANCE.plantSlowness.get();
                localPlantBreaking = ConquestConfig.INSTANCE.plantBreaking.get();
                localPassThroughLeaves = ConquestConfig.INSTANCE.passThroughLeaves.get();

                // Apply server values for this multiplayer session only
                ConquestConfig.INSTANCE.plantSlowness.set(payload.plantSlowness());
                ConquestConfig.INSTANCE.plantBreaking.set(payload.plantBreaking());
                ConquestConfig.INSTANCE.passThroughLeaves.set(payload.passThroughLeaves());
            });
        });

        // === RESTORE LOCAL CONFIG WHEN LEAVING ANY WORLD/SERVER ===
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            client.execute(() -> {
                if (localPlantSlowness != null) {  // we had a server override this session
                    // Restore the player's original local values
                    ConquestConfig.INSTANCE.plantSlowness.set(localPlantSlowness);
                    ConquestConfig.INSTANCE.plantBreaking.set(localPlantBreaking);
                    ConquestConfig.INSTANCE.passThroughLeaves.set(localPassThroughLeaves);

                    // Re-save to disk so the json file is back to the player's preferences
                    // (this also undoes any accidental overwrite from clicking DONE in MP)
                    ConquestConfig.INSTANCE.save();

                    // Clear snapshot
                    localPlantSlowness = null;
                    localPlantBreaking = null;
                    localPassThroughLeaves = null;
                }
            });
        });


        ScreenEventFactory.createAfterKeyPressEvent().register(PaletteGuiEvents::onKeyPress);

        //ScreenEventFactory.createBeforeKeyPressEvent().register(PaletteGuiEvents::onKeyPress);

    }

    private static void replaceVanillaTab(ResourceKey<CreativeModeTab> tabKey, String sortFileLabel) {
        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(tabKey);
        CreativeModeTabAccessor accessor = (CreativeModeTabAccessor) tab;

        Sorter<ItemStack> sorter = loadSorter("conquest", sortFileLabel);

        accessor.conquest$setDisplayItemsGenerator((parameters, output) -> {
            NonNullList<ItemStack> list = NonNullList.create();
            sorter.apply(list);
            sorter.sort(list);
            list.forEach(stack -> output.accept(stack, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS));
        });
    }

    private static Sorter<ItemStack> loadSorter(String namespace, String label) {
        String path = String.format("/assets/%s/groups/%s.txt", namespace, label);
        try (InputStream in = RefabricatedModClient.class.getResourceAsStream(path)) {
            if (in == null) {
                return Sorter.none();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                return ItemList.read(reader, path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Sorter.none();
    }
}
