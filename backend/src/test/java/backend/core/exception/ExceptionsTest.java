package backend.core.exception;

import ch.supsi.fscli.backend.core.exception.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {
    
    @Test
    void testFSException_WithMessage() {
        String message = "Test exception";
        FSException exception = new FSException(message);
        
        assertEquals(message, exception.getMessage());
    }
    
    @Test
    void testFSException_WithMessageAndCause() {
        String message = "Test exception";
        Throwable cause = new RuntimeException("Cause");
        FSException exception = new FSException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void testAlreadyExistsException() {
        String message = "File already exists";
        AlreadyExistsException exception = new AlreadyExistsException(message);
        
        assertEquals(message, exception.getMessage());
        assertInstanceOf(FSException.class, exception);
    }
    
    @Test
    void testInvalidCommandException() {
        String message = "Invalid command";
        InvalidCommandException exception = new InvalidCommandException(message);
        
        assertEquals(message, exception.getMessage());
        assertInstanceOf(FSException.class, exception);
    }
    
    @Test
    void testInvalidPathException() {
        String message = "Invalid path";
        InvalidPathException exception = new InvalidPathException(message);
        
        assertEquals(message, exception.getMessage());
        assertInstanceOf(FSException.class, exception);
    }
    
    @Test
    void testNotADirectoryException() {
        String message = "Not a directory";
        NotADirectoryException exception = new NotADirectoryException(message);
        
        assertEquals(message, exception.getMessage());
        assertInstanceOf(FSException.class, exception);
    }
    
    @Test
    void testNotFoundException() {
        String message = "File not found";
        NotFoundException exception = new NotFoundException(message);
        
        assertEquals(message, exception.getMessage());
        assertInstanceOf(FSException.class, exception);
    }
}
