package com.conquestrefabricated.core.group.fabric;

import com.conquestrefabricated.core.asset.lang.Translations;
import com.conquestrefabricated.core.item.group.ConquestGroup;
import com.conquestrefabricated.core.item.group.sort.ItemList;
import com.conquestrefabricated.core.item.group.sort.Sorter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public abstract class ConquestItemGroup extends CreativeModeTab implements ConquestGroup {

    private static final String pathFormat = "/assets/%s/groups/%s.txt";

    private final int index;
    private final Component translationKey;
    public final Sorter<ItemStack> sorter;
    public List<ItemStack> cached = Collections.emptyList();
    public String label;

    public ConquestItemGroup(int index, String label, Row row, int i, Type type, Component text, Supplier<ItemStack> supplier, DisplayItemsGenerator entryCollector) {
        super(row, i, type, text, supplier, entryCollector);
        String namespace = "conquest";
        this.index = index;
        this.label = label;
        this.translationKey = Component.translatable(Translations.getKey("itemGroup", namespace, label));
        this.sorter = getItemSorter(namespace, label);
        Translations.getInstance().add(translationKey.getString(), Translations.translate(label));
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

    private Sorter<ItemStack> getItemSorter(String namespace, String label) {
        String path = String.format(pathFormat, namespace, label);
        try (InputStream in = FamilyGroup.class.getResourceAsStream(path)) {
            if (in == null) {
                return Sorter.none();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                return ItemList.read(reader);
            }
        } catch (IOException e) {
            // errors if unable to close the resource or reading the stream fails
            e.printStackTrace();
        }
        return Sorter.none();
    }



    public abstract void populate(NonNullList<ItemStack> items);
}
