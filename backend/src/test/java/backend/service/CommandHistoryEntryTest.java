package backend.service;

import ch.supsi.fscli.backend.service.CommandHistoryEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandHistoryEntryTest {

    @Test
    void testConstructor() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryEntry entry = new CommandHistoryEntry("ls -l", true, timestamp);
        
        assertEquals("ls -l", entry.getCommand());
        assertTrue(entry.isSuccessful());
        assertEquals(timestamp, entry.getTimestamp());
    }

    @Test
    void testGetCommand() {
        CommandHistoryEntry entry = new CommandHistoryEntry("pwd", true, 0L);
        assertEquals("pwd", entry.getCommand());
    }

    @Test
    void testIsSuccessful() {
        CommandHistoryEntry entry1 = new CommandHistoryEntry("mkdir test", true, 0L);
        assertTrue(entry1.isSuccessful());
        
        CommandHistoryEntry entry2 = new CommandHistoryEntry("invalid", false, 0L);
        assertFalse(entry2.isSuccessful());
    }

    @Test
    void testGetTimestamp() {
        long timestamp = 1234567890123L;
        CommandHistoryEntry entry = new CommandHistoryEntry("cd /", true, timestamp);
        assertEquals(timestamp, entry.getTimestamp());
    }

    @Test
    void testGetFormattedTimestamp() {
        long timestamp = 1609459200000L; // 2021-01-01 00:00:00 UTC
        CommandHistoryEntry entry = new CommandHistoryEntry("test", true, timestamp);
        
        String formatted = entry.getFormattedTimestamp();
        assertNotNull(formatted);
        assertTrue(formatted.contains("2021") || formatted.contains("2020")); // Timezone dependent
        assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void testToStringSuccessful() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryEntry entry = new CommandHistoryEntry("mkdir dir1", true, timestamp);
        
        String str = entry.toString();
        assertTrue(str.contains("✓"));
        assertTrue(str.contains("mkdir dir1"));
    }

    @Test
    void testToStringFailed() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryEntry entry = new CommandHistoryEntry("invalid", false, timestamp);
        
        String str = entry.toString();
        assertTrue(str.contains("✗"));
        assertTrue(str.contains("invalid"));
    }

    @Test
    void testToStringContainsTimestamp() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryEntry entry = new CommandHistoryEntry("ls", true, timestamp);
        
        String str = entry.toString();
        assertTrue(str.contains("["));
        assertTrue(str.contains("]"));
    }

    @Test
    void testNullCommand() {
        CommandHistoryEntry entry = new CommandHistoryEntry(null, true, 0L);
        assertNull(entry.getCommand());
    }

    @Test
    void testEmptyCommand() {
        CommandHistoryEntry entry = new CommandHistoryEntry("", false, 0L);
        assertEquals("", entry.getCommand());
    }

    @Test
    void testZeroTimestamp() {
        CommandHistoryEntry entry = new CommandHistoryEntry("test", true, 0L);
        assertEquals(0L, entry.getTimestamp());
        assertNotNull(entry.getFormattedTimestamp());
    }

    @Test
    void testComplexCommand() {
        String command = "ln -s /very/long/path/to/target /another/long/path";
        CommandHistoryEntry entry = new CommandHistoryEntry(command, true, System.currentTimeMillis());
        
        assertEquals(command, entry.getCommand());
        assertTrue(entry.toString().contains(command));
    }

    @Test
    void testMultipleEntries() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryEntry entry1 = new CommandHistoryEntry("cmd1", true, timestamp);
        CommandHistoryEntry entry2 = new CommandHistoryEntry("cmd2", false, timestamp + 1000);
        
        assertNotEquals(entry1.getCommand(), entry2.getCommand());
        assertNotEquals(entry1.isSuccessful(), entry2.isSuccessful());
        assertNotEquals(entry1.getTimestamp(), entry2.getTimestamp());
    }
}
