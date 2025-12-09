package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.service.FileSystemService;
import com.google.inject.Inject;

import java.util.List;

/**
 * Facade Pattern - Unified API for frontend-backend communication.
 * This facade delegates to specialized controllers for different subsystems:
 * - CommandExecutionController: handles command execution
 * - HistoryController: manages command history
 * 
 * <p>All controllers use the same Service layer instance.</p>
 * 
 * <p>This class provides a clean, unified interface that can be used by:</p>
 * <ul>
 *   <li>REST controllers (Spring, JAX-RS, etc.)</li>
 *   <li>WebSocket handlers</li>
 *   <li>JavaFX controllers</li>
 *   <li>Direct method invocation</li>
 * </ul>
 * 
 * <p>All methods use DTOs (Data Transfer Objects) for easy serialization.</p>
 * 
 * @see CommandExecutionController
 * @see HistoryController
 * @see FileSystemService
 */
public class FileSystemController {
    /** The service layer that contains business logic and command history */
    private final FileSystemService service;
    
    /** Controller specialized in command execution operations */
    private final CommandExecutionController commandExecutionController;
    
    /** Controller specialized in history management operations */
    private final HistoryController historyController;
    
    /**
     * Constructs a new FileSystemController facade with injected dependencies.
     * 
     * @param service The file system service
     * @param commandExecutionController Controller for command execution
     * @param historyController Controller for history management
     * @param fileSystem The filesystem implementation (not used directly, service manages it)
     */
    @Inject
    public FileSystemController(
            FileSystemService service,
            CommandExecutionController commandExecutionController,
            HistoryController historyController,
            FileSystem fileSystem) {
        this.service = service;
        // Only set filesystem if service doesn't have one yet
        if (service.getFileSystem() == null) {
            this.service.setFileSystem(fileSystem);
        }
        this.commandExecutionController = commandExecutionController;
        this.historyController = historyController;
    }
    
    /**
     * Execute a command and return the response.
     * 
     * @param request Command request with command string and options
     * @return Command response DTO
     */
    public CommandResponseDTO executeCommand(CommandRequest request) {
        return commandExecutionController.executeCommand(request);
    }
    
    /**
     * Execute a command string directly (added to history).
     * 
     * @param commandString The command to execute
     * @return Command response DTO
     */
    public CommandResponseDTO executeCommand(String commandString) {
        return commandExecutionController.executeCommand(commandString);
    }
    
    /**
     * Get list of all available commands.
     * 
     * @return Array of command names
     */
    public String[] getAvailableCommands() {
        return commandExecutionController.getAvailableCommands();
    }
    
    /**
     * Get help text for a specific command.
     * 
     * @param commandName Name of the command
     * @return Help text
     */
    public String getCommandHelp(String commandName) {
        return commandExecutionController.getCommandHelp(commandName);
    }
    
    /**
     * Get help for all commands.
     * 
     * @return List of help texts
     */
    public List<String> getAllCommandsHelp() {
        return commandExecutionController.getAllCommandsHelp();
    }
    
    /**
     * Get command history.
     * 
     * @return List of command history DTOs
     */
    public List<CommandHistoryDTO> getHistory() {
        return historyController.getHistory();
    }
    
    /**
     * Get last N commands from history.
     * 
     * @param count Number of commands to retrieve
     * @return List of command history DTOs
     */
    public List<CommandHistoryDTO> getLastCommands(int count) {
        return historyController.getLastCommands(count);
    }
    
    /**
     * Search command history.
     * 
     * @param pattern Search pattern
     * @return List of matching command history DTOs
     */
    public List<CommandHistoryDTO> searchHistory(String pattern) {
        return historyController.searchHistory(pattern);
    }
    
    /**
     * Clear command history.
     */
    public void clearHistory() {
        historyController.clearHistory();
    }
    
    /**
     * Get command history as simple string list.
     * 
     * @return List of command strings
     */
    public List<String> getHistoryCommands() {
        return historyController.getHistoryCommands();
    }

    /**
     * Updates the filesystem instance used by this controller.
     * This will propagate the change to the underlying service layer.
     * 
     * @param fs The new filesystem instance to use
     */
    public void setFileSystem(FileSystem fs) {
        this.service.setFileSystem(fs);
    }
}
