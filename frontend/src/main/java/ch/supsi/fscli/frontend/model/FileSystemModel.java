package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.FileSystemController;
import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.EventPublisher;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Frontend model for FileSystem.
 * Delegates to backend controller (API boundary).
 * Flow: View → Frontend Controller → Frontend Model → Backend Controller → Service
 */
public final class FileSystemModel {
    @Getter
    private FileSystemController backendController;
    @Getter @Setter
    private FileSystemPersistenceController backendPersistenceController;

    @Setter
    private EventPublisher<FileSystemEvent> fileSystemEventManager;
    @Setter
    private EventPublisher<CommandLineEvent> commandLineEventManager;

    private static FileSystemModel instance;

    public static FileSystemModel getInstance() {
        if (instance == null) {
            instance = new FileSystemModel();
        }
        return instance;
    }

    private FileSystemModel() {}

    public void createFileSystem() {
        if (fileSystemEventManager == null) return;
        if (backendPersistenceController == null) return;
        backendPersistenceController.createNewFileSystem();
        FileSystem fileSystem = backendPersistenceController.getFileSystem();
        if (fileSystem == null) {
            fileSystemEventManager.notify(new FileSystemEvent(false));
            return;
        }
        this.backendController = new FileSystemController(fileSystem);
        fileSystemEventManager.notify(new FileSystemEvent(true));
    }

    public void executeCommand(String command) {
        if (fileSystemEventManager == null) return;
        if (!isFileSystemReady()) return;
        CommandResponseDTO response = backendController.executeCommand(command);
        if (response == null) {
            commandLineEventManager.notify(new CommandLineEvent(false, null, null, null));
            return;
        }
        commandLineEventManager.notify(new CommandLineEvent(true, getCurrentDirectory(), response.getOutputAsString(), response.getErrorMessage()));
    }

    private String getCurrentDirectory() {
        return backendPersistenceController.getCurrentDirectory();
    }

    public boolean isFileSystemReady() {
        return backendController != null && backendPersistenceController.isFileSystemLoaded();
    }

    public String[] getAvailableCommands() {
        return backendController.getAvailableCommands();
    }

    public String getCommandHelp(String commandName) {
        return backendController.getCommandHelp(commandName);
    }

    public List<String> getAllCommandsHelp() { return backendController.getAllCommandsHelp(); }

    public List<String> getCommandHistory() {
        return backendController.getHistoryCommands();
    }
}

