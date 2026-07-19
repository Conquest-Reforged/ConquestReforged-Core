package com.conquestrefabricated.core.net;

import net.minecraft.network.FriendlyByteBuf;

public interface MessageHandler<T> {

    T decode(FriendlyByteBuf buffer);

    void encode(T message, FriendlyByteBuf buffer);

  //  void handle(T message, Supplier<COntext> context);
}
