package com.robertx22.library_of_exile.mixins;

import com.robertx22.library_of_exile.config.map_dimension.MapWorldBorder;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {


    @Inject(method = "getWorldBorder", at = @At("RETURN"), cancellable = true)
    private void hook2(CallbackInfoReturnable<WorldBorder> cir) {
        try {
            Level world = (Level) (Object) this;
            if (world != null && !world.isClientSide) {
                if (MapDimensions.isMap(world)) {
                    var c = MapDimensions.getInfo(world);

                    if (c == null) {
                        return;
                    }
                    if (c.config.DISABLE_WORLDBORDER_OVERRIDE.get()) {
                        return;
                    }
                    var settings = world.getServer().getWorldData().overworldData().getWorldBorder();
                    if (settings != null) {
                        cir.setReturnValue(new MapWorldBorder(settings));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
