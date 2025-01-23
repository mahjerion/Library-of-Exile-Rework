package com.robertx22.library_of_exile.database.affix.base;

import com.robertx22.library_of_exile.events.base.ExileEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class GrabMobAffixesEvent extends ExileEvent {

    public LivingEntity en;

    public GrabMobAffixesEvent(LivingEntity en) {
        this.en = en;
    }

    public List<ExileAffixData> allAffixes = new ArrayList<>();

    public void add(ExileAffixData affix) {
        allAffixes.add(affix);
    }
}
