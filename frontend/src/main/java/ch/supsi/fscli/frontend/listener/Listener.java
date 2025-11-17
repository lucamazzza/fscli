package ch.supsi.fscli.frontend.listener;

import ch.supsi.fscli.frontend.event.Event;

public interface Listener<T extends Event> {
    void update(T event);
}
