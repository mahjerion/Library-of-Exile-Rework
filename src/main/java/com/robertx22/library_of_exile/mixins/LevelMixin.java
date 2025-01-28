package com.robertx22.library_of_exile.mixins;

import com.robertx22.library_of_exile.config.map_dimension.MapDimensionConfig;
import com.robertx22.library_of_exile.config.map_dimension.MapGameRules;
import com.robertx22.library_of_exile.config.map_dimension.MapWorldBorder;
import com.robertx22.library_of_exile.dimension.MapDimensions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// this might not work if other mods modify gamerules like this, but ideally they'd only do it to their own dimensions..right?
@Mixin(Level.class)
public class LevelMixin {


    @Inject(method = "getGameRules", at = @At("RETURN"), cancellable = true)
    private void hook(CallbackInfoReturnable<GameRules> cir) {
        Level world = (Level) (Object) this;
        try {

            if (MapDimensions.isMap(world)) {
                if (!world.isClientSide) {
                    if (MapDimensionConfig.get(world.dimensionTypeId().location()).DISABLE_GAMERULE_OVERRIDE.get()) {
                        return;
                    }
                }

                cir.setReturnValue(new MapGameRules(cir.getReturnValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "getWorldBorder", at = @At("RETURN"), cancellable = true)
    private void hook2(CallbackInfoReturnable<WorldBorder> cir) {
        Level world = (Level) (Object) this;
        try {
            if (MapDimensions.isMap(world)) {
                if (!world.isClientSide) {
                    if (MapDimensionConfig.get(world.dimensionTypeId().location()).DISABLE_WORLDBORDER_OVERRIDE.get()) {
                        return;
                    }
                }
                cir.setReturnValue(new MapWorldBorder(cir.getReturnValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
