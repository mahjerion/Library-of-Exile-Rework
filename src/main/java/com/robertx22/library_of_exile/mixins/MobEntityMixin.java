package com.robertx22.library_of_exile.mixins;

import com.robertx22.library_of_exile.components.EntityInfoComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobEntityMixin {

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void hook(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag, CallbackInfoReturnable<SpawnGroupData> cir) {

        try {
            LivingEntity en = (LivingEntity) (Object) this;
            EntityInfoComponent.IEntityInfo info = EntityInfoComponent.get(en);
            info.setSpawnReasonOnCreate(pReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
