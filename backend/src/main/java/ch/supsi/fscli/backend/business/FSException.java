package ch.supsi.fscli.backend.business;

public class FSException extends Exception {
    public FSException(String message) {
        super(message);
    }
    public FSException(String message, Throwable cause) {
        super(message, cause);
    }
}
