package com.robertx22.library_of_exile.tags;

import com.robertx22.library_of_exile.registry.ExileRegistry;
import com.robertx22.library_of_exile.util.ExplainedResult;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExileTagRequirement<TAGGABLE extends ExileRegistry<?> & ITaggable<TAGGABLE>> {

    public List<ExileTagRequirementEntry> entries = new ArrayList<>();

    public ExplainedResult matches(TAGGABLE obj) {
        for (ExileTagRequirementEntry entry : entries) {
            if (!entry.matches(obj.getTags().tags)) {
                return ExplainedResult.failure(Component.literal("Tag check of " + obj.getRegistryIdPlusGuid() + ":" + " failed for: " + entry.toString()));
            }
        }
        return ExplainedResult.success();
    }

    public static class Builder<TAG extends ITaggable<TAG> & ExileRegistry<?>> {
        List<String> must = new ArrayList<>();
        List<String> any = new ArrayList<>();
        List<String> not = new ArrayList<>();


        public void mustHave(String... tags) {
            must.addAll(Arrays.asList(tags));
        }

        public void includes(String... tags) {
            any.addAll(Arrays.asList(tags));
        }

        public void excludes(String... tags) {
            not.addAll(Arrays.asList(tags));
        }

        public void mustHave(TAG... tags) {
            for (TAG tag : tags) {
                must.add(tag.GUID());
            }
        }

        public void includes(TAG... tags) {
            for (TAG tag : tags) {
                any.add(tag.GUID());
            }
        }

        public void excludes(TAG... tags) {
            for (TAG tag : tags) {
                not.add(tag.GUID());
            }
        }

        public ExileTagRequirement<TAG> build() {
            ExileTagRequirement<TAG> r = new ExileTagRequirement<>();
            r.entries.add(new ExileTagRequirementEntry(ExileTagRequirementEntry.TagRequirementCheck.HAS_ALL, must));
            r.entries.add(new ExileTagRequirementEntry(ExileTagRequirementEntry.TagRequirementCheck.HAS_ANY, any));
            r.entries.add(new ExileTagRequirementEntry(ExileTagRequirementEntry.TagRequirementCheck.HAS_NONE, not));
            return r;
        }
    }
}
