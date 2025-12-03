package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.util.AppError;

public record CommandLineEvent(AppError error, String currentDir, String output, String outputError) implements Event {
    @Override
    public String toString() {
        return error().getDefaultMessage();
    }
}
