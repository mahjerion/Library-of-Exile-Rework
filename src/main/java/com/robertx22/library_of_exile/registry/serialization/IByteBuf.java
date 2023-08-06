package com.robertx22.library_of_exile.registry.serialization;

import net.minecraft.network.FriendlyByteBuf;

public interface IByteBuf<T> {

    T getFromBuf(FriendlyByteBuf buf);

    void toBuf(FriendlyByteBuf buf);
}
