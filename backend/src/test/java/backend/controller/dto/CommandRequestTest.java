package backend.controller.dto;

import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandRequestTest {

    @Test
    void testDefaultConstructor() {
        CommandRequest request = new CommandRequest();
        assertNull(request.getCommand());
        assertTrue(request.isAddToHistory());
    }

    @Test
    void testConstructorWithCommand() {
        CommandRequest request = new CommandRequest("ls -l");
        assertEquals("ls -l", request.getCommand());
        assertTrue(request.isAddToHistory());
    }

    @Test
    void testConstructorWithCommandAndAddToHistory() {
        CommandRequest request = new CommandRequest("pwd", false);
        assertEquals("pwd", request.getCommand());
        assertFalse(request.isAddToHistory());
    }

    @Test
    void testConstructorWithAddToHistoryTrue() {
        CommandRequest request = new CommandRequest("mkdir test", true);
        assertEquals("mkdir test", request.getCommand());
        assertTrue(request.isAddToHistory());
    }

    @Test
    void testSetCommand() {
        CommandRequest request = new CommandRequest();
        request.setCommand("cd /home");
        assertEquals("cd /home", request.getCommand());
    }

    @Test
    void testSetAddToHistory() {
        CommandRequest request = new CommandRequest();
        request.setAddToHistory(false);
        assertFalse(request.isAddToHistory());
        
        request.setAddToHistory(true);
        assertTrue(request.isAddToHistory());
    }

    @Test
    void testNullCommand() {
        CommandRequest request = new CommandRequest(null);
        assertNull(request.getCommand());
        assertTrue(request.isAddToHistory());
    }

    @Test
    void testEmptyCommand() {
        CommandRequest request = new CommandRequest("");
        assertEquals("", request.getCommand());
    }

    @Test
    void testComplexCommand() {
        CommandRequest request = new CommandRequest("ln -s /target /link", false);
        assertEquals("ln -s /target /link", request.getCommand());
        assertFalse(request.isAddToHistory());
    }

    @Test
    void testSetCommandNull() {
        CommandRequest request = new CommandRequest("ls");
        request.setCommand(null);
        assertNull(request.getCommand());
    }

    @Test
    void testMultipleModifications() {
        CommandRequest request = new CommandRequest();
        
        request.setCommand("first");
        assertEquals("first", request.getCommand());
        
        request.setCommand("second");
        assertEquals("second", request.getCommand());
        
        request.setAddToHistory(false);
        assertFalse(request.isAddToHistory());
        
        request.setAddToHistory(true);
        assertTrue(request.isAddToHistory());
    }
}
