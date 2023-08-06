package com.robertx22.library_of_exile.registry;

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
                System.out.println("Items filtered too much, no possibility left, returning null!");
            }
            return null;
        }

        return RandomUtils.weightedRandom(list);
    }

}
