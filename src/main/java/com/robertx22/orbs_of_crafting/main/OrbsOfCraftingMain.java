package com.robertx22.orbs_of_crafting.main;

import com.robertx22.library_of_exile.events.base.EventConsumer;
import com.robertx22.library_of_exile.events.base.ExileEvents;
import com.robertx22.orbs_of_crafting.register.ExileCurrency;
import com.robertx22.orbs_of_crafting.register.orb_edit.OrbEdit;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;
import java.util.Map;

public class OrbsOfCraftingMain {


    public static void init(IEventBus bus) {
    
        OrbCommands.init();

        ExileEvents.AFTER_DATABASE_LOADED.register(new EventConsumer<ExileEvents.AfterDatabaseLoaded>() {
            @Override
            public void accept(ExileEvents.AfterDatabaseLoaded event) {
                for (Map.Entry<ExileCurrency, List<OrbEdit>> en : OrbEdit.ORB_AND_EDIT_CACHED_MAP.get().entrySet()) {
                    for (OrbEdit edit : en.getValue()) {
                        en.getKey().applyEdits(edit);
                    }
                }
            }
        });

    }


}
