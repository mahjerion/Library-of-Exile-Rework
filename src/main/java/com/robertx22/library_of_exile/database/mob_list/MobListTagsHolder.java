package com.robertx22.library_of_exile.database.mob_list;

import com.robertx22.library_of_exile.tags.ExileTagList;

public class MobListTagsHolder extends ExileTagList<MobListTag> {
    @Override
    public MobListTag getInstance() {
        return new MobListTag("", "");
    }
}
