package com.robertx22.library_of_exile.tags;

import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;

public interface ITaggable<T extends RegistryTag<?>> {

    public ExileTagList<T> getTags();
}
