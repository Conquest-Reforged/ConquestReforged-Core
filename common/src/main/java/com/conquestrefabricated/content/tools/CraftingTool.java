package com.conquestrefabricated.content.tools;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;

import java.util.Objects;

/**
 * A set of crafting tools: an item that opens a stonecutter-style picker listing every
 * {@link ToolCraftingRecipe} tagged with this set's id.
 *
 * <p>Adding a set needs no new recipe type - declare one of these, register its item and menu type
 * from your loader's registration phase, and point blocks at it with
 * {@code Props.craftedWith(tool.id(), ingredient)}. See {@link CraftingTools} for Conquest's own.</p>
 *
 * <p>The item and menu type are built on demand rather than in the constructor: {@code Item}
 * constructors claim an intrusive holder from the item registry, which only works while that
 * registry is open.</p>
 */
public final class CraftingTool {

    private final Identifier id;
    private final ResourceKey<Item> itemKey;

    private Item item;
    private MenuType<ToolCraftingMenu> menuType;

    private CraftingTool(Identifier id, Identifier itemId) {
        this.id = id;
        this.itemKey = ResourceKey.create(Registries.ITEM, itemId);
    }

    /** A tool set whose id and item id are the same, e.g. {@code conquest:mason_tools}. */
    public static CraftingTool of(Identifier id) {
        return new CraftingTool(id, id);
    }

    /** The id recipes name in their {@code tool} field. */
    public Identifier id() {
        return this.id;
    }

    public ResourceKey<Item> itemKey() {
        return this.itemKey;
    }

    public Identifier itemId() {
        return this.itemKey.identifier();
    }

    /** Lang key for the picker's title bar. */
    public String titleKey() {
        return "container." + this.itemId().getNamespace() + "." + this.itemId().getPath();
    }

    public Component title() {
        return Component.translatable(this.titleKey());
    }

    /** Lang key for the item's tooltip line. */
    public String tooltipKey() {
        return "tooltip." + this.itemId().getNamespace() + ".item." + this.itemId().getPath();
    }

    public Item item() {
        return Objects.requireNonNull(this.item, () -> this.id + " item has not been created yet");
    }

    public MenuType<ToolCraftingMenu> menuType() {
        return Objects.requireNonNull(this.menuType, () -> this.id + " menu type has not been created yet");
    }

    /** Builds the item. Call only while the item registry is open, then register the result. */
    public Item createItem() {
        this.item = new CraftingToolItem(this, new Item.Properties().stacksTo(1).setId(this.itemKey));
        return this.item;
    }

    /** Builds the menu type. Call only while the menu registry is open, then register the result. */
    public MenuType<ToolCraftingMenu> createMenu() {
        this.menuType = new MenuType<>(
                (containerId, inventory) -> new ToolCraftingMenu(this, containerId, inventory),
                FeatureFlags.DEFAULT_FLAGS);
        return this.menuType;
    }

    @Override
    public String toString() {
        return "CraftingTool[" + this.id + "]";
    }
}
