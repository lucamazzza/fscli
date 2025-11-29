package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.FileSystemController;
import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.frontend.event.EventError;
import ch.supsi.fscli.frontend.event.EventNotifier;
import ch.supsi.fscli.frontend.event.FileEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FileSystem {

    private boolean isFilePresent;

    @Getter
    private FileSystemController backendController;

    @Getter
    private final FileSystemPersistenceController backendPersistenceController;

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
        this.backendPersistenceController = new FileSystemPersistenceController();
        // NON inizializzare qui
        this.backendController = null;
    }



    public void createFileSystem() {
        // crea il filesystem vero e proprio (backend)
        backendPersistenceController.createNewFileSystem();

        // recupera il FileSystem backend appena creato
        ch.supsi.fscli.backend.core.FileSystem fsBackend = backendPersistenceController.getFileSystem();

        // passa al controller backend
        this.backendController = new FileSystemController(fsBackend);

        this.isFilePresent = true;
        eventManager.notify(new FileEvent(EventError.SUCCESS, "FileSystem was created successfully", true));
    }


    public CommandResponseDTO executeCommand(String commandString) {
        if (!isFileSystemReady()) {
            if (eventManager != null) {
                eventManager.notify(new FileEvent(EventError.ERROR,
                        "No filesystem loaded. Please create or load a filesystem first.", false));
            }
            return CommandResponseDTO.error("No filesystem loaded");
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

    public List<String> getCommandHistory() {
        return backendController.getHistoryCommands();
    }
}

