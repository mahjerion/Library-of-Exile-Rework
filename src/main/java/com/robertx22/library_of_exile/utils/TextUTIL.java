package com.robertx22.library_of_exile.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class TextUTIL {

    public static MutableComponent mergeList(List<? extends Component> list) {
        var c = net.minecraft.network.chat.Component.empty();

        for (int i = 0; i < list.size(); i++) {
            c.append(list.get(i));
            if (i < (list.size() - 1)) {
                c.append(Component.literal("\n"));
            }
        }
        return c;
    }
}
