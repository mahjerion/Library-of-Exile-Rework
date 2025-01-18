package com.robertx22.library_of_exile.util;

import com.robertx22.library_of_exile.registry.IGUID;

public abstract class AutoHashClass implements IGUID {

    @Override
    public abstract int hashCode();

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode() == this.hashCode();
    }
}
