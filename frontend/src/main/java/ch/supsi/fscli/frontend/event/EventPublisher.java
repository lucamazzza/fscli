package ch.supsi.fscli.frontend.event;

public interface EventPublisher<T extends Event> {
    void notify(T event);
}
