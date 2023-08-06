package com.robertx22.library_of_exile.utils.geometry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class MyPosition extends Vec3 {

    public MyPosition(double x, double y, double z) {
        super(x, y, z);
    }

    public MyPosition(Vec3 vec) {
        super(vec.x, vec.y, vec.z);
    }

    public MyPosition(Vector3f vec) {
        super(vec.x, vec.y, vec.z);
    }

    public MyPosition(Vector3d vec) {
        super(vec.x, vec.y, vec.z);
    }

    public MyPosition(BlockPos pos) {
        super(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public MyPosition(BlockPos pos, boolean dontAddMiddle) {
        super(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos asBlockPos() {
        return new BlockPos((int) x, (int) y, (int) z);
    }

    public Vec3 asVec3D() {
        return new Vec3(x, y, z);
    }

    public Vector3d asVector3D() {
        return new Vector3d(x, y, z);
    }
}
