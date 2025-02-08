package com.robertx22.library_of_exile.tags;

import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.tags.tag_types.RegistryTag;
import com.robertx22.library_of_exile.util.ExplainedResult;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// The tag used and the object used to be checked against the tag
public class ExileTagRequirement<TAGGABLE extends ExileRegistry<?> & ITaggable<?>> {

    public List<ExileTagRequirementEntry> checks = new ArrayList<>();

    public ExplainedResult matches(TAGGABLE obj) {
        for (ExileTagRequirementEntry entry : checks) {
            if (!entry.matches(obj.getTags().tags)) {
                return ExplainedResult.failure(Component.literal("Tag check of " + obj.getRegistryIdPlusGuid() + ":" + " failed for: " + entry.toString()));
            }
        }
        return ExplainedResult.success();
    }

    public Builder createBuilder() {
        return new Builder();
    }

    public class Builder {
        List<String> must = new ArrayList<>();
        List<String> any = new ArrayList<>();
        List<String> not = new ArrayList<>();

        boolean addedMust = false;
        boolean addedany = false;
        boolean addednot = false;


        public Builder mustHave(String... tags) {
            must.addAll(Arrays.asList(tags));
            addedMust = true;
            return this;
        }

        public Builder includes(String... tags) {
            any.addAll(Arrays.asList(tags));
            addedany = true;
            return this;
        }

        public Builder excludes(String... tags) {
            not.addAll(Arrays.asList(tags));
            addednot = true;
            return this;
        }

        public Builder mustHave(RegistryTag<TAGGABLE>... tags) {
            for (RegistryTag<TAGGABLE> tag : tags) {
                must.add(tag.GUID());
            }
            addedMust = true;
            return this;
        }

        public Builder includes(RegistryTag<TAGGABLE>... tags) {
            for (RegistryTag<TAGGABLE> tag : tags) {
                any.add(tag.GUID());
            }
            addedany = true;
            return this;
        }

        public Builder excludes(RegistryTag<TAGGABLE>... tags) {
            for (RegistryTag<TAGGABLE> tag : tags) {
                not.add(tag.GUID());
            }
            addednot = true;
            return this;
        }

        public ExileTagRequirement<TAGGABLE> build() {
            ExileTagRequirement<TAGGABLE> r = new ExileTagRequirement<>();

            // we don't want empty entries, especially for the 'HAS_NONE' one
            if (addedMust) {
                r.checks.add(new ExileTagRequirementEntry(ExileTagRequirementEntry.TagRequirementCheck.HAS_ALL, must));
            }
            if (addedany) {
                r.checks.add(new ExileTagRequirementEntry(ExileTagRequirementEntry.TagRequirementCheck.HAS_ANY, any));
            }
            if (addednot) {
                r.checks.add(new ExileTagRequirementEntry(ExileTagRequirementEntry.TagRequirementCheck.HAS_NONE, not));
            }
            return r;
        }
    }
}
