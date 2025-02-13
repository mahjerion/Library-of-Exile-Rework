package com.robertx22.library_of_exile.database.league;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class LibLeagues extends ExileKeyHolder<League> {

    public static LibLeagues INSTANCE = new LibLeagues(Ref.REGISTER_INFO);

    public LibLeagues(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    public ExileKey<League, KeyInfo> EMPTY = ExileKey.ofId(this, "empty", x -> new EmptyLeague(x.GUID()));

    @Override
    public void loadClass() {

    }
}
