package ch.supsi.fscli.frontend.event;

import lombok.Getter;

@Getter
public class FileEvent extends Event{
    private final Boolean isSuccess;

    private FileEvent() {
        super(EventError.ERROR, "Error");
        this.isSuccess = false;
    }

    public FileEvent(EventError error, String message, Boolean success) {
        super(error, message);
        this.isSuccess = success;
    }

    @Override
    public String toString() {
        return "Is successful";
    }
}
