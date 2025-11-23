package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.frontend.event.EventError;
import ch.supsi.fscli.frontend.event.EventNotifier;
import ch.supsi.fscli.frontend.event.FileEvent;
import ch.supsi.fscli.frontend.service.FileSystemService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FileSystem {
    private boolean isFilePresent;

    @Getter
    private final FileSystemService backendService;

    @Setter
    private EventNotifier<FileEvent> eventManager;

    private static FileSystem instance;

    public static FileSystem getInstance() {
        if (instance == null) {
            instance = new FileSystem();
        }
        return instance;
    }

    private FileSystem() {
        this.isFilePresent = false;
        this.backendService = new FileSystemService();
    }

    public void createFileSystem() {
        backendService.createNewFileSystem();
        this.isFilePresent = true;
        eventManager.notify(new FileEvent(EventError.SUCCESS, "FileSystem was created successfully", true));
    }
    
    public CommandResponseDTO executeCommand(String commandString) {
        return backendService.executeCommand(commandString);
    }
    
    public String getCurrentDirectory() {
        return backendService.getCurrentDirectory();
    }
    
    public boolean isFileSystemReady() {
        return isFilePresent && backendService.isFileSystemLoaded();
    }
    
    public String[] getAvailableCommands() {
        return backendService.getAvailableCommands();
    }
    
    public String getCommandHelp(String commandName) {
        return backendService.getCommandHelp(commandName);
    }
    
    public List<String> getCommandHistory() {
        return backendService.getHistoryCommands();
    }

}
