package ch.supsi.fscli.backend.service;

public class NotFoundException extends FSException {
    public NotFoundException(String message) {
        super(message);
    }
}
