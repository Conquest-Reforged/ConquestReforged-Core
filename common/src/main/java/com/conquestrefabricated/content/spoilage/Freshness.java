package com.conquestrefabricated.content.spoilage;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * How far a stack has gone off, in {@link Spoilage#STAGES} steps. A stack without this component is fresh,
 * so a perishable item that has never been looked at stacks with every other fresh one.
 *
 * <p>The component takes part in stacking like any other: stacks at different stages do not merge by
 * themselves. That is deliberate - it is what stops old food being hidden among fresh - and the only way to
 * combine them is the {@code conquest:merge_perishables} crafting recipe, which averages.</p>
 */
public record Freshness(int stage) {

    public static final Codec<Freshness> CODEC = Codec.intRange(1, Spoilage.STAGES - 1)
            .xmap(Freshness::new, Freshness::stage);

    public static final StreamCodec<RegistryFriendlyByteBuf, Freshness> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(Freshness::new, Freshness::stage).cast();
}
