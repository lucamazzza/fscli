package ch.supsi.fscli.backend.service;

public class FSException extends Exception {
    public FSException(String message) {
        super(message);
    }
    public FSException(String message, Throwable cause) {
        super(message, cause);
    }
}
