package com.robertx22.library_of_exile.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

// todo tag localization
public class ExileTagRequirementEntry {

    public TagRequirementCheck check = TagRequirementCheck.HAS_ALL;
    public List<String> tags = new ArrayList<>();

    public ExileTagRequirementEntry(TagRequirementCheck check, List<String> tags) {
        this.check = check;
        this.tags = tags;
    }

    public ExileTagRequirementEntry(TagRequirementCheck check, String... tags) {
        this.check = check;
        this.tags = Arrays.asList(tags);
    }

    // wow i didnt know there's automatic tostring methods
    @Override
    public String toString() {
        return "ExileTagRequirementEntry{" +
                "check=" + check +
                ", tags=" + tags +
                '}';
    }

    public enum TagRequirementCheck {
        HAS_ALL() {
            @Override
            public boolean matches(List<String> objTags, List<String> tags) {
                return new HashSet<>(objTags).containsAll(tags);
            }
        },
        HAS_ANY() {
            @Override
            public boolean matches(List<String> objTags, List<String> tags) {
                return tags.stream().anyMatch(x -> objTags.contains(x));
            }
        },
        HAS_NONE() {
            @Override
            public boolean matches(List<String> objTags, List<String> tags) {
                return tags.stream().anyMatch(x -> !objTags.contains(x));
            }
        };

        public abstract boolean matches(List<String> objTags, List<String> tags);
    }

    public boolean matches(List<String> objTags) {
        return this.check.matches(objTags, tags);
    }
}
