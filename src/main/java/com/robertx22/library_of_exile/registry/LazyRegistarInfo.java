package com.robertx22.library_of_exile.registry;

import com.robertx22.library_of_exile.registry.register_info.ExileRegistrationInfo;

import java.util.ArrayList;
import java.util.List;

public class LazyRegistarInfo {

    public List<ExileRegistry<?>> entries = new ArrayList<>();
    public ExileRegistrationInfo info;


    public LazyRegistarInfo(ExileRegistrationInfo info) {
        this.info = info;
    }
    

}
