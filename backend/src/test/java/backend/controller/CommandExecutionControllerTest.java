package backend.controller;

import backend.util.TestInjectorFactory;
import ch.supsi.fscli.backend.controller.CommandExecutionController;
import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.service.FileSystemService;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CommandExecutionController.
 * Tests the facade's command execution subsystem using integration testing.
 */
class CommandExecutionControllerTest {
    
    private CommandExecutionController controller;
    private FileSystemService service;
    
    @BeforeEach
    void setUp() {
        Injector injector = TestInjectorFactory.createTestInjector();
        service = injector.getInstance(FileSystemService.class);
        FileSystem fileSystem = injector.getInstance(FileSystem.class);
        service.setFileSystem(fileSystem);
        controller = new CommandExecutionController(service);
    }
    
    @Test
    void testExecuteCommandWithRequest_Success() {
        // Arrange
        CommandRequest request = new CommandRequest("mkdir test");
        request.setAddToHistory(true);
        
        // Act
        CommandResponseDTO result = controller.executeCommand(request);
        
        // Assert
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testExecuteCommandWithRequest_Silent() {
        // Arrange
        CommandRequest request = new CommandRequest("pwd");
        request.setAddToHistory(false);
        
        // Act
        CommandResponseDTO result = controller.executeCommand(request);
        
        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getOutput().size());
        assertEquals("/", result.getOutput().get(0));
        
        // Verify it's not in history
        assertTrue(service.getHistoryCommands().isEmpty());
    }
    
    @Test
    void testExecuteCommandWithRequest_NullRequest() {
        // Act
        CommandResponseDTO result = controller.executeCommand((CommandRequest) null);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Invalid request", result.getErrorMessage());
    }
    
    @Test
    void testExecuteCommandWithRequest_NullCommand() {
        // Arrange
        CommandRequest request = new CommandRequest(null);
        
        // Act
        CommandResponseDTO result = controller.executeCommand(request);
        
        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Invalid request", result.getErrorMessage());
    }
    
    @Test
    void testExecuteCommandString_Success() {
        // Act
        CommandResponseDTO result = controller.executeCommand("mkdir mydir");
        
        // Assert
        assertTrue(result.isSuccess());
        
        // Verify command was added to history
        assertEquals(1, service.getHistoryCommands().size());
    }
    
    @Test
    void testExecuteCommandString_Error() {
        // Act
        CommandResponseDTO result = controller.executeCommand("cd nonexistent");
        
        // Assert
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testGetAvailableCommands() {
        // Act
        String[] commands = controller.getAvailableCommands();
        
        // Assert
        assertNotNull(commands);
        assertTrue(commands.length >= 10); // At least 10 commands
        assertTrue(List.of(commands).contains("ls"));
        assertTrue(List.of(commands).contains("cd"));
        assertTrue(List.of(commands).contains("mkdir"));
    }
    
    @Test
    void testGetCommandHelp() {
        // Act
        String help = controller.getCommandHelp("ls");
        
        // Assert
        assertNotNull(help);
        assertTrue(help.contains("Usage") || help.contains("ls"));
    }
    
    @Test
    void testGetCommandHelp_UnknownCommand() {
        // Act
        String help = controller.getCommandHelp("invalid");
        
        // Assert
        assertNotNull(help);
        assertTrue(help.contains("command not found") || help.contains("invalid"));
    }
    
    @Test
    void testGetAllCommandsHelp() {
        // Act
        List<String> helpTexts = controller.getAllCommandsHelp();
        
        // Assert
        assertNotNull(helpTexts);
        assertTrue(helpTexts.size() >= 10);
    }
    
    @Test
    void testResponseDTOContainsTimestamp() {
        // Act
        CommandResponseDTO result = controller.executeCommand("pwd");
        
        // Assert
        assertTrue(result.getTimestamp() > 0);
        assertTrue(result.getTimestamp() <= System.currentTimeMillis());
    }
}
