package com.robertx22.library_of_exile.utils.geometry;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

import java.util.function.Consumer;

public abstract class ShapeHelper {

    public abstract Vector3d getRandomPos();

    public abstract Vector3d getRandomEdgePos();


    public void doXTimes(int times, Consumer<XTimesData> action) {
        for (int i = 0; i < times; i++) {
            float multi = (float) i / (float) times;
            XTimesData data = new XTimesData(i, times, multi);
            action.accept(data);
        }
    }

    public void spawnParticle(Level world, Vector3d pos, ParticleOptions particle) {
        spawnParticle(world, pos, particle, new Vector3d(0, 0, 0));
    }

    public void spawnParticle(Level world, Vector3d pos, ParticleOptions particle, Vector3d vel) {

        if (world.isClientSide()) {
            world.addParticle(particle, pos.x, pos.y, pos.z, vel.x, vel.y, vel.z);
        } else {
            if (world instanceof ServerLevel sw) {
                sw.sendParticles(particle, pos.x, pos.y, pos.z, 1, vel.x, vel.y, vel.z, 0);
            }
        }
    }

}
