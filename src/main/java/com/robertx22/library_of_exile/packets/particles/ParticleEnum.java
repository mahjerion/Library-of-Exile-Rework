package com.robertx22.library_of_exile.packets.particles;

import com.robertx22.library_of_exile.main.Packets;
import com.robertx22.library_of_exile.utils.GeometryUtils;
import com.robertx22.library_of_exile.utils.RGB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3d;
import org.joml.Vector3f;

public enum ParticleEnum {

    THORNS() {
        @Override
        public void activate(ParticlePacketData data, Level world) {
            Vector3d center = getCenter(data);

            for (int i = 0; i < data.amount; i++) {
                Vector3d p = GeometryUtils.randomPos(center, world.getRandom(), data.radius);
                Vector3d m = GeometryUtils.randomMotion(center, world.random);

                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.BIRCH_LEAVES.defaultBlockState()),
                        p.x, p.y, p.z, m.x, m.y, m.z
                );
                world.addParticle(ParticleTypes.ITEM_SLIME, p.x, p.y, p.z, m.x, m.y, m.z);

            }
        }
    },

    AOE() {
        @Override
        public void activate(ParticlePacketData data, Level world) {
            Vector3d p = getCenter(data);

            for (int i = 0; i < data.amount; i++) {
                Vector3d r = GeometryUtils.getRandomPosInRadiusCircle(p.x, p.y, p.z, data.radius);
                world.addParticle(data.getParticleType(), r.x, r.y, r.z, data.mx, data.my, data.mz);
            }
        }
    },
    CIRCLE_REDSTONE() {
        @Override
        public void activate(ParticlePacketData data, Level world) {
            Vector3d p = getCenter(data);

            for (int i = 0; i < data.radius * 60; i++) {
                Vector3d r = GeometryUtils.getRandomPosInRadiusCircle(p.x, p.y, p.z, data.radius);
                this.spawnRedstone(world, data.color, r.x, r.y, r.z);
            }
        }
    },

    NOVA_REDSTONE() {
        @Override
        public void activate(ParticlePacketData data, Level world) {

            Vector3d p = getCenter(data);

            for (int i = 0; i < data.radius * 50; i++) {
                Vector3d r = GeometryUtils.getRandomHorizontalPosInRadiusCircle(p.x, p.y, p.z, data.radius);
                this.spawnRedstone(world, data.color, r.x, r.y, r.z);
            }
        }
    },
    NOVA() {
        @Override
        public void activate(ParticlePacketData data, Level world) {

            Vector3d p = getCenter(data);

            for (int i = 0; i < data.amount; i++) {

                Vector3d r = GeometryUtils.getRandomHorizontalPosInRadiusCircle(p.x, p.y, p.z, data.radius);
                world.addParticle(data.getParticleType(), r.x, r.y, r.z, 0, 0, 0);
            }
        }
    };

    ParticleEnum() {

    }

    public static void sendToClients(Entity source, ParticlePacketData data) {
        if (source.level().isClientSide) {
            try {
                data.type.activate(data, source.level());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Packets.sendToTracking(new ParticlePacket(data), source);
        }
    }

    public static void sendToClients(BlockPos pos, Level world, ParticlePacketData data) {
        if (!world.isClientSide) {
            Packets.sendToTracking(new ParticlePacket(data), pos, world);
        } else {
            data.type.activate(data, world);
        }
    }

    public Vector3d getCenter(ParticlePacketData data) {
        if (data.isVecPos) {
            return data.getPos();
        } else {
            BlockPos pos = data.getBlockPos();
            return new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        }
    }

    public void spawnRedstone(Level world, RGB color, double xpos, double ypos, double zpos) {

        DustParticleOptions data = new DustParticleOptions(new Vector3f(color.getR(), color.getG(), color.getB()), 1F);
        world.addParticle(data, true, xpos, ypos, zpos, 1, 1, 1);
    }

    public abstract void activate(ParticlePacketData data, Level world);

}
