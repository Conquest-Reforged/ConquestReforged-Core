package com.conquestrefabricated.core.group.neoforge;

import com.conquestrefabricated.core.asset.lang.Translations;
import com.conquestrefabricated.core.item.group.ConquestGroup;
import com.conquestrefabricated.core.item.group.sort.ItemList;
import com.conquestrefabricated.core.item.group.sort.Sorter;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class ConquestItemGroup extends CreativeModeTab implements ConquestGroup {

    private static final String pathFormat = "/assets/%s/groups/%s.txt";

    private final int index;
    private final Component translationKey;
    public final Sorter<ItemStack> sorter;
    public List<ItemStack> cached = Collections.emptyList();

    public ConquestItemGroup(int index, String label, Row row, int i, Type type, Component text, Supplier<ItemStack> supplier, DisplayItemsGenerator entryCollector) {
        super(CreativeModeTab.builder()
                .icon(supplier)
                .backgroundTexture(Identifier.parse("textures/gui/container/creative_inventory/tab_items.png"))
                .title(text)
                        .displayItems((displayContext, output) -> {
                            FamilyGroup self = (FamilyGroup) FamilyGroup.FAMILY_GROUPS.stream()
                                    .filter(g -> g.label.equals(label))
                                    .findFirst()
                                    .orElseThrow();

                            if (self.cached.isEmpty()) {
                                NonNullList<ItemStack> list = NonNullList.create();
                                self.populate(list);
                                self.sorter.apply(list);
                                self.sorter.sort(list);
                                self.cached = new ArrayList<>(list);
                            }

                            for (ItemStack stack : self.cached) {
                                if (!stack.isEmpty()) {
                                    output.accept(stack);
                                }
                            }
                        })
                );
        String namespace = "conquest";
        this.index = index;
        this.translationKey = Component.translatable(Translations.getKey("itemGroup", namespace, label));
        this.sorter = getItemSorter(namespace, label);
        //Log.info("Sorter for " + label + ": " + (this.sorter == Sorter.<ItemStack>none() ? "NONE (resource not found)" : "loaded"));

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
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path.substring(1))) {
            if (in == null) {
                return Sorter.none();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                return ItemList.read(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Sorter.none();
    }



    public abstract void populate(NonNullList<ItemStack> items);
}
