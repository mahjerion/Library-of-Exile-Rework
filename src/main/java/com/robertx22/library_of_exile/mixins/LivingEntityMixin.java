package com.robertx22.library_of_exile.mixins;

import com.robertx22.library_of_exile.events.base.ExileEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "actuallyHurt", at = @At("HEAD"))
    public void hookOnActuallyHurt(DamageSource source, float amount, CallbackInfo ci) {
        try {
            LivingEntity entity = (LivingEntity) (Object) this;
            ExileEvents.DAMAGE_BEFORE_APPLIED.callEvents(new ExileEvents.OnDamageEntity(source, amount, entity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
