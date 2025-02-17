package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.registry.helpers.ExileKey;

import java.util.HashMap;
import java.util.List;

public class RelicStatsContainer {

    public RelicStatsContainer(HashMap<String, Float> map) {
        this.map = map;
    }

    public static class InCalc {
        private HashMap<String, Float> map = new HashMap<>();

        public void add(ExactRelicStat ex) {
            if (!map.containsKey(ex.stat)) {
                map.put(ex.stat, ex.getStat().base);
            }
            float val = map.get(ex.stat) + ex.num;
            val = ex.getStat().cap(val);
            map.put(ex.stat, val);
        }
    }

    public static RelicStatsContainer calculate(List<ExactRelicStat> mods) {
        InCalc c = new InCalc();
        for (ExactRelicStat ex : mods) {
            c.add(ex);
        }
        return new RelicStatsContainer(c.map);
    }

    private HashMap<String, Float> map = new HashMap<>();

    public float get(RelicStat stat) {
        return map.getOrDefault(stat.GUID(), stat.base);
    }

    public float get(ExileKey<? extends RelicStat, ?> key) {
        var stat = key.get();
        return map.getOrDefault(stat.GUID(), stat.base);
    }
}
