package ch.supsi.fscli.frontend.event;

@FunctionalInterface
public interface EventNotifier<T extends Event> {
    void notify(T event);
}
