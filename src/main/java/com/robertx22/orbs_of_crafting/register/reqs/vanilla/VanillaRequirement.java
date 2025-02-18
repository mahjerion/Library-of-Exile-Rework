package com.robertx22.orbs_of_crafting.register.reqs.vanilla;

import com.google.gson.JsonObject;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.orbs_of_crafting.main.OrbsRef;
import com.robertx22.orbs_of_crafting.misc.StackHolder;
import com.robertx22.orbs_of_crafting.register.reqs.ItemReqSers;
import com.robertx22.orbs_of_crafting.register.reqs.base.ItemRequirement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

// tool param is the stack being modified
public class VanillaRequirement extends ItemRequirement {
    transient String desc;

    public JsonObject vanillaCondition = null;

    public VanillaRequirement(String id, String desc, LootItemCondition function) {
        super(ItemReqSers.VANILLA_CONDITION, id);
        this.desc = desc;
        this.vanillaCondition = (JsonObject) LootDataType.PREDICATE.parser().toJsonTree(function);
    }


    @Override
    public Class<?> getClassForSerialization() {
        return VanillaRequirement.class;
    }

    @Override
    public MutableComponent getDescWithParams() {
        return getTranslation(TranslationType.DESCRIPTION).getTranslatedName();
    }

    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(Ref.MODID)
                .desc(ExileTranslation.registry(this, desc)
                );
    }

    
    @Override
    public boolean isValid(Player p, StackHolder obj) {
        var opt = LootDataType.PREDICATE.deserialize(new ResourceLocation(""), vanillaCondition, p.getServer().getResourceManager());

        if (opt.isPresent()) {
            LootParams lootparams = (new LootParams.Builder((ServerLevel) p.level()))
                    .withParameter(LootContextParams.THIS_ENTITY, p)
                    .withParameter(LootContextParams.ORIGIN, p.position())
                    .withParameter(LootContextParams.TOOL, obj.stack)
                    .create(LootContextParamSet.builder().build());

            var ctx = new LootContext.Builder(lootparams).create(OrbsRef.id("vanilla_condition"));
            var condi = opt.get();
            return condi.test(ctx);
        }

        return false;
    }

}
