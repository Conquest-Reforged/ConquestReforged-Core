package com.conquestrefabricated.core.item.group.manager;

import com.conquestrefabricated.core.item.group.ConquestGroup;
import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import java.util.*;

@Environment(EnvType.CLIENT)
public class ItemGroupManager {

    private static final ItemGroupManager instance = new ItemGroupManager();
    public static final List<CreativeModeTab> fixed = Lists.newArrayList(
            CreativeModeTabs.searchTab(),
            BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.INVENTORY),
            BuiltInRegistries.CREATIVE_MODE_TAB.getValueOrThrow(CreativeModeTabs.HOTBAR)
    );

    static {
        fixed.sort(Comparator.comparing(BuiltInRegistries.CREATIVE_MODE_TAB::getId));
    }

    private final Map<Class<?>, Set<CreativeModeTab>> conquestGroups = new HashMap<>();
    private final Map<GroupType, Set<CreativeModeTab>> groups = new EnumMap<>(GroupType.class);

    public static void init() {
        instance.storeVanillaGroups();
        instance.storeModGroups();
    }

    private ItemGroupManager() {
        for (GroupType type : GroupType.values()) {
            groups.put(type, new HashSet<>());
        }
    }

    public void register(CreativeModeTab group) {
        conquestGroups.computeIfAbsent(group.getClass(), k -> new HashSet<>()).add(group);
        groups.put(GroupType.CONQUEST, conquestGroups.get(group.getClass()));
    }

    private void storeVanillaGroups() {
        Set<CreativeModeTab> groups = this.groups.get(GroupType.VANILLA);
        for(CreativeModeTab group : CreativeModeTabs.allTabs()) {
            if(!fixed.contains(group) && !(group instanceof ConquestGroup) && !(group instanceof DelegateGroup)) {
                groups.add(group);
            }
        }
    }

    private void storeModGroups() {
        Set<CreativeModeTab> vanilla = this.groups.get(GroupType.VANILLA);
        Set<CreativeModeTab> other = this.groups.get(GroupType.OTHER);
        for (CreativeModeTab group : CreativeModeTabs.allTabs()) {
            if (vanilla.contains(group)) {
                continue;
            }
            if (group instanceof ConquestGroup || group instanceof DelegateGroup) {
                continue;
            }
            other.add(group);
        }
    }

    public static ItemGroupManager getInstance() {
        return instance;
    }

}
