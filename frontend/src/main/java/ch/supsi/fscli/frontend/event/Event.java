package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;import lombok.Getter;

import java.util.ResourceBundle;
import java.util.Locale;

@Getter
public abstract class Event {
    private final EventError error;
    private final String message;


    public Event(EventError error, String message) {
        this.error = error;
        this.message = message;
    }

    // Default constructor uses localized message
    private Event() {
        this(EventError.ERROR, FrontendMessageProvider.get("event.unknownError"));
    }

    @Override
    public String toString() {
        return message;
    }
}
