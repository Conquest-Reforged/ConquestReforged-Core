package com.conquestrefabricated;

import com.conquestrefabricated.client.gui.config.ConfigSyncPacket;
import com.conquestrefabricated.client.gui.config.ConquestConfig;
import com.conquestrefabricated.client.gui.palette.ClientPlayerInteractionManagerExtension;
import com.conquestrefabricated.client.gui.palette.PaletteItemPacket;
import com.conquestrefabricated.content.blocks.block.BoardsHorizontal;
import com.conquestrefabricated.content.blocks.block.decor.Chairs;
import com.conquestrefabricated.content.blocks.block.trees.*;
import com.conquestrefabricated.content.blocks.init.BlockRegistrar;
import com.conquestrefabricated.content.blocks.init.fabric.TileRegistrar;
import com.conquestrefabricated.content.blocks.init.fabric.ManualBlockRegistrar;
import com.conquestrefabricated.content.blocks.init.fabric.ParticleRegistrarEvent;
import com.conquestrefabricated.content.effects.fabric.EffectsInit;
import com.conquestrefabricated.content.entities.init.EntityCommonInit;
import com.conquestrefabricated.content.items.init.fabric.ItemCommonInit;
import com.conquestrefabricated.core.init.InitCommon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import static com.conquestrefabricated.api.tags.ModTags.CYCLING_TOOLS;

public class RefabricatedMod implements ModInitializer {

    public static final CustomPacketPayload.Type<ConfigSyncPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("conquest", "config_sync"));

    @Override
    public void onInitialize() {
        BlockRegistrar.blocks();
        ManualBlockRegistrar.manualBlocks();
        BlockRegistrar.items();
        ManualBlockRegistrar.manualItems();
       // InitCommon.items();
        ItemCommonInit.init();
        TileRegistrar.entities();
        EntityCommonInit.entities();
        ParticleRegistrarEvent.onIParticleTypeRegistration();
        EffectsInit.init();
        /*
        BlockRegistrar.blocks();
        InitCommon.blocks();
        BlockRegistrar.items();
        InitCommon.items();*/
        InitCommon.common();
        ModGamerulesInit.initGamerules();
        PayloadTypeRegistry.serverboundPlay().register(PaletteItemPacket.ID, PaletteItemPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ConfigSyncPacket.ID, ConfigSyncPacket.CODEC);

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockState state = world.getBlockState(hitResult.getBlockPos());
            if ((state.getBlock() instanceof BoardsHorizontal ||
                    state.getBlock() instanceof Chairs.Toggle2 ||
                    state.getBlock() instanceof Chairs.Toggle3 ||
                    state.getBlock() instanceof Chairs.Toggle4 ||
                    state.getBlock() instanceof Chairs.Toggle5 ||
                    state.getBlock() instanceof Chairs.Toggle6 ||state.getBlock() instanceof Underbranches ||
                    state.getBlock() instanceof UnderbranchesToggle2 ||
                    state.getBlock() instanceof UnderbranchesToggle3 ||
                    state.getBlock() instanceof UnderbranchesToggle5 ||
                    state.getBlock() instanceof UnderbranchesThinToggle2 ||
                    state.getBlock() instanceof UnderbranchesThinToggle3 ||
                    state.getBlock() instanceof UnderbranchesThinToggle5)
                    && player.isShiftKeyDown() && (player.getMainHandItem().is(CYCLING_TOOLS) || player.getMainHandItem().isEmpty())) {
                state.getBlock().useWithoutItem(state, world, hitResult.getBlockPos(), player, hitResult);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

        ServerPlayNetworking.registerGlobalReceiver(PaletteItemPacket.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                int slot = payload.slot();
                ItemStack stack = payload.stack();
                ClientPlayerInteractionManagerExtension.Action actionType = payload.action();

                if (actionType == ClientPlayerInteractionManagerExtension.Action.DECREMENT) {
                    player.sendSystemMessage(Component.literal("received the decrement packet"));
                    player.getInventory().setItem(slot, stack);
                    player.sendSystemMessage(Component.literal(String.valueOf(slot)));
                }

                if (actionType == ClientPlayerInteractionManagerExtension.Action.TO_INVENTORY) {
                    player.sendSystemMessage(Component.literal("received the inventory packet"));
                    player.getInventory().setItem(slot, stack);
                    player.sendSystemMessage(Component.literal(String.valueOf(slot)));
                }

                if (actionType == ClientPlayerInteractionManagerExtension.Action.DROP) {
                    ItemEntity itemEntity = new ItemEntity(
                            player.level(), player.getX(), player.getY(), player.getZ(), stack);
                    itemEntity.setDeltaMovement(
                            player.level().getRandom().triangle(0.0, 0.11485000171139836),
                            player.level().getRandom().triangle(0.2, 0.11485000171139836),
                            player.level().getRandom().triangle(0.0, 0.11485000171139836));
                    player.level().addFreshEntity(itemEntity);
                }
            });
        });

        ///Configs
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            ServerPlayNetworking.send(player, new ConfigSyncPacket(
                    ConquestConfig.INSTANCE.plantSlowness.get(),
                    ConquestConfig.INSTANCE.plantBreaking.get(),
                    ConquestConfig.INSTANCE.passThroughLeaves.get()
            ));
        });

    }

}
