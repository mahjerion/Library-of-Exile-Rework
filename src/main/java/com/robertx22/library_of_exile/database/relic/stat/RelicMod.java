package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.database.init.LibDatabase;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;

public class RelicMod {

    public float min;
    public float max;
    public String stat;

    public RelicMod(ExileKey<? extends RelicStat, ? extends KeyInfo> key, float min, float max) {
        this.min = min;
        this.max = max;
        this.stat = key.GUID();
    }

    public RelicStat getStat() {
        return LibDatabase.RelicStats().get(stat);
    }

    public ExactRelicStat toExact(int perc) {
        float num = (min + (max - min) * perc / 100F);
        return new ExactRelicStat(stat, num);
    }
}
