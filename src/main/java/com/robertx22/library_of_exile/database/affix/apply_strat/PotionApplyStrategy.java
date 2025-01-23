package com.robertx22.library_of_exile.database.affix.apply_strat;

import com.robertx22.library_of_exile.database.affix.base.ExileAffixData;
import com.robertx22.library_of_exile.database.affix.types.PotionMobAffix;
import net.minecraft.world.entity.LivingEntity;

public class PotionApplyStrategy extends ApplyStrategy<PotionMobAffix> {
    public static PotionApplyStrategy INSTANCE = new PotionApplyStrategy();

    @Override
    public void applyManually(ExileAffixData data, LivingEntity en) {

    }

    @Override
    public void applyOnMobSpawn(ExileAffixData data, LivingEntity en) {

    }

    @Override
    public void remove(ExileAffixData data, LivingEntity en) {

    }

    @Override
    public void onEverySecond(ExileAffixData data, LivingEntity en) {
        PotionMobAffix affix = (PotionMobAffix) data.getAffix();
        affix.tryApply(data.perc, en);
    }
}
