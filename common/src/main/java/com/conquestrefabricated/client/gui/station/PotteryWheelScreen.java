package com.conquestrefabricated.client.gui.station;

import com.conquestrefabricated.content.pottery.PotteryWheelMenu;
import com.conquestrefabricated.content.pottery.PotteryWheelStation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** The pottery wheel's picker: the shared workstation screen, naming its work shaping. */
public class PotteryWheelScreen extends WorkstationScreen<PotteryWheelMenu> {

    public PotteryWheelScreen(PotteryWheelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected String langPrefix() {
        return PotteryWheelStation.LANG_PREFIX;
    }
}
