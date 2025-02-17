package com.robertx22.library_of_exile.database.relic.relic_type;

import com.robertx22.library_of_exile.util.TranslateInfo;

public class EmptyRelicType extends RelicType {

    public EmptyRelicType(String id) {
        super(id, new TranslateInfo("", "s"));
        this.weight = 0;
    }
}
