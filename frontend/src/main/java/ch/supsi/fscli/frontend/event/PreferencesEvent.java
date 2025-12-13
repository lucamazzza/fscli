package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.util.AppError;

public record PreferencesEvent(AppError error) implements Event {
    @Override
    public String toString() {
        return error().getDefaultMessage();
    }
}
