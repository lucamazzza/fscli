package backend.util;

import ch.supsi.fscli.backend.util.FilesystemLogger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilesystemLoggerTest {

    @Test
    void testLogInfoDoesNotThrow() {
        assertDoesNotThrow(() -> FilesystemLogger.logInfo("Test info message"));
    }

    @Test
    void testLogErrorDoesNotThrow() {
        assertDoesNotThrow(() -> FilesystemLogger.logError("Test error message"));
    }

    @Test
    void testLogInfoWithNull() {
        assertDoesNotThrow(() -> FilesystemLogger.logInfo(null));
    }

    @Test
    void testLogErrorWithNull() {
        assertDoesNotThrow(() -> FilesystemLogger.logError(null));
    }

    @Test
    void testLogInfoWithEmptyString() {
        assertDoesNotThrow(() -> FilesystemLogger.logInfo(""));
    }

    @Test
    void testLogErrorWithEmptyString() {
        assertDoesNotThrow(() -> FilesystemLogger.logError(""));
    }

    @Test
    void testLogInfoWithLongMessage() {
        String longMessage = "a".repeat(1000);
        assertDoesNotThrow(() -> FilesystemLogger.logInfo(longMessage));
    }

    @Test
    void testLogErrorWithLongMessage() {
        String longMessage = "error ".repeat(200);
        assertDoesNotThrow(() -> FilesystemLogger.logError(longMessage));
    }

    @Test
    void testMultipleLogInfoCalls() {
        assertDoesNotThrow(() -> {
            FilesystemLogger.logInfo("Message 1");
            FilesystemLogger.logInfo("Message 2");
            FilesystemLogger.logInfo("Message 3");
        });
    }

    @Test
    void testMultipleLogErrorCalls() {
        assertDoesNotThrow(() -> {
            FilesystemLogger.logError("Error 1");
            FilesystemLogger.logError("Error 2");
            FilesystemLogger.logError("Error 3");
        });
    }

    @Test
    void testMixedLogCalls() {
        assertDoesNotThrow(() -> {
            FilesystemLogger.logInfo("Info message");
            FilesystemLogger.logError("Error message");
            FilesystemLogger.logInfo("Another info");
        });
    }

    @Test
    void testLogInfoWithSpecialCharacters() {
        assertDoesNotThrow(() -> FilesystemLogger.logInfo("Test with \n newline and \t tab"));
    }

    @Test
    void testLogErrorWithSpecialCharacters() {
        assertDoesNotThrow(() -> FilesystemLogger.logError("Error: \\ / : * ? \" < > |"));
    }
}
