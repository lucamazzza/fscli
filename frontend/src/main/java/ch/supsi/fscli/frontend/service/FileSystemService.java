package ch.supsi.fscli.frontend.service;

import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.controller.dto.CommandRequest;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import lombok.Getter;

import java.util.List;

public class FileSystemService {
    private ch.supsi.fscli.backend.controller.FileSystemController backendController;

    @Getter
    private ch.supsi.fscli.backend.core.FileSystem fileSystem;
    
    public FileSystemService() {
        this.fileSystem = null;
        this.backendController = null;
    }
    
    public void createNewFileSystem() {
        this.fileSystem = new InMemoryFileSystem();
        this.backendController = new ch.supsi.fscli.backend.controller.FileSystemController(fileSystem);
    }
    
    public boolean isFileSystemLoaded() {
        return fileSystem != null && backendController != null;
    }
    
    public CommandResponseDTO executeCommand(String commandString) {
        if (!isFileSystemLoaded()) {
            return CommandResponseDTO.error("No filesystem loaded. Create or open a filesystem first.");
        }
        
        if (commandString == null || commandString.trim().isEmpty()) {
            return CommandResponseDTO.error("Empty command");
        }
        
        CommandRequest request = new CommandRequest(commandString.trim());
        return backendController.executeCommand(request);
    }
    
    public CommandResponseDTO executeCommandSilent(String commandString) {
        if (!isFileSystemLoaded()) {
            return CommandResponseDTO.error("No filesystem loaded. Create or open a filesystem first.");
        }
        
        CommandRequest request = new CommandRequest(commandString.trim(), false);
        return backendController.executeCommand(request);
    }
    
    public String[] getAvailableCommands() {
        if (!isFileSystemLoaded()) {
            return new String[0];
        }
        return backendController.getAvailableCommands();
    }
    
    public String getCommandHelp(String commandName) {
        if (!isFileSystemLoaded()) {
            return "No filesystem loaded";
        }
        return backendController.getCommandHelp(commandName);
    }
    
    public List<String> getAllCommandsHelp() {
        if (!isFileSystemLoaded()) {
            return List.of("No filesystem loaded");
        }
        return backendController.getAllCommandsHelp();
    }
    
    public List<CommandHistoryDTO> getHistory() {
        if (!isFileSystemLoaded()) {
            return List.of();
        }
        return backendController.getHistory();
    }
    
    public List<CommandHistoryDTO> getLastCommands(int count) {
        if (!isFileSystemLoaded()) {
            return List.of();
        }
        return backendController.getLastCommands(count);
    }
    
    public List<CommandHistoryDTO> searchHistory(String pattern) {
        if (!isFileSystemLoaded()) {
            return List.of();
        }
        return backendController.searchHistory(pattern);
    }
    
    public void clearHistory() {
        if (isFileSystemLoaded()) {
            backendController.clearHistory();
        }
    }
    
    public List<String> getHistoryCommands() {
        if (!isFileSystemLoaded()) {
            return List.of();
        }
        return backendController.getHistoryCommands();
    }
    
    public String getCurrentDirectory() {
        if (!isFileSystemLoaded()) {
            return "/";
        }
        return fileSystem.pwd();
    }

    public void setFileSystem(ch.supsi.fscli.backend.core.FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.backendController = new ch.supsi.fscli.backend.controller.FileSystemController(fileSystem);
    }
}
