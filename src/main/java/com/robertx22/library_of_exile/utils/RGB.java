package com.robertx22.library_of_exile.utils;

public class RGB {
    public RGB(int R, int G, int B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }

    public RGB() {

    }

    private int R;
    private int G;
    private int B;

    public float getR() {
        return R / 255.0F; // you need to divide by 255 for the color to work as intended..
    }

    public float getG() {
        return G / 255.0F;
    }

    public float getB() {
        return B / 255.0F;
    }

    public int getIntR() {
        return R;
    }

    public int getIntG() {
        return G;
    }

    public int getIntB() {
        return B;
    }
}
