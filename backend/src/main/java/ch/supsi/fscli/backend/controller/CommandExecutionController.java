package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.service.FileSystemService;

import java.util.List;

/**
 * Controller specialized in command execution operations.
 * Part of the Facade pattern implementation.
 * 
 * <p>This controller is responsible for:</p>
 * <ul>
 *   <li>Executing filesystem commands</li>
 *   <li>Retrieving available commands</li>
 *   <li>Providing command help text</li>
 *   <li>Converting between internal and DTO response formats</li>
 * </ul>
 * 
 * <p>Delegates all business logic to {@link FileSystemService}.</p>
 * 
 * @see FileSystemController
 * @see FileSystemService
 */
public class CommandExecutionController {
    /** The service layer that handles business logic and command execution */
    private final FileSystemService service;
    
    /**
     * Constructs a new CommandExecutionController.
     * 
     * @param service The service layer to delegate operations to
     */
    public CommandExecutionController(FileSystemService service) {
        this.service = service;
    }
    
    /**
     * Executes a command with advanced options.
     * 
     * <p>This method supports:</p>
     * <ul>
     *   <li>Command execution with history tracking</li>
     *   <li>Silent execution (no history)</li>
     *   <li>Custom command options via {@link CommandRequest}</li>
     * </ul>
     * 
     * @param request Command request containing command string and options
     * @return Command response DTO with execution results
     */
    public CommandResponseDTO executeCommand(CommandRequest request) {
        if (request == null || request.getCommand() == null) {
            return CommandResponseDTO.error("Invalid request");
        }
        
        CommandResponse response = request.isAddToHistory()
            ? service.executeCommand(request.getCommand())
            : service.executeCommandSilent(request.getCommand());
        
        return convertToDTO(response);
    }
    
    /**
     * Executes a command string directly (with history tracking).
     * This is a convenience method for simple command execution.
     * 
     * @param commandString The command to execute (e.g., "ls -l /home")
     * @return Command response DTO with execution results
     */
    public CommandResponseDTO executeCommand(String commandString) {
        CommandResponse response = service.executeCommand(commandString);
        return convertToDTO(response);
    }
    
    /**
     * Retrieves all available commands in the system.
     * 
     * @return Array of command names (e.g., ["ls", "cd", "mkdir"])
     */
    public String[] getAvailableCommands() {
        return service.getAvailableCommands();
    }
    
    /**
     * Retrieves help text for a specific command.
     * 
     * @param commandName Name of the command (e.g., "ls")
     * @return Help text including usage and description, or error message if command not found
     */
    public String getCommandHelp(String commandName) {
        return service.getCommandHelp(commandName);
    }
    
    /**
     * Retrieves help text for all available commands.
     * 
     * @return List of help texts, one for each command
     */
    public List<String> getAllCommandsHelp() {
        return service.getAllCommandsHelp();
    }
    
    /**
     * Converts internal CommandResponse to DTO format for API consumption.
     * 
     * @param response Internal command response
     * @return DTO formatted response with timestamp
     */
    private CommandResponseDTO convertToDTO(CommandResponse response) {
        return new CommandResponseDTO(
                response.isSuccess(),
                response.getOutput(),
                response.getErrorMessage()
        );
    }
}
