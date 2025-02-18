package com.robertx22.orbs_of_crafting.register.reqs;

import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.orbs_of_crafting.misc.StackHolder;
import com.robertx22.orbs_of_crafting.register.reqs.base.ItemRequirement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class IsDamagedReq extends ItemRequirement {

    public IsDamagedReq(String id) {
        super(ItemReqSers.IS_DAMAGED, id);
    }

    @Override
    public Class<?> getClassForSerialization() {
        return IsDamagedReq.class;
    }

    @Override
    public MutableComponent getDescWithParams() {
        return this.getTranslation(TranslationType.DESCRIPTION).getTranslatedName();
    }

    @Override
    public boolean isValid(Player p, StackHolder s) {
        return s.stack.isDamaged();
    }


    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(Ref.MODID)
                .desc(ExileTranslation.registry(this, "Must be a Damaged Item")
                );
    }


}