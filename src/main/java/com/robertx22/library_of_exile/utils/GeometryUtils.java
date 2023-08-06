package com.robertx22.library_of_exile.utils;

import net.minecraft.util.RandomSource;
import org.joml.Vector3d;

public class GeometryUtils {

    public static Vector3d getRandomHorizontalPosInRadiusCircle(Vector3d pos, float radius) {
        return getRandomHorizontalPosInRadiusCircle(pos.x, pos.y, pos.z, radius);
    }

    public static Vector3d getRandomHorizontalPosInRadiusCircle(double x, double y, double z, float radius) {

        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        double xpos = x + (radius * Math.sin(phi) * Math.cos(theta));
        double ypos = y;
        double zpos = z + (radius * Math.cos(phi));

        return new Vector3d(xpos, ypos, zpos);

    }

    public static Vector3d getRandomPosInRadiusCircle(Vector3d p, float radius) {
        return getRandomPosInRadiusCircle(p.x, p.y, p.z, radius);
    }

    public static Vector3d randomMotion(Vector3d p, RandomSource rand) {

        double x = rand.nextDouble() - 0.5D * 2;
        double y = -rand.nextDouble();
        double z = rand.nextDouble() - 0.5D * 2;

        return new Vector3d(x, y, z);
    }

    public static Vector3d randomPos(Vector3d p, RandomSource rand, float radius) {

        double x = p.x + rand.nextDouble() * radius - 0.5D * radius;
        double y = p.y + rand.nextDouble() * radius - 0.5D * radius;
        double z = p.z + rand.nextDouble() * radius - 0.5D * radius;

        return new Vector3d(x, y, z);

    }

    public static Vector3d getRandomPosInRadiusCircle(double x, double y, double z, float radius) {

        double u = Math.random();
        double v = Math.random();
        double theta = 2 * Math.PI * u;
        double phi = Math.acos(2 * v - 1);
        double xpos = x + (radius * Math.sin(phi) * Math.cos(theta));
        double ypos = y + (radius * Math.sin(phi) * Math.sin(theta));
        double zpos = z + (radius * Math.cos(phi));

        return new Vector3d(xpos, ypos, zpos);

    }

}
