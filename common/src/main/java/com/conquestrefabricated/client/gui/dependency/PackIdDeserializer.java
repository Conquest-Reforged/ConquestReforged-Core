package com.conquestrefabricated.client.gui.dependency;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;

public class PackIdDeserializer {

    public static final Codec<String> PACK_ID_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("pack_id", "").forGetter(id -> id)
    ).apply(instance, id -> id));

    public static final MetadataSectionType<String> INSTANCE = new MetadataSectionType<>("pack", PACK_ID_CODEC);
}