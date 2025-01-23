package com.robertx22.library_of_exile.database.affix.apply_strat;

import com.robertx22.library_of_exile.database.affix.base.ExileAffixData;
import com.robertx22.library_of_exile.database.affix.types.AttributeMobAffix;
import net.minecraft.world.entity.LivingEntity;

public class AttributeApplyStrategy extends ApplyStrategy<AttributeMobAffix> {

    public static AttributeApplyStrategy INSTANCE = new AttributeApplyStrategy();

    @Override
    public void applyManually(ExileAffixData data, LivingEntity en) {
        AttributeMobAffix affix = (AttributeMobAffix) data.getAffix();
        affix.apply(data.perc, en);
    }

    @Override
    public void applyOnMobSpawn(ExileAffixData data, LivingEntity en) {
        applyManually(data, en);
    }

    @Override
    public void remove(ExileAffixData data, LivingEntity en) {
        AttributeMobAffix affix = (AttributeMobAffix) data.getAffix();
        affix.remove(en);
    }

    @Override
    public void onEverySecond(ExileAffixData data, LivingEntity en) {

    }
}
