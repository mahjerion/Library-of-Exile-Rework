package com.robertx22.library_of_exile.tags;

import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ExileTagList<TAG extends RegistryTag<?>> {

    public List<String> tags = new ArrayList<>();

    public ExileTagList() {
    }

    public ExileTagList(List<String> tags) {
        this.tags = tags;
    }

    public abstract TAG getInstance();

    public List<TAG> toTags() {
        return tags.stream().map(x -> (TAG) getInstance().fromTagString(x)).collect(Collectors.toList());
    }
}
