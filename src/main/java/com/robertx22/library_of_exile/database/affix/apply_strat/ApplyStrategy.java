package com.robertx22.library_of_exile.database.affix.apply_strat;

import com.robertx22.library_of_exile.database.affix.base.ExileAffixData;
import com.robertx22.library_of_exile.database.affix.types.ExileMobAffix;
import net.minecraft.world.entity.LivingEntity;

public abstract class ApplyStrategy<T extends ExileMobAffix> {

    public abstract void applyManually(ExileAffixData data, LivingEntity en);

    public abstract void applyOnMobSpawn(ExileAffixData data, LivingEntity en);

    public abstract void remove(ExileAffixData data, LivingEntity en);

    public abstract void onEverySecond(ExileAffixData data, LivingEntity en);


}
