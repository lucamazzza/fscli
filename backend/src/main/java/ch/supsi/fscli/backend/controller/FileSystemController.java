package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;

import java.util.List;

/**
 * Facade Pattern - Unified API for frontend-backend communication.
 * This facade delegates to specialized controllers for different subsystems:
 * - CommandExecutionController: handles command execution
 * - HistoryController: manages command history
 * 
 * This class provides a clean, unified interface that can be used by:
 * - REST controllers (Spring, JAX-RS, etc.)
 * - WebSocket handlers
 * - JavaFX controllers
 * - Direct method invocation
 * <p>
 * All methods use DTOs (Data Transfer Objects) for easy serialization.
 */
public class FileSystemController {
    private CommandExecutionController commandExecutionController;
    private HistoryController historyController;
    
    public FileSystemController(FileSystem fileSystem) {
        this.commandExecutionController = new CommandExecutionController(fileSystem);
        this.historyController = new HistoryController();
    }
    
    /**
     * Execute a command and return the response.
     * 
     * @param request Command request with command string and options
     * @return Command response DTO
     */
    public CommandResponseDTO executeCommand(CommandRequest request) {
        if (request == null || request.getCommand() == null)
            return CommandResponseDTO.error("Invalid request");

        CommandResponseDTO response = commandExecutionController.executeCommand(request);
        
        if (request.isAddToHistory()) {
            historyController.addToHistory(request.getCommand(), response.isSuccess());
        }
        
        return response;
    }
    
    /**
     * Execute a command string directly (added to history).
     * 
     * @param commandString The command to execute
     * @return Command response DTO
     */
    public CommandResponseDTO executeCommand(String commandString) {
        CommandRequest request = new CommandRequest(commandString);
        return executeCommand(request);
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

    public void setFileSystem(FileSystem fs) {
        this.commandExecutionController = new CommandExecutionController(fs);
    }

}
