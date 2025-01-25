package com.robertx22.library_of_exile.database.affix.types;

import com.robertx22.library_of_exile.database.affix.apply_strat.ApplyStrategy;
import com.robertx22.library_of_exile.database.affix.apply_strat.PotionApplyStrategy;
import com.robertx22.library_of_exile.database.affix.base.AffixTranslation;
import com.robertx22.library_of_exile.localization.ExileTranslation;
import com.robertx22.library_of_exile.localization.TranslationBuilder;
import com.robertx22.library_of_exile.localization.TranslationType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class PotionMobAffix extends ExileMobAffix {


    public static record Data(String status_effect_id,
                              AffixNumberRange amplifer,
                              AffixNumberRange duration_ticks,
                              int apply_every_x_seconds) {

        public static Data of(MobEffect effect, AffixNumberRange amp, AffixNumberRange durationTicks, int apply_every_x_seconds) {
            return new Data(BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), amp, durationTicks, apply_every_x_seconds);
        }

        public static Data of5sEvery10s(MobEffect effect, AffixNumberRange amp) {
            return new Data(BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), amp, new AffixNumberRange(20 * 5, 20 * 5), 10);
        }

        public static Data of3sEvery10s(MobEffect effect, AffixNumberRange amp) {
            return new Data(BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), amp, new AffixNumberRange(20 * 3, 20 * 3), 10);
        }

        public static Data of1sEvery10s(MobEffect effect, AffixNumberRange amp) {
            return new Data(BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), amp, new AffixNumberRange(20 * 1, 20 * 1), 10);
        }

        public static Data ofPermanent(MobEffect effect, AffixNumberRange amp) {
            return new Data(BuiltInRegistries.MOB_EFFECT.getKey(effect).toString(), amp, new AffixNumberRange(20 * 10, 20 * 10), 10);
        }


        public MobEffect getEffect() {
            return ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(status_effect_id));
        }
    }


    public Data data;


    public PotionMobAffix(Affects affects, String id, int weight, Data data, AffixTranslation t) {
        super("potion_mob_affix", affects, id, weight, t);
        this.data = data;
    }


    public void tryApply(int perc, LivingEntity en) {

        if (en.tickCount % (20 * data.apply_every_x_seconds) == 0) {
            var instance = new MobEffectInstance(data.getEffect(), data.duration_ticks.getInt(perc), data.amplifer.getInt(perc));
            en.addEffect(instance);
        }
    }


    @Override
    public Class<PotionMobAffix> getClassForSerialization() {
        return PotionMobAffix.class;
    }


    @Override
    public ApplyStrategy getApplyStrategy() {
        return PotionApplyStrategy.INSTANCE;
    }

    // we don't need to require them to localize EVERY single potion affix separately
    @Override
    public TranslationBuilder createTranslationBuilder() {
        return TranslationBuilder.of(modid).name(ExileTranslation.of(modid + "." + "potion_mob_affix", locName));
    }

    @Override
    public MutableComponent getParamName(int perc) {
        int sec = (int) (data.duration_ticks.getInt(perc) / 20);
        return getTranslation(TranslationType.NAME).getTranslatedName(data.getEffect().getDisplayName(), data.amplifer.getInt(perc), sec, data.apply_every_x_seconds)
                .withStyle(ChatFormatting.GREEN);
    }


}
