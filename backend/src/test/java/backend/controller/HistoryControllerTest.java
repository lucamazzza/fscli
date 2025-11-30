package backend.controller;

import ch.supsi.fscli.backend.controller.HistoryController;
import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.service.FileSystemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for HistoryController.
 * Tests the facade's history management subsystem using integration testing.
 */
class HistoryControllerTest {
    
    private HistoryController controller;
    private FileSystemService service;
    
    @BeforeEach
    void setUp() {
        service = new FileSystemService();
        service.setFileSystem(new InMemoryFileSystem());
        controller = new HistoryController(service);
    }
    
    @Test
    void testGetHistory_Empty() {
        // Act
        List<CommandHistoryDTO> result = controller.getHistory();
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testGetHistory_AfterCommands() {
        // Arrange - execute some commands
        service.executeCommand("mkdir test1");
        service.executeCommand("mkdir test2");
        service.executeCommand("invalid command");
        
        // Act
        List<CommandHistoryDTO> result = controller.getHistory();
        
        // Assert
        assertEquals(3, result.size());
        assertEquals("mkdir test1", result.get(0).getCommand());
        assertTrue(result.get(0).isSuccessful());
        assertEquals("mkdir test2", result.get(1).getCommand());
        assertFalse(result.get(2).isSuccessful()); // invalid command failed
    }
    
    @Test
    void testGetLastCommands() {
        // Arrange
        service.executeCommand("cmd1");
        service.executeCommand("cmd2");
        service.executeCommand("cmd3");
        service.executeCommand("cmd4");
        
        // Act
        List<CommandHistoryDTO> result = controller.getLastCommands(2);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals("cmd3", result.get(0).getCommand());
        assertEquals("cmd4", result.get(1).getCommand());
    }
    
    @Test
    void testGetLastCommands_RequestMoreThanAvailable() {
        // Arrange
        service.executeCommand("pwd");
        
        // Act
        List<CommandHistoryDTO> result = controller.getLastCommands(10);
        
        // Assert
        assertEquals(1, result.size());
    }
    
    @Test
    void testSearchHistory_Found() {
        // Arrange
        service.executeCommand("ls -l");
        service.executeCommand("mkdir test");
        service.executeCommand("ls -a");
        
        // Act
        List<CommandHistoryDTO> result = controller.searchHistory("ls");
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(0).getCommand().contains("ls"));
        assertTrue(result.get(1).getCommand().contains("ls"));
    }
    
    @Test
    void testSearchHistory_NotFound() {
        // Arrange
        service.executeCommand("pwd");
        
        // Act
        List<CommandHistoryDTO> result = controller.searchHistory("nonexistent");
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
    @Test
    void testClearHistory() {
        // Arrange
        service.executeCommand("cmd1");
        service.executeCommand("cmd2");
        
        // Act
        controller.clearHistory();
        
        // Assert
        assertTrue(controller.getHistory().isEmpty());
    }
    
    @Test
    void testGetHistoryCommands() {
        // Arrange
        service.executeCommand("ls");
        service.executeCommand("cd /home");
        service.executeCommand("pwd");
        
        // Act
        List<String> result = controller.getHistoryCommands();
        
        // Assert
        assertEquals(3, result.size());
        assertEquals("ls", result.get(0));
        assertEquals("cd /home", result.get(1));
        assertEquals("pwd", result.get(2));
    }
    
    @Test
    void testHistoryDTOConversion_PreservesData() {
        // Arrange
        long beforeCommand = System.currentTimeMillis();
        service.executeCommand("pwd");
        long afterCommand = System.currentTimeMillis();
        
        // Act
        List<CommandHistoryDTO> result = controller.getHistory();
        
        // Assert
        assertEquals(1, result.size());
        CommandHistoryDTO dto = result.get(0);
        assertEquals("pwd", dto.getCommand());
        assertTrue(dto.isSuccessful());
        assertTrue(dto.getTimestamp() >= beforeCommand);
        assertTrue(dto.getTimestamp() <= afterCommand);
    }
    
    @Test
    void testHistoryDTOConversion_FailedCommand() {
        // Arrange
        service.executeCommand("invalid command xyz");
        
        // Act
        List<CommandHistoryDTO> result = controller.getHistory();
        
        // Assert
        assertEquals(1, result.size());
        assertFalse(result.get(0).isSuccessful());
    }
}
