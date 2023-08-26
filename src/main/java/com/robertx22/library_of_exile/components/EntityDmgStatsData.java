package com.robertx22.library_of_exile.components;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EntityDmgStatsData {

    private HashMap<String, Float> map = new HashMap<>();

    private float enviroOrMobDmg = 0;

    public float getEnviroOrMobDmg() {
        return enviroOrMobDmg;
    }

    public void onDamagedBy(Entity entity, float dmg) {

        if (map.size() > 300) {
            map = new HashMap<>();
            enviroOrMobDmg = 0;
        }

        if (entity instanceof Player) {
            String id = entity.getUUID().toString();
            map.put(id, dmg + map.getOrDefault(id, 0F));
        } else {
            enviroOrMobDmg += dmg;
        }
    }

    public LivingEntity getHighestDamager(ServerLevel world) {

        Optional<Map.Entry<String, Float>> opt = map.entrySet()
                .stream()
                .max((one, two) -> one.getValue() >= two.getValue() ? 1 : -1);

        if (opt.isPresent()) {

            String id = opt.get()
                    .getKey();

            Entity en = world.getEntity(UUID.fromString(id));

            if (en instanceof LivingEntity) {
                return (LivingEntity) en;
            }
        }
        return null;
    }

    public float getTotalPlayerDamage() {
        if (map.isEmpty()) {
            return 0;
        }
        return map.values()
                .stream()
                .reduce((x, y) -> x + y)
                .get();
    }

}
