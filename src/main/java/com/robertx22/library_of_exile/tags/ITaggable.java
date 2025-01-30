package com.robertx22.library_of_exile.tags;

import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;

public interface ITaggable<T extends ExileRegistry<?>> {

    public ExileTagList<RegistryTag<T>> getTags();
}
