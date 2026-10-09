package com.conquestrefabricated.content.spoilage;

import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * Tells a player's client how fast food ages in the container they have opened.
 *
 * <p>A menu does not say which block it belongs to, so the last block a player right-clicked is
 * remembered; when a menu then opens, and that block is a container, the pace there is worked out - by the
 * very same {@link SpoilageEnvironment#stored} that ages the food - and sent. Closing the menu sends
 * {@link StoragePace#NONE}. The loader supplies the way to send, with {@link #useSender}.</p>
 */
public final class SpoilageStorage {

    private static final Map<UUID, BlockPos> LAST_USED = new ConcurrentHashMap<>();

    private static BiConsumer<ServerPlayer, StoragePace> sender = (player, pace) -> {
    };

    private SpoilageStorage() {
    }

    public static void useSender(BiConsumer<ServerPlayer, StoragePace> loaderSender) {
        sender = loaderSender;
    }

    public static void register() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> {
            if (player instanceof ServerPlayer) {
                LAST_USED.put(player.getUUID(), pos.immutable());
            }
            return InteractionResult.PASS;
        });
        PlayerEvent.OPEN_MENU.register(SpoilageStorage::opened);
        PlayerEvent.CLOSE_MENU.register((player, menu) -> {
            if (player instanceof ServerPlayer server) {
                sender.accept(server, StoragePace.NONE);
            }
        });
        PlayerEvent.PLAYER_QUIT.register(player -> LAST_USED.remove(player.getUUID()));
    }

    private static void opened(Player player, AbstractContainerMenu menu) {
        if (!(player instanceof ServerPlayer server) || !(server.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = LAST_USED.get(player.getUUID());
        StoragePace pace = StoragePace.NONE;
        if (pos != null && Spoilage.enabled() && pos.distSqr(player.blockPosition()) <= 100.0) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof Container) {
                BlockState state = level.getBlockState(pos);
                List<Double> rates = new ArrayList<>();
                for (SpoilClass kind : SpoilClass.values()) {
                    rates.add(SpoilageEnvironment.stored(level, pos, kind, state));
                }
                pace = new StoragePace(SpoilageEnvironment.placeOf(level, pos).ordinal(), rates);
            }
        }
        sender.accept(server, pace);
    }
}
