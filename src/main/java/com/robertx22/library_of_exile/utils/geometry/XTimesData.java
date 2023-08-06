package com.robertx22.library_of_exile.utils.geometry;

public class XTimesData {
    public int index = 0;
    public int currentNumber = 0;
    public int totalTimes = 0;
    public float multi = 0;

    public XTimesData(int index, int totalTimes, float multi) {
        this.index = index;
        this.currentNumber = index + 1;
        this.totalTimes = totalTimes;
        this.multi = multi;
    }
}
