package com.conquestrefabricated.core.item.group.manager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class DelegateGroup extends CreativeModeTab {

    private final CreativeModeTab group;

    DelegateGroup(int index, CreativeModeTab group) {
        super(group.row(), group.column(), group.getType(), group.getDisplayName(), group::getIconItem,(displayContext, entries) -> {} );
        this.group = group;
    }



    @Override
    @Environment(EnvType.CLIENT)
    public Component getDisplayName() {
        return group.getDisplayName();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public ItemStack getIconItem() {
        return group.getIconItem();
    }



    @Override
    @Environment(EnvType.CLIENT)
    public Identifier getBackgroundTexture() {
        return group.getBackgroundTexture();
    }



    @Override
    @Environment(EnvType.CLIENT)
    public boolean showTitle() {
        return group.showTitle();
    }


    @Override
    @Environment(EnvType.CLIENT)
    public boolean canScroll() {
        return group.canScroll();
    }



    @Override
    @Environment(EnvType.CLIENT)
    public int column() {
        return super.column();
    }



    @Override
    @Environment(EnvType.CLIENT)
    public boolean isAlignedRight() {
        return super.isAlignedRight();
    }


/*
    @Override
    public int getTabPage() {
        return super.getTabPage();
    }

    @Override
    public boolean hasSearchBar() {
        return group.hasSearchBar();
    }

    @Override
    public int getSearchbarWidth() {
        return group.getSearchbarWidth();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Identifier getBackgroundImage() {
        return group.getBackgroundImage();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Identifier getTabsImage() {
        return group.getTabsImage();
    }

    @Override
    public int getLabelColor() {
        return group.getLabelColor();
    }

    @Override
    public int getSlotColor() {
        return group.getSlotColor();
    }*/
}
