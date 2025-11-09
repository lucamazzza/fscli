package backend.controller;

import ch.supsi.fscli.backend.controller.FileSystemController;
import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemControllerTest {

    private FileSystemController controller;
    private FileSystem fileSystem;

    @BeforeEach
    void setUp() {
        fileSystem = new InMemoryFileSystem();
        controller = new FileSystemController(fileSystem);
    }

    @Test
    void testExecuteCommandWithNull() {
        CommandResponseDTO response = controller.executeCommand((CommandRequest) null);
        
        assertFalse(response.isSuccess());
        assertEquals("Invalid request", response.getErrorMessage());
    }

    @Test
    void testExecuteCommandWithNullCommand() {
        CommandRequest request = new CommandRequest();
        CommandResponseDTO response = controller.executeCommand(request);
        
        assertFalse(response.isSuccess());
        assertEquals("Invalid request", response.getErrorMessage());
    }

    @Test
    void testExecuteCommandString() {
        CommandResponseDTO response = controller.executeCommand("test");
        
        assertNotNull(response);
    }

    @Test
    void testExecuteCommandWithAddToHistory() {
        CommandRequest request = new CommandRequest("test", true);
        controller.executeCommand(request);
        
        List<CommandHistoryDTO> history = controller.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void testExecuteCommandWithoutAddToHistory() {
        CommandRequest request = new CommandRequest("test", false);
        controller.executeCommand(request);
        
        List<CommandHistoryDTO> history = controller.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    void testGetAvailableCommands() {
        String[] commands = controller.getAvailableCommands();
        
        assertNotNull(commands);
    }

    @Test
    void testGetCommandHelp() {
        String help = controller.getCommandHelp("test");
        
        assertNotNull(help);
    }

    @Test
    void testGetAllCommandsHelp() {
        List<String> helpTexts = controller.getAllCommandsHelp();
        
        assertNotNull(helpTexts);
    }

    @Test
    void testGetHistoryInitiallyEmpty() {
        List<CommandHistoryDTO> history = controller.getHistory();
        
        assertNotNull(history);
        assertTrue(history.isEmpty());
    }

    @Test
    void testGetHistoryAfterCommand() {
        controller.executeCommand("test");
        
        List<CommandHistoryDTO> history = controller.getHistory();
        assertEquals(1, history.size());
        assertEquals("test", history.get(0).getCommand());
    }

    @Test
    void testGetLastCommands() {
        controller.executeCommand("cmd1");
        controller.executeCommand("cmd2");
        controller.executeCommand("cmd3");
        
        List<CommandHistoryDTO> last2 = controller.getLastCommands(2);
        
        assertEquals(2, last2.size());
        assertEquals("cmd2", last2.get(0).getCommand());
        assertEquals("cmd3", last2.get(1).getCommand());
    }

    @Test
    void testGetLastCommandsMoreThanAvailable() {
        controller.executeCommand("cmd1");
        
        List<CommandHistoryDTO> last10 = controller.getLastCommands(10);
        
        assertEquals(1, last10.size());
    }

    @Test
    void testSearchHistory() {
        controller.executeCommand("mkdir test");
        controller.executeCommand("cd test");
        controller.executeCommand("ls");
        
        List<CommandHistoryDTO> results = controller.searchHistory("test");
        
        assertEquals(2, results.size());
    }

    @Test
    void testSearchHistoryCaseInsensitive() {
        controller.executeCommand("MKDIR TEST");
        
        List<CommandHistoryDTO> results = controller.searchHistory("mkdir");
        
        assertEquals(1, results.size());
    }

    @Test
    void testSearchHistoryNoMatches() {
        controller.executeCommand("ls");
        
        List<CommandHistoryDTO> results = controller.searchHistory("nonexistent");
        
        assertTrue(results.isEmpty());
    }

    @Test
    void testClearHistory() {
        controller.executeCommand("cmd1");
        controller.executeCommand("cmd2");
        
        controller.clearHistory();
        
        List<CommandHistoryDTO> history = controller.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void testGetHistoryCommands() {
        controller.executeCommand("cmd1");
        controller.executeCommand("cmd2");
        
        List<String> commands = controller.getHistoryCommands();
        
        assertEquals(2, commands.size());
        assertEquals("cmd1", commands.get(0));
        assertEquals("cmd2", commands.get(1));
    }

    @Test
    void testMultipleExecutions() {
        for (int i = 0; i < 5; i++) {
            controller.executeCommand("cmd" + i);
        }
        
        assertEquals(5, controller.getHistory().size());
    }

    @Test
    void testHistoryDTOConversion() {
        controller.executeCommand("test command");
        
        List<CommandHistoryDTO> history = controller.getHistory();
        CommandHistoryDTO dto = history.get(0);
        
        assertEquals("test command", dto.getCommand());
        assertTrue(dto.getTimestamp() > 0);
    }

    @Test
    void testExecuteCommandReturnsDTO() {
        CommandResponseDTO response = controller.executeCommand("test");
        
        assertNotNull(response);
        assertNotNull(response.getOutput());
        assertTrue(response.getTimestamp() > 0);
    }
}
