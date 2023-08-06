package com.robertx22.library_of_exile.utils.geometry;


import net.minecraft.core.BlockPos;
import org.joml.Vector3d;

public class Circle2d extends ShapeHelper {

    public Vector3d middle;
    public float radius;

    public Circle2d(MyPosition middle, float radius) {
        this.middle = new Vector3d(middle.x, middle.y, middle.z);
        this.radius = radius;
    }

    public Circle2d(Vector3d middle, float radius) {
        this.middle = middle;
        this.radius = radius;
    }

    public Circle2d(BlockPos pos, float radius) {
        this.middle = new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
        this.radius = radius;
    }

    @Override
    public Vector3d getRandomPos() {

        double x = middle.x;
        double y = middle.y;
        double z = middle.z;

        double u = Math.random();
        double v = Math.random();
        double theta = 6.283185307179586D * u;
        double phi = Math.acos(2.0D * v - 1.0D);
        double xpos = x + (double) radius * Math.sin(phi) * Math.cos(theta);
        double zpos = z + (double) radius * Math.cos(phi);
        return new Vector3d(xpos, y, zpos);
    }

    @Override
    public Vector3d getRandomEdgePos() {
        return getEdgePos((float) Math.random());
    }


    public Vector3d getEdgePos(float angleMulti) {

        double x = middle.x;
        double y = middle.y;
        double z = middle.z;

        double angle = angleMulti * Math.PI * 2;


        double xpos = x + Math.cos(angle) * radius;
        double zpos = z + Math.sin(angle) * radius;

        return new Vector3d(xpos, y, zpos);
    }
}
