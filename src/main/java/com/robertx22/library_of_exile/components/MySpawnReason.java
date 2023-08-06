package com.robertx22.library_of_exile.components;

import net.minecraft.world.entity.MobSpawnType;

public enum MySpawnReason {
    NATURAL,
    SPAWNER,
    OTHER;

    public static MySpawnReason get(MobSpawnType reason) {

        if (reason == MobSpawnType.SPAWNER) {
            return SPAWNER;
        }
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION || reason == MobSpawnType.STRUCTURE) {
            return NATURAL;
        }

        return OTHER;
    }

}
