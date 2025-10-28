package ch.supsi.fscli.backend.business;

public class NotFoundException extends FSException {
    public NotFoundException(String message) {
        super(message);
    }
}
