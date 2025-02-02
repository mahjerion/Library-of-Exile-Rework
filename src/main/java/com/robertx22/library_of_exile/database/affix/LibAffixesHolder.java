package com.robertx22.library_of_exile.database.affix;

import com.robertx22.library_of_exile.database.affix.base.AffixTranslation;
import com.robertx22.library_of_exile.database.affix.types.AffixNumberRange;
import com.robertx22.library_of_exile.database.affix.types.AttributeMobAffix;
import com.robertx22.library_of_exile.database.affix.types.ExileMobAffix;
import com.robertx22.library_of_exile.database.affix.types.PotionMobAffix;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

// add affixes where mobs inflict potion effect on you
public class LibAffixesHolder extends ExileKeyHolder<ExileMobAffix> {
    public static LibAffixesHolder INSTANCE = new LibAffixesHolder(Ref.REGISTER_INFO);

    public LibAffixesHolder(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    public ExileKey<ExileMobAffix, KeyInfo> FAST_MOBS = ExileKey.ofId(this, "fast_mobs", x -> {
        var data = PotionMobAffix.Data.ofPermanent(MobEffects.MOVEMENT_SPEED, new AffixNumberRange(1, 2));
        var translation = AffixTranslation.ofPermanentPotion(Ref.MODID);
        return new PotionMobAffix(ExileMobAffix.Affects.MOB, x.GUID(), 1000, data, translation);
    });
    public ExileKey<ExileMobAffix, KeyInfo> PERIODIC_SPEEDY_MOBS = ExileKey.ofId(this, "periodic_speedy_mobs", x -> {
        var data = PotionMobAffix.Data.of5sEvery10s(MobEffects.MOVEMENT_SPEED, new AffixNumberRange(3, 6));
        var translation = AffixTranslation.ofPotion(Ref.MODID);
        return new PotionMobAffix(ExileMobAffix.Affects.MOB, x.GUID(), 1000, data, translation);
    });
    public ExileKey<ExileMobAffix, KeyInfo> PERIODIC_INVISIBILITY = ExileKey.ofId(this, "periodic_invis", x -> {
        var data = PotionMobAffix.Data.of5sEvery10s(MobEffects.INVISIBILITY, new AffixNumberRange(1, 1));
        var translation = AffixTranslation.ofPotion(Ref.MODID);
        return new PotionMobAffix(ExileMobAffix.Affects.MOB, x.GUID(), 1000, data, translation);
    });
    public ExileKey<ExileMobAffix, KeyInfo> PERIODIC_LEVITATION = ExileKey.ofId(this, "periodic_levitate", x -> {
        var data = PotionMobAffix.Data.of1sEvery10s(MobEffects.LEVITATION, new AffixNumberRange(1, 1));
        var translation = AffixTranslation.ofPotion(Ref.MODID);
        return new PotionMobAffix(ExileMobAffix.Affects.MOB, x.GUID(), 1000, data, translation);
    });
    public ExileKey<ExileMobAffix, KeyInfo> KNOCKBACK_IMMUNE = ExileKey.ofId(this, "knockback_immune", x -> {
        var data = AttributeMobAffix.Data.of(Attributes.KNOCKBACK_RESISTANCE, UUID.fromString("9bedbca3-44e3-4d99-acf3-7a8407b39339"), AttributeModifier.Operation.ADDITION, new AffixNumberRange(1, 1));
        var translation = AffixTranslation.ofAttribute(Ref.MODID);
        return new AttributeMobAffix(ExileMobAffix.Affects.MOB, x.GUID(), 1000, data, translation);
    });
    public ExileKey<ExileMobAffix, KeyInfo> HIGH_HEALTH = ExileKey.ofId(this, "high_health", x -> {
        var data = AttributeMobAffix.Data.of(Attributes.MAX_HEALTH, UUID.fromString("8bedbca3-44e3-4d99-acf3-7a8407b39339"), AttributeModifier.Operation.MULTIPLY_TOTAL, new AffixNumberRange(0.25F, 0.5F));
        var translation = AffixTranslation.ofAttribute(Ref.MODID);
        return new AttributeMobAffix(ExileMobAffix.Affects.MOB, x.GUID(), 1000, data, translation);
    });
    public ExileKey<ExileMobAffix, KeyInfo> HIGH_KNOCKBACK = ExileKey.ofId(this, "high_knockback", x -> {
        var data = AttributeMobAffix.Data.of(Attributes.ATTACK_KNOCKBACK, UUID.fromString("7bedbca3-44e3-4d99-acf3-7a8407b39339"), AttributeModifier.Operation.ADDITION, new AffixNumberRange(1, 3));
        var translation = AffixTranslation.ofAttribute(Ref.MODID);
        return new AttributeMobAffix(ExileMobAffix.Affects.MOB, x.GUID(), 1000, data, translation);
    });

    @Override
    public void loadClass() {

    }
}
