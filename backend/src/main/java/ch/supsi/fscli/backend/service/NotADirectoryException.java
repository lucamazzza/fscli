package ch.supsi.fscli.backend.service;

public class NotADirectoryException extends FSException {
    public NotADirectoryException(String message) {
        super(message);
    }
}
