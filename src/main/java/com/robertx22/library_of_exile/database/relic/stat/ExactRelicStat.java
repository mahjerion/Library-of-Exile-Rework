package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.database.init.LibDatabase;

public class ExactRelicStat {

    public String stat;
    public float num;

    public ExactRelicStat(String stat, float num) {
        this.stat = stat;
        this.num = num;
    }

    public RelicStat getStat() {
        return LibDatabase.RelicStats().get(stat);
    }

}
