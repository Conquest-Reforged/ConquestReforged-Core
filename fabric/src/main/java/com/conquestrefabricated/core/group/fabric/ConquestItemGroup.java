package com.conquestrefabricated.core.group.fabric;

import com.conquestrefabricated.core.asset.lang.Translations;
import com.conquestrefabricated.core.item.group.ConquestGroup;
import com.conquestrefabricated.core.Namespaces;
import com.conquestrefabricated.core.item.group.sort.GroupFiles;
import com.conquestrefabricated.core.item.group.sort.Sorter;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public abstract class ConquestItemGroup extends CreativeModeTab implements ConquestGroup {

    private final int index;
    private final String namespace;
    private final Component translationKey;
    public final Sorter<ItemStack> sorter;
    public List<ItemStack> cached = Collections.emptyList();
    public String label;

    public ConquestItemGroup(int index, String label, Row row, int i, Type type, Component text, Supplier<ItemStack> supplier, DisplayItemsGenerator entryCollector) {
        this(Namespaces.DEFAULT, index, label, row, i, type, text, supplier, entryCollector);
    }

    public ConquestItemGroup(String namespace, int index, String label, Row row, int i, Type type, Component text, Supplier<ItemStack> supplier, DisplayItemsGenerator entryCollector) {
        super(row, i, type, text, supplier, entryCollector);
        this.index = index;
        this.namespace = namespace;
        this.label = label;
        this.translationKey = Component.translatable(Translations.getKey("itemGroup", namespace, label));
        this.sorter = GroupFiles.loadSorter(label);
        Translations.getInstance().add(translationKey.getString(), Translations.translate(label));
    }

    /** The namespace this tab is registered under. */
    public String getNamespace() {
        return namespace;
    }

    /** Unique across namespaces, unlike {@link #label} on its own. */
    public String getKey() {
        return namespace + ':' + label;
    }

    @Override
    public Component getDisplayName() {
        return translationKey;
    }

    public void invalidate() {
        cached = Collections.emptyList();
    }

    public int getOrderIndex() {
        return index;
    }




    public abstract void populate(NonNullList<ItemStack> items);
}
