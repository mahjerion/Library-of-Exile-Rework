package com.robertx22.library_of_exile.database.affix.base;

import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.library_of_exile.main.ApiForgeEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public class MobAffixEvents {

    public static void init() {

        // mob on spawn affix should be enough like this
        // player affixes need more work, i'll leave that for later
        ApiForgeEvents.registerForgeEvent(LivingEvent.LivingTickEvent.class, event ->
        {
            try {
                LivingEntity en = event.getEntity();
                if (en.level().isClientSide) {
                    return;
                }


                if (en.tickCount == 3) {
                    var e = new GrabMobAffixesEvent(en);
                    ExileEvents.GRAB_MOB_AFFIXES.callEvents(e);

                    for (ExileAffixData data : e.allAffixes) {
                        data.getAffix().getApplyStrategy().applyOnMobSpawn(data, en);
                    }
                } else {
                    if (en.tickCount % 20 == 0) {
                        var e = new GrabMobAffixesEvent(en);
                        ExileEvents.GRAB_MOB_AFFIXES.callEvents(e);

                        // test
                        // var d = new ExileAffixData(LibAffixesHolder.INSTANCE.KNOCKBACK_IMMUNE.GUID(), 100);
                        //e.allAffixes.add(d);
                        //d.getAffix().getApplyStrategy().applyManually(d, en);

                        for (ExileAffixData data : e.allAffixes) {
                            data.getAffix().getApplyStrategy().onEverySecond(data, en);
                        }
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }
}
