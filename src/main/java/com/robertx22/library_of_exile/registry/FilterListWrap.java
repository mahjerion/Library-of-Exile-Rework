package com.robertx22.library_of_exile.registry;

import com.robertx22.library_of_exile.main.ExileLog;
import com.robertx22.library_of_exile.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilterListWrap<C extends ExileRegistry> {

    public FilterListWrap(List<C> list) {
        this.list = list;
    }

    public FilterListWrap(Collection<C> list) {
        this.list = new ArrayList<C>(list);
    }

    private boolean errorIfNothingLeft = true;

    public FilterListWrap<C> errorIfNothingLeft(boolean bool) {
        this.errorIfNothingLeft = bool;
        return this;
    }

    // in 0-100
    public float getDropChance(C c) {
        if (!list.contains(c)) {
            // don't want to error here
            // ExileLog.get().warn(c.GUID() + " has ZERO dropchance because it's not even inside the LIST");
            return 0;
        }

        int weight = c.Weight();

        int total = list.stream().mapToInt(x -> x.Weight()).sum();

        float m = weight / (float) total;

        return m * 100F;
    }


    public List<C> list = new ArrayList<C>();

    public FilterListWrap<C> of(Predicate<C> pred) {
        this.list = list.stream()
                .filter(pred)
                .collect(Collectors.toList());
        return this;
    }

    public C random() {

        if (this.list.isEmpty()) {
            if (errorIfNothingLeft) {
                ExileLog.get().warn("Items filtered too much, no possibility left, returning null!");
            }
            return null;
        }

        return RandomUtils.weightedRandom(list);
    }

}
