package com.conquestrefabricated.client.gui.station;

import com.conquestrefabricated.content.loom.LoomMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** The loom's picker: the shared workstation screen, naming its work weaving. */
public class LoomScreen extends WorkstationScreen<LoomMenu> {

    public LoomScreen(LoomMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected String langPrefix() {
        return "container.conquest.loom";
    }
}
