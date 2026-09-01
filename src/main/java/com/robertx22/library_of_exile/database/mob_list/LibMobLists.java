package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class LibMobLists extends ExileKeyHolder<MobList> {

    public static LibMobLists INSTANCE = new LibMobLists(Ref.REGISTER_INFO);


    public LibMobLists(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    /**
     * The four elemental caster monsters, at a quarter of the list between them.
     * <p>
     * They exist because a map's incoming damage was almost entirely physical: an ordinary monster
     * hits with its weapon, and the only route to fire, cold, lightning or chaos was a monster
     * rolling an elemental affix - a chance on top of a rarity chance. So a player could build
     * elemental resistances and never find out whether they worked. These put elemental damage in
     * the pool directly rather than raising those odds.
     * <p>
     * The weight is derived from whatever the list already totals rather than written down, so it
     * stays a quarter when someone adds a mob - a fixed number would quietly drift as lists grow.
     * The arithmetic: for a share {@code p} of the whole, {@code n} newcomers each want
     * {@code T*p / (n*(1-p))}, and at a quarter between four of them that is {@code T/12}.
     * <p>
     * By id, because these are mmorpg's mobs and this library cannot reference them - see
     * {@link MobEntry#MobEntry(int, String)}. A pack without mmorpg installed simply doesn't roll
     * them.
     */
    private static void addWizards(List<MobEntry> all) {
        int total = all.stream().mapToInt(x -> x.weight).sum();
        int each = Math.max(1, total / 12);

        all.add(new MobEntry(each, "mmorpg:fire_wizard"));
        all.add(new MobEntry(each, "mmorpg:ice_wizard"));
        all.add(new MobEntry(each, "mmorpg:lightning_wizard"));
        all.add(new MobEntry(each, "mmorpg:chaos_wizard"));
    }

    public ExileKey<MobList, KeyInfo> GENERIC_UNDEAD = ExileKey.ofId(this, "generic_undead", x -> {
        List<MobEntry> all = new ArrayList<>();
        all.add(new MobEntry(1000, EntityType.ZOMBIE));
        all.add(new MobEntry(300, EntityType.SKELETON));
        all.add(new MobEntry(500, EntityType.HUSK));
        addWizards(all);
        return new MobList(x.GUID(), 1000, all);
    });
    public ExileKey<MobList, KeyInfo> SPIDER_FOREST = ExileKey.ofId(this, "spider_forest", x -> {
        List<MobEntry> all = new ArrayList<>();
        all.add(new MobEntry(1000, EntityType.SPIDER));
        all.add(new MobEntry(300, EntityType.CAVE_SPIDER));
        all.add(new MobEntry(100, EntityType.WITCH));
        all.add(new MobEntry(50, EntityType.STRAY));
        addWizards(all);
        return new MobList(x.GUID(), 1000, all);
    });
    public ExileKey<MobList, KeyInfo> NETHER = ExileKey.ofId(this, "nether", x -> {
        List<MobEntry> all = new ArrayList<>();
        all.add(new MobEntry(1000, EntityType.WITHER_SKELETON));
        all.add(new MobEntry(300, EntityType.BLAZE));
        all.add(new MobEntry(500, EntityType.ZOMBIFIED_PIGLIN));
        addWizards(all);
        return new MobList(x.GUID(), 1000, all, MobListTags.HAS_FLYING_MOBS);
    });
    public ExileKey<MobList, KeyInfo> EVIL_VILLAGER = ExileKey.ofId(this, "evil_villager", x -> {
        List<MobEntry> all = new ArrayList<>();
        all.add(new MobEntry(700, EntityType.ZOMBIE_VILLAGER));
        all.add(new MobEntry(300, EntityType.PILLAGER));
        all.add(new MobEntry(50, EntityType.EVOKER));
        all.add(new MobEntry(5, EntityType.VINDICATOR));
        addWizards(all);
        return new MobList(x.GUID(), 1000, all);
    });
    public ExileKey<MobList, KeyInfo> GREEN = ExileKey.ofId(this, "green", x -> {
        List<MobEntry> all = new ArrayList<>();
        all.add(new MobEntry(50, EntityType.SLIME));
        all.add(new MobEntry(300, EntityType.CREEPER));
        all.add(new MobEntry(50, EntityType.PHANTOM));
        all.add(new MobEntry(1000, EntityType.ZOMBIE));
        addWizards(all);
        return new MobList(x.GUID(), 1000, all, MobListTags.HAS_FLYING_MOBS);
    });

    public ExileKey<MobList, KeyInfo> FOREST = ExileKey.ofId(this, "forest", x -> {
        List<MobEntry> all = new ArrayList<>();
        all.add(new MobEntry(700, EntityType.CAVE_SPIDER));
        all.add(new MobEntry(300, EntityType.SPIDER));
        all.add(new MobEntry(100, EntityType.WITCH));
        all.add(new MobEntry(33, EntityType.SLIME));
        addWizards(all);
        return new MobList(x.GUID(), 1000, all, MobListTags.FOREST, MobListTags.HARVEST);
    });

    public ExileKey<MobList, KeyInfo> MAP_DEFAULT = ExileKey.ofId(this, "map_default", x -> {
        List<MobEntry> all = new ArrayList<>();
        all.add(new MobEntry(1000, EntityType.ZOMBIE));
        all.add(new MobEntry(250, EntityType.SKELETON));
        all.add(new MobEntry(25, EntityType.WITCH));
        all.add(new MobEntry(25, EntityType.SLIME));
        all.add(new MobEntry(100, EntityType.SPIDER));
        all.add(new MobEntry(100, EntityType.PILLAGER));
        addWizards(all);
        return new MobList(x.GUID(), 1000, all, MobListTags.MAP);
    });

    @Override
    public void loadClass() {

    }
}
