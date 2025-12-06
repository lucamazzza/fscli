package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.FileSystemController;
import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.serde.FilesystemFileManager;
import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;
import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.EventPublisher;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.util.AppError;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Frontend model for FileSystem.
 * Delegates to backend controller (API boundary).
 * Flow: View → Frontend Controller → Frontend Model → Backend Controller → Service
 */
public final class FileSystemModel {
    private FileSystemController backendController;
    @Setter
    private FileSystemPersistenceController backendPersistenceController;

    @Setter
    private EventPublisher<FileSystemEvent> fileSystemEventManager;
    @Setter
    private EventPublisher<CommandLineEvent> commandLineEventManager;

    private File file;

    private static FileSystemModel instance;

    public static FileSystemModel getInstance() {
        if (instance == null) {
            instance = new FileSystemModel();
        }
        return instance;
    }

    private FileSystemModel() {
        file = null;
        backendController = null;
    }

    public void save() {
        if (!isPersistedFileValid()) {
            fileSystemEventManager.notify(new FileSystemEvent(AppError.SAVE_FAILED_FILE_NOT_FOUND));
            return;
        }
        FilesystemFileManager fileManager = new FilesystemFileManager(file.toPath());
        try {
            fileManager.save(backendPersistenceController.getFileSystem().getRoot());
        } catch (IOException e) {
            fileSystemEventManager.notify(new FileSystemEvent(AppError.SAVE_FAILED_GENERIC));
        }
        fileSystemEventManager.notify(new FileSystemEvent(AppError.SAVE_SUCCESS));
    }

    public void saveAs(File file) {
        if (file == null) {
            fileSystemEventManager.notify(new FileSystemEvent(AppError.SAVE_AS_FAILED_INVALID_PATH));
            return;
        }
        try {
            FileSystem backendFS = backendPersistenceController.getFileSystem();
            DirectoryNode root = backendFS.getRoot();
            FilesystemFileManager fileManager = new FilesystemFileManager(file.toPath());
            fileManager.save(root);
            this.file = file;
            fileSystemEventManager.notify(new FileSystemEvent(AppError.SAVE_AS_SUCCESS));
        } catch (IOException e) {
            fileSystemEventManager.notify(new FileSystemEvent(AppError.SAVE_AS_FAILED_INVALID_PATH));
        }
    }

    public void load(File file) {
        if (file == null) return;
        if (!file.exists()) return;
        if (backendPersistenceController.loadFileSystem(file.toPath())) {
            FileSystem fsBackend = backendPersistenceController.getFileSystem();
            this.backendController = BackendInjector.getInjector().getInstance(FileSystemController.class);
            this.file = file;
            fileSystemEventManager.notify(new FileSystemEvent(AppError.LOAD_SUCCESS));
            return;
        }
        fileSystemEventManager.notify(new FileSystemEvent(AppError.LOAD_FAILED_READ));
    }

    public void createFileSystem(boolean force) {
        if (fileSystemEventManager == null) return;
        if (backendPersistenceController == null) return;
        if (file == null && isFileSystemReady() && !force) {
            fileSystemEventManager.notify(new FileSystemEvent(AppError.NEW_FAILED_UNSAVED_WORK));
            return;
        }
        backendPersistenceController.createNewFileSystem();
        FileSystem fileSystem = backendPersistenceController.getFileSystem();
        if (fileSystem == null) {
            fileSystemEventManager.notify(new FileSystemEvent(AppError.NEW_FAILED_BS_MISSING));
            return;
        }
        this.backendController = BackendInjector.getInjector().getInstance(FileSystemController.class);
        this.file = null;
        fileSystemEventManager.notify(new FileSystemEvent(AppError.NEW_SUCCESS));
    }

    public CommandResponseDTO executeCommand(String command) {
        if (fileSystemEventManager == null) return CommandResponseDTO.error(BackendMessageProvider.get("filesystem.notLoaded"));
        if (!isFileSystemReady()) {
            // Make this event better for logging in the future
            commandLineEventManager.notify(new CommandLineEvent(AppError.CMD_EXECUTION_FAILED_FS_MISSING, null, null, null));
            return CommandResponseDTO.error(FrontendMessageProvider.get("fileEvent.FSNotReady"));
        }
        String currentDir = getCurrentDirectory();
        CommandResponseDTO response = backendController.executeCommand(command);
        if (response == null) {
            commandLineEventManager.notify(new CommandLineEvent(AppError.CMD_EXECUTION_FAILED_BAD_RESPONSE, null, null, null));
            return CommandResponseDTO.error(FrontendMessageProvider.get("fileEvent.FSNotReady"));
        }

        commandLineEventManager.notify(new CommandLineEvent(AppError.CMD_EXECUTION_SUCCESS, currentDir, response.getOutputAsString(), response.getErrorMessage()));

        return CommandResponseDTO.success(response.getOutputAsString());
    }

    public String getCurrentDirectory() {
        return backendPersistenceController.getCurrentDirectory();
    }

    public boolean isFileSystemReady() {
        return backendController != null && backendPersistenceController.isFileSystemLoaded();
    }

    private boolean isPersistedFileValid() {
        return file != null && file.exists();
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

