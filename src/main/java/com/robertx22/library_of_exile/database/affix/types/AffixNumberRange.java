package com.robertx22.library_of_exile.database.affix.types;

public class AffixNumberRange {

    public float min;
    public float max;

    public AffixNumberRange(float min, float max) {
        this.min = min;
        this.max = max;
    }

    public float getNumber(int perc) {
        float num = (min + (max - min) * perc / 100F);
        return num;
    }

    public int getInt(int perc) {
        // todo maybe better way of clamping to int
        return Math.round(getNumber(perc));
    }
}
