package com.robertx22.library_of_exile.components;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.utils.LoadSave;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityInfoComponent {

    public static final ResourceLocation RESOURCE = new ResourceLocation(Ref.MODID, "entity_info");
    public static Capability<DefaultImpl> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static IEntityInfo get(LivingEntity entity) {
        return entity.getCapability(INSTANCE).orElse(new EntityInfoComponent.DefaultImpl(entity));
    }


    private static final String DMG_STATS = "dmg_stats";
    private static final String SPAWN_POS = "spawn_pos";
    private static final String SPAWN_REASON = "spawn";
    private static final String CUSTOM_SAVE_DATA = "custom_data";


    public interface IEntityInfo {

        EntityDmgStatsData getDamageStats();

        BlockPos getSpawnPos();

        void spawnInit(Entity en);

        MySpawnReason getSpawnReason();

        // CurrentMobAffixesData getMobAffixes();

        void setSpawnReasonOnCreate(MobSpawnType reason);
    }


    public static class DefaultImpl implements ICap, IEntityInfo {

        final LazyOptional<DefaultImpl> supp = LazyOptional.of(() -> this);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == INSTANCE) {
                return supp.cast();
            }
            return LazyOptional.empty();
        }

        // public CurrentMobAffixesData mobAffixes = new CurrentMobAffixesData();

        EntityDmgStatsData dmgStats = new EntityDmgStatsData();

        private BlockPos spawnPos;
        public MySpawnReason spawnReason = null;

        public LivingEntity owner;

        public DefaultImpl(LivingEntity en) {
            this.owner = en;
        }

        @Override
        public BlockPos getSpawnPos() {
            if (isSpawnInit()) {
                return spawnPos;
            }
            return this.owner.blockPosition();
        }

        private boolean isSpawnInit() {
            return spawnPos != null && !this.spawnPos.equals(BlockPos.ZERO);
        }

        @Override
        public void spawnInit(Entity entity) {
            if (isSpawnInit()) {
                this.spawnPos = entity.blockPosition();
            }
        }

        @Override
        public MySpawnReason getSpawnReason() {
            return spawnReason == null ? MySpawnReason.OTHER : spawnReason;
        }

        /*
        @Override
        public CurrentMobAffixesData getMobAffixes() {
            return mobAffixes;
        }

         */

        @Override
        public void setSpawnReasonOnCreate(MobSpawnType reason) {
            if (spawnReason == null) {
                spawnReason = MySpawnReason.get(reason);
            }
        }

        @Override
        public EntityDmgStatsData getDamageStats() {
            return dmgStats;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            try {
                if (dmgStats != null) {
                    LoadSave.Save(dmgStats, nbt, DMG_STATS);
                }
                /*
                if (mobAffixes != null) {
                    LoadSave.Save(mobAffixes, nbt, MOB_AFFIXES);
                }

                 */
                if (spawnPos != null) {
                    nbt.putLong(SPAWN_POS, spawnPos.asLong());
                }

                nbt.putString(getSpawnReason().name(), SPAWN_REASON);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {

            try {

                /*
                this.mobAffixes = LoadSave.Load(CurrentMobAffixesData.class, new CurrentMobAffixesData(), nbt, MOB_AFFIXES);
                if (mobAffixes == null) {
                    mobAffixes = new CurrentMobAffixesData();
                }

                 */

                this.dmgStats = LoadSave.Load(EntityDmgStatsData.class, new EntityDmgStatsData(), nbt, DMG_STATS);
                if (dmgStats == null) {
                    dmgStats = new EntityDmgStatsData();
                }
                this.spawnPos = BlockPos.of(nbt.getLong(SPAWN_POS));

                String res = nbt.getString(SPAWN_REASON);
                if (res != null && !res.isEmpty()) {
                    this.spawnReason = MySpawnReason.valueOf(res);
                } else {
                    this.spawnReason = MySpawnReason.OTHER;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getCapIdForSyncing() {
            return "entity_info";
        }
    }

}
