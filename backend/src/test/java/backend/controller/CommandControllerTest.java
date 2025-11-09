package backend.controller;

import ch.supsi.fscli.backend.controller.CommandController;
import ch.supsi.fscli.backend.controller.CommandResponse;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandControllerTest {

    private CommandController controller;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        fileSystem = new InMemoryFileSystem();
        controller = new CommandController(fileSystem);
    }

    @Test
    void testExecuteCommandNull() {
        CommandResponse response = controller.executeCommand(null);
        
        assertFalse(response.isSuccess());
        assertEquals("Empty command", response.getErrorMessage());
    }

    @Test
    void testExecuteCommandEmpty() {
        CommandResponse response = controller.executeCommand("");
        
        assertFalse(response.isSuccess());
        assertEquals("Empty command", response.getErrorMessage());
    }

    @Test
    void testExecuteCommandWhitespace() {
        CommandResponse response = controller.executeCommand("   \t  ");
        
        assertFalse(response.isSuccess());
        assertEquals("Empty command", response.getErrorMessage());
    }

    @Test
    void testExecuteUnknownCommand() {
        CommandResponse response = controller.executeCommand("nonexistent");
        
        assertFalse(response.isSuccess());
        assertTrue(response.getErrorMessage().contains("Unknown command"));
    }

    @Test
    void testGetAvailableCommandsInitially() {
        String[] commands = controller.getAvailableCommands();
        
        assertNotNull(commands);
    }

    @Test
    void testGetCommandHelpForNonexistentCommand() {
        String help = controller.getCommandHelp("nonexistent");
        
        assertTrue(help.contains("command not found"));
    }

    @Test
    void testExecuteInvalidSyntax() {
        CommandResponse response = controller.executeCommand("123invalid");
        
        assertFalse(response.isSuccess());
    }

    @Test
    void testMultipleCommandExecutions() {
        CommandResponse response1 = controller.executeCommand("unknown1");
        CommandResponse response2 = controller.executeCommand("unknown2");
        
        assertFalse(response1.isSuccess());
        assertFalse(response2.isSuccess());
    }

    @Test
    void testExecuteCommandTrimsWhitespace() {
        CommandResponse response = controller.executeCommand("  nonexistent  ");
        
        assertFalse(response.isSuccess());
        assertTrue(response.getErrorMessage().contains("Unknown command"));
    }

    @Test
    void testResponseStructure() {
        CommandResponse response = controller.executeCommand("test");
        
        assertNotNull(response);
        assertNotNull(response.getOutput());
        assertFalse(response.isSuccess());
    }
}
