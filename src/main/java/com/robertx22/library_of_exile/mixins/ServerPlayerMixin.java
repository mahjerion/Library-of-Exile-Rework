package com.robertx22.library_of_exile.mixins;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    // server player overrides ondeath method, so we need thid specific mixin
    // keep the try catch cus any exception removes all player death items!!!
    /*
    @Inject(method = "die", at = @At("HEAD"))
    private void myhookOnDeath(DamageSource source, CallbackInfo ci) {
        try {
            ServerPlayer victim = (ServerPlayer) (Object) this;

            ExileEvents.PLAYER_DEATH.callEvents(new ExileEvents.OnPlayerDeath(victim));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     */
}
