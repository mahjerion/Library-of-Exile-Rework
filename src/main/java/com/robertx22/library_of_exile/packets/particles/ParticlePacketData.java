package com.robertx22.library_of_exile.packets.particles;

import com.robertx22.library_of_exile.utils.RGB;
import com.robertx22.library_of_exile.vanilla_util.main.VanillaUTIL;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3d;


public class ParticlePacketData {

    private ParticlePacketData() {

    }

    public static ParticlePacketData empty() {
        return new ParticlePacketData();
    }

    public double x = 1;
    public double y = 1;
    public double z = 1;

    public boolean isVecPos = false;

    public double mx = 1;
    public double my = 1;
    public double mz = 1;

    public ParticleEnum type;

    public float radius = 1;

    public int amount = 1;

    public RGB color;

    public String particleID;

    public <T extends ParticleOptions> ParticleOptions getParticleType() {
        ParticleType<T> particleType = (ParticleType<T>) VanillaUTIL.REGISTRY.particles().get(new ResourceLocation(particleID));

        if (particleType instanceof ParticleOptions) {
            return (ParticleOptions) particleType;
        } else return ParticleTypes.CRIT;
    }

    public ParticlePacketData motion(Vector3d v) {
        mx = v.x;
        my = v.y;
        mz = v.z;
        return this;
    }

    public ParticlePacketData type(ParticleType type) {
        this.particleID = VanillaUTIL.REGISTRY.particles().getKey(type)
                .toString();
        return this;
    }

    public ParticlePacketData amount(int num) {
        this.amount = num;
        return this;
    }

    public ParticlePacketData radius(double rad) {
        this.radius = (float) rad;
        return this;
    }

    public ParticlePacketData radius(float rad) {
        this.radius = rad;
        return this;
    }

    public ParticlePacketData color(RGB color) {
        this.color = color;
        return this;
    }

    public Vector3d getPos() {
        return new Vector3d(x, y, z);
    }

    public BlockPos getBlockPos() {
        return new BlockPos((int) x, (int) y, (int) z);
    }

    public ParticlePacketData(Vector3d pos, ParticleEnum type) {
        x = pos.x();
        y = pos.y();
        z = pos.z();
        this.isVecPos = true;
        this.type = type;
    }

    public ParticlePacketData(BlockPos pos, ParticleEnum type) {
        x = pos.getX();
        y = pos.getY();
        z = pos.getZ();
        this.type = type;
    }
}
