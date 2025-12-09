package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.listener.Listener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Event manager for the observer pattern.
 * Manages listeners and notifies them of events.
 */
public final class EventManager<T extends Event> implements EventNotifier<T>, EventPublisher<T>{
    private final List<Listener<T>> listeners;

    public EventManager() {
        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void addListener(Listener<T> listener) {
        if (listener != null && !listeners.contains(listener)) {
            ((CopyOnWriteArrayList<Listener<T>>) listeners).addIfAbsent(listener);
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

        for (Listener<T> listener : listeners) {
            try {
                listener.update(event);
            } catch (Exception e) {
                System.err.println(FrontendMessageProvider.get("error.listener"));
                e.printStackTrace();
            }
        }
    }
}
