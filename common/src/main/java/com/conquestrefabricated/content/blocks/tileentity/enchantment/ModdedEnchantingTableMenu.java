package com.conquestrefabricated.content.blocks.tileentity.enchantment;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;

public class ModdedEnchantingTableMenu extends EnchantmentMenu {
    private ContainerLevelAccess access;

    public ModdedEnchantingTableMenu(int p_39454_, Inventory p_39455_) {
        super(p_39454_, p_39455_);
    }

    public ModdedEnchantingTableMenu(int p_39457_, Inventory p_39458_, ContainerLevelAccess p_39459_) {
        super(p_39457_, p_39458_, p_39459_);
        this.access = p_39459_;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, BuiltInRegistries.BLOCK
                .get(Identifier.parse("conquest:enchantment_table")).get().value()) ||
                stillValid(this.access, player, BuiltInRegistries.BLOCK.get(Identifier.parse("conquest:floating_book")).get().value());
    }
}
