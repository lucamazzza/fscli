package backend.util;

import ch.supsi.fscli.backend.util.PreferencesLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesLoggerTest {
    
    private List<LogEntry> capturedLogs;
    
    @BeforeEach
    void setUp() {
        capturedLogs = new ArrayList<>();
        PreferencesLogger.setExternalListener((level, msg) -> {
            capturedLogs.add(new LogEntry(level, msg));
        });
    }
    
    @AfterEach
    void tearDown() {
        PreferencesLogger.setExternalListener(null);
    }
    
    @Test
    void testLogInfo() {
        PreferencesLogger.logInfo("Test info message");
        
        assertEquals(1, capturedLogs.size());
        assertEquals(Level.INFO, capturedLogs.get(0).level);
        assertEquals("Test info message", capturedLogs.get(0).message);
    }
    
    @Test
    void testLogError() {
        Exception e = new RuntimeException("Test exception");
        PreferencesLogger.logError("Test error", e);
        
        assertEquals(1, capturedLogs.size());
        assertEquals(Level.SEVERE, capturedLogs.get(0).level);
        assertTrue(capturedLogs.get(0).message.contains("Test error"));
        assertTrue(capturedLogs.get(0).message.contains("Test exception"));
    }
    
    @Test
    void testMultipleLogs() {
        PreferencesLogger.logInfo("Info 1");
        PreferencesLogger.logInfo("Info 2");
        PreferencesLogger.logError("Error 1", new Exception("E1"));
        
        assertEquals(3, capturedLogs.size());
    }
    
    @Test
    void testNoExternalListener() {
        PreferencesLogger.setExternalListener(null);
        
        // Should not throw exception
        assertDoesNotThrow(() -> {
            PreferencesLogger.logInfo("Test");
            PreferencesLogger.logError("Error", new Exception());
        });
    }
    
    @Test
    void testSetExternalListener() {
        List<LogEntry> newLogs = new ArrayList<>();
        PreferencesLogger.setExternalListener((level, msg) -> {
            newLogs.add(new LogEntry(level, msg));
        });
        
        PreferencesLogger.logInfo("Test");
        
        assertEquals(0, capturedLogs.size());
        assertEquals(1, newLogs.size());
    }
    
    private static class LogEntry {
        Level level;
        String message;
        
        LogEntry(Level level, String message) {
            this.level = level;
            this.message = message;
        }
    }
}
