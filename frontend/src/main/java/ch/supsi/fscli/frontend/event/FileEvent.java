package ch.supsi.fscli.frontend.event;

import lombok.Getter;

import java.util.ResourceBundle;
import java.util.Locale;

@Getter
public class FileEvent extends Event {
    private final Boolean isSuccess;

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    private FileEvent() {
        super(EventError.ERROR, MESSAGES.getString("fileEvent.error"));
        this.isSuccess = false;
    }

    public FileEvent(EventError error, String message, Boolean success) {
        super(error, message);
        this.isSuccess = success;
    }

    @Override
    public String toString() {
        return isSuccess ? MESSAGES.getString("fileEvent.success") : MESSAGES.getString("fileEvent.failure");
    }
}
