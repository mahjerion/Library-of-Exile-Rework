package com.robertx22.library_of_exile.events.base;

import java.util.function.Consumer;

public abstract class EventConsumer<T extends ExileEvent> implements Consumer<T> {

    /**
     * Less = call first, More = call later.
     */
    public int callOrder() {
        return 0;
    }
}
