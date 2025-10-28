package ch.supsi.fscli.backend.business;

public class NotADirectoryException extends FSException {
    public NotADirectoryException(String message) {
        super(message);
    }
}
