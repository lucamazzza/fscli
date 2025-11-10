package ch.supsi.fscli.frontend.listener;

import ch.supsi.fscli.frontend.event.Event;

public interface Listener {
    void update(Event event);
}
