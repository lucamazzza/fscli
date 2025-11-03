package ch.supsi.fscli.backend.service;

public class AlreadyExistsException extends FSException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
