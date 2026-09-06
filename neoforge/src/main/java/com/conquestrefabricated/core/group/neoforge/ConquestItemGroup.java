package com.conquestrefabricated.core.group.neoforge;

import com.conquestrefabricated.core.asset.lang.Translations;
import com.conquestrefabricated.core.item.group.ConquestGroup;
import com.conquestrefabricated.core.Namespaces;
import com.conquestrefabricated.core.item.group.sort.GroupFiles;
import com.conquestrefabricated.core.item.group.sort.Sorter;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class ConquestItemGroup extends CreativeModeTab implements ConquestGroup {

    private final int index;
    private final String namespace;
    private final Component translationKey;
    public final Sorter<ItemStack> sorter;
    public List<ItemStack> cached = Collections.emptyList();

    public ConquestItemGroup(int index, String label, Row row, int i, Type type, Component text, Supplier<ItemStack> supplier, DisplayItemsGenerator entryCollector) {
        this(Namespaces.DEFAULT, index, label, row, i, type, text, supplier, entryCollector);
    }

    public ConquestItemGroup(String namespace, int index, String label, Row row, int i, Type type, Component text, Supplier<ItemStack> supplier, DisplayItemsGenerator entryCollector) {
        super(CreativeModeTab.builder()
                .icon(supplier)
                .backgroundTexture(Identifier.parse("textures/gui/container/creative_inventory/tab_items.png"))
                .title(text)
                        .displayItems((displayContext, output) -> {
                            // keyed on namespace + label: an addon may reuse one of our labels
                            FamilyGroup self = FamilyGroup.FAMILY_GROUPS.stream()
                                    .filter(g -> g.getKey().equals(namespace + ':' + label))
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
        this.index = index;
        this.namespace = namespace;
        this.translationKey = Component.translatable(Translations.getKey("itemGroup", namespace, label));
        this.sorter = GroupFiles.loadSorter(label);
        //Log.info("Sorter for " + label + ": " + (this.sorter == Sorter.<ItemStack>none() ? "NONE (resource not found)" : "loaded"));

        Translations.getInstance().add(translationKey.getString(), Translations.translate(label));
    }

    /** The namespace this tab is registered under. */
    public String getNamespace() {
        return namespace;
    }

    /** Unique across namespaces, unlike the label on its own. */
    public String getKey() {
        return namespace + ':' + getLabel();
    }

    protected abstract String getLabel();

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
