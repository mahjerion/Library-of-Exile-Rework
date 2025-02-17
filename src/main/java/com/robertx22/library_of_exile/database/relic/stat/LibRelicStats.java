package com.robertx22.library_of_exile.database.relic.stat;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class LibRelicStats extends ExileKeyHolder<RelicStat> {

    public static LibRelicStats INSTANCE = new LibRelicStats(Ref.REGISTER_INFO);

    public LibRelicStats(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }


    public ExileKey<RelicStat, KeyInfo> EMPTY = ExileKey.ofId(this, "empty", x -> new EmptyRelicStat(x.GUID()));

    @Override
    public void loadClass() {

    }
}
