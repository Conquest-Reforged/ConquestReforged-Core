package com.conquestrefabricated.mixin;

import com.conquestrefabricated.client.gui.palette.ClientPlayerInteractionManagerExtension;
import com.conquestrefabricated.client.gui.palette.PaletteItemPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultiPlayerGameMode.class)
public class ClientPlayerInteractionManagerMixin implements ClientPlayerInteractionManagerExtension {

    @Override
    public void clickStack(ItemStack stack, int slotId) {
        // Implementation as shown before
        MultiPlayerGameMode self = (MultiPlayerGameMode) (Object) this;

        ClientPlayNetworking.send(new PaletteItemPacket(slotId, stack, Action.TO_INVENTORY));
        //self.networkHandler.sendPacket(new PaletteItemPacket(slotId, stack, Action.TO_INVENTORY));
    }

    @Override
    public void dropStack(ItemStack stack, Action action) {
        // Implementation as shown before
        MultiPlayerGameMode self = (MultiPlayerGameMode) (Object) this;

        ClientPlayNetworking.send(new PaletteItemPacket(0, stack, Action.TO_INVENTORY));

        //self.networkHandler.sendPacket(new PaletteItemPacket(stack, action));
    }

    @Override
    public void decrementStack(ItemStack stack, int slotId) {
        // Implementation as shown before
        MultiPlayerGameMode self = (MultiPlayerGameMode) (Object) this;

        ClientPlayNetworking.send(new PaletteItemPacket(slotId, stack, Action.DECREMENT));
        //self.networkHandler.sendPacket(new PaletteItemPacket(slotId, stack, Action.TO_INVENTORY));
    }
}