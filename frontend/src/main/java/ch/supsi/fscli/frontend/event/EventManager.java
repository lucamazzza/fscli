package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.listener.Listener;

import java.util.ArrayList;
import java.util.List;

public final class EventManager<T extends Event> implements EventNotifier<T>, EventPublisher<T>{
    private final List<Listener<T>> listeners;

    public EventManager() {
        listeners = new ArrayList<>();
    }

    @Override
    public void addListener(Listener<T> listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(Listener<T> listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void notify(T event) {
        if (event == null) return;
        if (listeners.isEmpty()) return;

        try {
            listeners.forEach(listener -> listener.update(event));
        } catch (Exception e) {
            System.err.println("[ERROR] - Something went wrong when notifying the listeners.");
            e.printStackTrace();
        }
    }
}
