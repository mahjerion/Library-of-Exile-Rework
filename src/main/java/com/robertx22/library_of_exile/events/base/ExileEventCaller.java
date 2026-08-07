package com.robertx22.library_of_exile.events.base;

import com.robertx22.library_of_exile.main.ExileLog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExileEventCaller<T extends ExileEvent> {

    List<EventConsumer<T>> events = new ArrayList<>();

    public ExileEventCaller() {
    }

    // this makes sure there aren't random concurrentmodification errors etc
    Lock lock = new ReentrantLock();

    public T callEvents(T event) {
        events.forEach(x -> {
            if (!event.canceled) {
                try {
                    x.accept(event);
                } catch (Exception e) {
                    // still swallow and carry on to the next consumer - four mods rely on one bad
                    // listener not taking the others down. but it goes through the logger now: as a
                    // bare printStackTrace this landed in STDERR with nothing saying which event it
                    // came from, which is how chunk generation failures stayed invisible in server
                    // logs for two rounds of investigation.
                    ExileLog.get().error("Exile event consumer threw while handling "
                            + event.getClass().getSimpleName() + " (" + x.getClass().getName() + ")", e);
                }
            }
        });
        return event;
    }

    public void register(EventConsumer<T> t) {
        lock.lock();
        try {
            this.events.add(t);
            events.sort(Comparator.comparingInt(x -> x.callOrder()));
        } finally {
            lock.unlock();
        }
    }

}
