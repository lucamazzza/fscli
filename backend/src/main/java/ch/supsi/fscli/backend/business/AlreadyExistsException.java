package ch.supsi.fscli.backend.business;

public class AlreadyExistsException extends FSException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
