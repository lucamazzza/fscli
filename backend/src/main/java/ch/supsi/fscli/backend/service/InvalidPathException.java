package ch.supsi.fscli.backend.service;

public class InvalidPathException extends FSException {
    public InvalidPathException(String message) {
        super(message);
    }
}
