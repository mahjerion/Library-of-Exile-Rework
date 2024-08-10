package com.robertx22.library_of_exile.main;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {

    @Override
    public void connect() {
        ExileLog.get().debug("Connecting " + Ref.MODID + " Mixins");
        Mixins.addConfiguration(
                "library_of_exile-mixins.json"
        );
    }
}