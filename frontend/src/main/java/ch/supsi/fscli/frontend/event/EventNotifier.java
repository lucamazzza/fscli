package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.listener.Listener;

public interface EventNotifier<T extends Event> {
    void addListener(Listener<T> listener);
    void removeListener(Listener<T> listener);
}
