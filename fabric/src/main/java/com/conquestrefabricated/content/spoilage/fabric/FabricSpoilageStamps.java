package com.conquestrefabricated.content.spoilage.fabric;

import com.conquestrefabricated.content.spoilage.SpoilageStamps;
import com.conquestrefabricated.core.Namespaces;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

/** Keeps the aging stamps as saved Fabric data attachments on block entities and players. */
final class FabricSpoilageStamps implements SpoilageStamps.Storage {

    private static final AttachmentType<Long> AGED_AT = AttachmentRegistry.createPersistent(
            Identifier.fromNamespaceAndPath(Namespaces.DEFAULT, "aged_at"), Codec.LONG);

    @Override
    public long get(Object holder) {
        Long stamp = ((AttachmentTarget) holder).getAttached(AGED_AT);
        return stamp == null ? -1L : stamp;
    }

    @Override
    public void set(Object holder, long gameTime) {
        ((AttachmentTarget) holder).setAttached(AGED_AT, gameTime);
    }

    @Override
    public void clear(Object holder) {
        AttachmentTarget target = (AttachmentTarget) holder;
        if (target.hasAttached(AGED_AT)) {
            target.removeAttached(AGED_AT);
        }
    }
}
