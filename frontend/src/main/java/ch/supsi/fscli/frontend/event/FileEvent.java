package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;import lombok.Getter;

import java.util.ResourceBundle;
import java.util.Locale;

@Getter
public class FileEvent extends Event {
    private final Boolean isSuccess;


    private FileEvent() {
        super(EventError.ERROR, FrontendMessageProvider.get("fileEvent.error"));
        this.isSuccess = false;
    }

    public FileEvent(EventError error, String message, Boolean success) {
        super(error, message);
        this.isSuccess = success;
    }

    @Override
    public String toString() {
        return isSuccess ? FrontendMessageProvider.get("fileEvent.success") : FrontendMessageProvider.get("fileEvent.failure");
    }
}
