package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.FileSystemController;
import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.frontend.event.EventError;
import ch.supsi.fscli.frontend.event.EventNotifier;
import ch.supsi.fscli.frontend.event.FileEvent;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Locale;

public class FileSystem {

    private boolean isFilePresent;

    @Getter
    private FileSystemController backendController;

    @Getter
    private final FileSystemPersistenceController backendPersistenceController;

    @Setter
    private EventNotifier<FileEvent> eventManager;

    private static FileSystem instance;

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static FileSystem getInstance() {
        if (instance == null) {
            instance = new FileSystem();
        }
        return instance;
    }

    private FileSystem() {
        this.isFilePresent = false;
        this.backendPersistenceController = new FileSystemPersistenceController();
        this.backendController = null;
    }

    public void createFileSystem() {
        backendPersistenceController.createNewFileSystem();
        ch.supsi.fscli.backend.core.FileSystem fsBackend = backendPersistenceController.getFileSystem();
        this.backendController = new FileSystemController(fsBackend);
        this.isFilePresent = true;
        if (eventManager != null) {
            eventManager.notify(new FileEvent(EventError.SUCCESS,
                    MESSAGES.getString("filesystem.created"), true));
        }
    }

    public boolean loadFileSystem(File file) {
        boolean success = backendPersistenceController.loadFileSystem(file.toPath());
        ch.supsi.fscli.backend.core.FileSystem fsBackend = backendPersistenceController.getFileSystem();
        this.backendController = new FileSystemController(fsBackend);
        this.isFilePresent = true;
        if (eventManager != null) {
            eventManager.notify(new FileEvent(EventError.SUCCESS,
                    MESSAGES.getString("filesystem.loaded"), true));
        }
        return success;
    }

    public void saveFileSystem(File file) throws IOException {
        backendPersistenceController.saveFileSystem(file.toPath());
    }

    public CommandResponseDTO executeCommand(String commandString) {
        if (!isFileSystemReady()) {
            if (eventManager != null) {
                eventManager.notify(new FileEvent(EventError.ERROR,
                        MESSAGES.getString("filesystem.notLoaded"), false));
            }
            return CommandResponseDTO.error(MESSAGES.getString("filesystem.notLoaded"));
        }
        return backendController.executeCommand(commandString);
    }

    public String getCurrentDirectory() {
        return backendPersistenceController.getCurrentDirectory();
    }

    public boolean isFileSystemReady() {
        return isFilePresent && backendPersistenceController.isFileSystemLoaded();
    }

    public String[] getAvailableCommands() {
        return backendController.getAvailableCommands();
    }

    public String getCommandHelp(String commandName) {
        return backendController.getCommandHelp(commandName);
    }

    public List<String> getAllCommandsHelp() {
        return backendController.getAllCommandsHelp();
    }

    public List<String> getCommandHistory() {
        return backendController.getHistoryCommands();
    }
}
