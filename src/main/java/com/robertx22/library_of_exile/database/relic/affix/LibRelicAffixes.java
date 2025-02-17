package com.robertx22.library_of_exile.database.relic.affix;

import com.robertx22.library_of_exile.database.map_data_block.MapDataBlock;
import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class LibRelicAffixes extends ExileKeyHolder<MapDataBlock> {

    public static LibRelicAffixes INSTANCE = new LibRelicAffixes(Ref.REGISTER_INFO);

    public LibRelicAffixes(ModRequiredRegisterInfo modRegisterInfo) {
        super(modRegisterInfo);
    }

    public ExileKey<RelicAffix, KeyInfo> EMPTY = ExileKey.ofId(this, "empty", x -> new EmptyRelicAffix(x.GUID()));

    @Override
    public void loadClass() {

    }
}
