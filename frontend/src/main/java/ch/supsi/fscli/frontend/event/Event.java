package ch.supsi.fscli.frontend.event;

import lombok.Getter;

import java.util.ResourceBundle;
import java.util.Locale;

@Getter
public abstract class Event {
    private final EventError error;
    private final String message;

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public Event(EventError error, String message) {
        this.error = error;
        this.message = message;
    }

    // Default constructor uses localized message
    private Event() {
        this(EventError.ERROR, MESSAGES.getString("event.unknownError"));
    }

    @Override
    public String toString() {
        return message;
    }
}
