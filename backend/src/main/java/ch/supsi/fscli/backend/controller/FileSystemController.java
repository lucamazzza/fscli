package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.service.CommandHistoryEntry;
import ch.supsi.fscli.backend.service.FileSystemService;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * API Facade for frontend-backend communication.
 * This class provides a clean, stateless interface that can be used by:
 * - REST controllers (Spring, JAX-RS, etc.)
 * - WebSocket handlers
 * - JavaFX controllers
 * - Direct method invocation
 * <p>
 * All methods use DTOs (Data Transfer Objects) for easy serialization.
 */
public class FileSystemController {
    private final FileSystemService service;
    
    public FileSystemController(FileSystem fileSystem) {
        this.service = new FileSystemService(fileSystem);
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

        CommandResponse response;
        if (request.isAddToHistory()) response = service.executeCommand(request.getCommand());
        else response = service.executeCommandSilent(request.getCommand());
        return convertToDTO(response);
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
        return service.getAvailableCommands();
    }
    
    /**
     * Get help text for a specific command.
     * 
     * @param commandName Name of the command
     * @return Help text
     */
    public String getCommandHelp(String commandName) {
        return service.getCommandHelp(commandName);
    }
    
    /**
     * Get help for all commands.
     * 
     * @return List of help texts
     */
    public List<String> getAllCommandsHelp() {
        return service.getAllCommandsHelp();
    }
    
    /**
     * Get command history.
     * 
     * @return List of command history DTOs
     */
    public List<CommandHistoryDTO> getHistory() {
        return service.getHistory().stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get last N commands from history.
     * 
     * @param count Number of commands to retrieve
     * @return List of command history DTOs
     */
    public List<CommandHistoryDTO> getLastCommands(int count) {
        return service.getLastCommands(count).stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Search command history.
     * 
     * @param pattern Search pattern
     * @return List of matching command history DTOs
     */
    public List<CommandHistoryDTO> searchHistory(String pattern) {
        return service.searchHistory(pattern).stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Clear command history.
     */
    public void clearHistory() {
        service.clearHistory();
    }
    
    /**
     * Get command history as simple string list.
     * 
     * @return List of command strings
     */
    public List<String> getHistoryCommands() {
        return service.getHistoryCommands();
    }
    
    private CommandResponseDTO convertToDTO(CommandResponse response) {
        return new CommandResponseDTO(
                response.isSuccess(),
                response.getOutput(),
                response.getErrorMessage()
        );
    }
    
    private CommandHistoryDTO convertHistoryToDTO(CommandHistoryEntry entry) {
        return new CommandHistoryDTO(
                entry.getCommand(),
                entry.isSuccessful(),
                entry.getTimestamp()
        );
    }
}
