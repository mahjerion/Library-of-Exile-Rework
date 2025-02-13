package com.robertx22.library_of_exile.database.extra_map_content;

import com.robertx22.library_of_exile.main.Ref;
import com.robertx22.library_of_exile.registry.helpers.ExileKey;
import com.robertx22.library_of_exile.registry.helpers.ExileKeyHolder;
import com.robertx22.library_of_exile.registry.helpers.KeyInfo;
import com.robertx22.library_of_exile.registry.register_info.ModRequiredRegisterInfo;

public class LibMapContents extends ExileKeyHolder<MapContent> {

    public static LibMapContents INSTANCE = new LibMapContents(Ref.REGISTER_INFO);

    public LibMapContents(ModRequiredRegisterInfo info) {
        super(info);
    }

    public ExileKey<MapContent, KeyInfo> EMPTY = ExileKey.ofId(this, "empty", x -> MapContent.of(x.GUID(), 0, "", 0, 0));

    @Override
    public void loadClass() {

    }
}
