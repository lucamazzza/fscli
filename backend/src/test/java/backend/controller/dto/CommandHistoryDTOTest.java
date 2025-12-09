package backend.controller.dto;

import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandHistoryDTOTest {

    @Test
    void testDefaultConstructor() {
        CommandHistoryDTO dto = new CommandHistoryDTO();
        assertNull(dto.getCommand());
        assertFalse(dto.isSuccessful());
        assertEquals(0, dto.getTimestamp());
    }

    @Test
    void testParameterizedConstructor() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryDTO dto = new CommandHistoryDTO("ls -l", true, timestamp);
        
        assertEquals("ls -l", dto.getCommand());
        assertTrue(dto.isSuccessful());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testSetCommand() {
        CommandHistoryDTO dto = new CommandHistoryDTO();
        dto.setCommand("pwd");
        assertEquals("pwd", dto.getCommand());
    }

    @Test
    void testSetSuccessful() {
        CommandHistoryDTO dto = new CommandHistoryDTO();
        dto.setSuccessful(true);
        assertTrue(dto.isSuccessful());
        
        dto.setSuccessful(false);
        assertFalse(dto.isSuccessful());
    }

    @Test
    void testSetTimestamp() {
        CommandHistoryDTO dto = new CommandHistoryDTO();
        long timestamp = 1234567890L;
        dto.setTimestamp(timestamp);
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testSuccessfulCommandHistory() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryDTO dto = new CommandHistoryDTO("mkdir test", true, timestamp);
        
        assertTrue(dto.isSuccessful());
        assertEquals("mkdir test", dto.getCommand());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testFailedCommandHistory() {
        long timestamp = System.currentTimeMillis();
        CommandHistoryDTO dto = new CommandHistoryDTO("invalid command", false, timestamp);
        
        assertFalse(dto.isSuccessful());
        assertEquals("invalid command", dto.getCommand());
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testNullCommand() {
        CommandHistoryDTO dto = new CommandHistoryDTO(null, true, 0L);
        assertNull(dto.getCommand());
    }

    @Test
    void testEmptyCommand() {
        CommandHistoryDTO dto = new CommandHistoryDTO("", false, 0L);
        assertEquals("", dto.getCommand());
    }

    @Test
    void testZeroTimestamp() {
        CommandHistoryDTO dto = new CommandHistoryDTO("test", true, 0L);
        assertEquals(0L, dto.getTimestamp());
    }

    @Test
    void testNegativeTimestamp() {
        CommandHistoryDTO dto = new CommandHistoryDTO("test", true, -1000L);
        assertEquals(-1000L, dto.getTimestamp());
    }
}
