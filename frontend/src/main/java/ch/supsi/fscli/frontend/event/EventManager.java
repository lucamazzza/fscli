package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.listener.Listener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public abstract class EventManager<T extends Event> implements EventNotifier<T>{
    @Getter
    private final List<Listener<T>> listeners;

    EventManager() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(Listener<T> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(Listener<T> listener) {
        this.listeners.remove(listener);
    }
}
