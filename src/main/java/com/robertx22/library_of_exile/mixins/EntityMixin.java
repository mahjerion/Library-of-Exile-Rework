package com.robertx22.library_of_exile.mixins;

import com.robertx22.library_of_exile.dimension.MapDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getLightLevelDependentMagicValue", at = @At(value = "HEAD"), cancellable = true)
    public void hookLoot(CallbackInfoReturnable<Float> cir) {

        try {
            Entity en = (Entity) (Object) this;
            if (en instanceof Spider) {
                // spiders should be hostile in our dimensions
                if (MapDimensions.isMap(en.level())) {
                    cir.setReturnValue(0F);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
