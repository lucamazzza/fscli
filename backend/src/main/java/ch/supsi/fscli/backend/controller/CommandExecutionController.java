package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;

import java.util.List;

/**
 * Controller specifically for command execution operations.
 * Part of the Facade pattern implementation.
 */
public class CommandExecutionController {
    private final CommandController commandController;
    
    public CommandExecutionController(FileSystem fileSystem) {
        this.commandController = new CommandController(fileSystem);
    }
    
    /**
     * Execute a command and return the response.
     * 
     * @param request Command request with command string
     * @return Command response DTO
     */
    public CommandResponseDTO executeCommand(CommandRequest request) {
        if (request == null || request.getCommand() == null) {
            return CommandResponseDTO.error("Invalid request");
        }
        
        CommandResponse response = commandController.executeCommand(request.getCommand());
        return convertToDTO(response);
    }
    
    /**
     * Execute a command string directly.
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
        return commandController.getAvailableCommands();
    }
    
    /**
     * Get help text for a specific command.
     * 
     * @param commandName Name of the command
     * @return Help text
     */
    public String getCommandHelp(String commandName) {
        return commandController.getCommandHelp(commandName);
    }
    
    /**
     * Get help for all commands.
     * 
     * @return List of help texts
     */
    public List<String> getAllCommandsHelp() {
        List<String> helpTexts = new java.util.ArrayList<>();
        for (String cmd : getAvailableCommands()) {
            helpTexts.add(getCommandHelp(cmd));
        }
        return helpTexts;
    }
    
    private CommandResponseDTO convertToDTO(CommandResponse response) {
        return new CommandResponseDTO(
                response.isSuccess(),
                response.getOutput(),
                response.getErrorMessage()
        );
    }
}
