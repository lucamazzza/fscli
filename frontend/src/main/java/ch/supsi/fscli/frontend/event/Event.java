package ch.supsi.fscli.frontend.event;

import lombok.Getter;

@Getter
public abstract class Event {
    private final EventError error;
    private final String message;

    public Event(EventError error, String message) {
        this.error = error;
        this.message = message;
    }

    private Event() {
        this(EventError.ERROR, "Unknown error");
    }

    @Override
    public String toString() {
        return message;
    }
}
