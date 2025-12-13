package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.FileSystemController;
import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.core.FileSystem;
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
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class FileSystemModel {
    private FileSystemController backendController;
    @Setter
    private FileSystemPersistenceController backendPersistenceController;
    @Setter
    private EventPublisher<FileSystemEvent> fileSystemEventManager;
    @Setter
    private EventPublisher<CommandLineEvent> commandLineEventManager;

    @Setter private File file;

    private final Supplier<FileSystemController> controllerFactory;

    private static FileSystemModel instance;

    public static FileSystemModel getInstance() {
        if (instance == null) {
            instance = new FileSystemModel(() ->
                    BackendInjector.getInjector().getInstance(FileSystemController.class)
            );
        }
        return instance;
    }

    FileSystemModel(Supplier<FileSystemController> controllerFactory) {
        this.controllerFactory = controllerFactory;
        this.file = null;
        this.backendController = null;
    }

    protected FilesystemFileManager createFileManager(Path path) {
        return new FilesystemFileManager(path);
    }

    public void save() {
        if (!isPersistedFileValid()) {
            notifyFsEvent(AppError.SAVE_FAILED_FILE_NOT_FOUND);
            return;
        }
        FilesystemFileManager fileManager = createFileManager(file.toPath());

        try {
            fileManager.save(backendPersistenceController.getFileSystem().getRoot());
            notifyFsEvent(AppError.SAVE_SUCCESS);
        } catch (IOException e) {
            notifyFsEvent(AppError.SAVE_FAILED_GENERIC);
        }
    }

    public void saveAs(File file) {
        if (file == null) {
            notifyFsEvent(AppError.SAVE_AS_FAILED_INVALID_PATH);
            return;
        }
        try {
            FileSystem backendFS = backendPersistenceController.getFileSystem();
            FilesystemFileManager fileManager = createFileManager(file.toPath());
            fileManager.save(backendFS.getRoot());
            this.file = file;
            notifyFsEvent(AppError.SAVE_AS_SUCCESS);
        } catch (IOException e) {
            notifyFsEvent(AppError.SAVE_AS_FAILED_INVALID_PATH);
        }
    }

    public void load(File file) {
        if (file == null || !file.exists()) return;
        if (backendPersistenceController.loadFileSystem(file.toPath())) {
            this.backendController = controllerFactory.get();
            this.file = file;
            notifyFsEvent(AppError.LOAD_SUCCESS);
            return;
        }
        notifyFsEvent(AppError.LOAD_FAILED_READ);
    }

    public void createFileSystem(boolean force) {
        if (fileSystemEventManager == null || backendPersistenceController == null) return;
        if (file == null && isFileSystemReady() && !force) {
            notifyFsEvent(AppError.NEW_FAILED_UNSAVED_WORK);
            return;
        }
        backendPersistenceController.createNewFileSystem();
        FileSystem fileSystem = backendPersistenceController.getFileSystem();
        if (fileSystem == null) {
            notifyFsEvent(AppError.NEW_FAILED_BS_MISSING);
            return;
        }
        this.backendController = controllerFactory.get();
        this.file = null;
        notifyFsEvent(AppError.NEW_SUCCESS);
    }

    public CommandResponseDTO executeCommand(String command) {
        if (fileSystemEventManager == null) return CommandResponseDTO.error(BackendMessageProvider.get("filesystem.notLoaded"));
        if (!isFileSystemReady()) {
            if (commandLineEventManager != null) {
                commandLineEventManager.notify(new CommandLineEvent(AppError.CMD_EXECUTION_FAILED_FS_MISSING, null, null, null));
            }
            return CommandResponseDTO.error(FrontendMessageProvider.get("fileEvent.FSNotReady"));
        }
        String currentDir = getCurrentDirectory();
        CommandResponseDTO response = backendController.executeCommand(command);
        if (response == null) {
            commandLineEventManager.notify(new CommandLineEvent(AppError.CMD_EXECUTION_FAILED_BAD_RESPONSE, null, null, null));
            return CommandResponseDTO.error(FrontendMessageProvider.get("fileEvent.FSNotReady"));
        }
        commandLineEventManager.notify(new CommandLineEvent(AppError.CMD_EXECUTION_SUCCESS, currentDir, response.getOutputAsString(), response.getErrorMessage()));
        return CommandResponseDTO.success(response.getOutput());
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

    private void notifyFsEvent(AppError error) {
        if (fileSystemEventManager != null) {
            fileSystemEventManager.notify(new FileSystemEvent(error));
        }
    }

    public List<String> getAllCommandsHelp() { return backendController.getAllCommandsHelp(); }

    public List<String> getCommandHistory() { return backendController.getHistoryCommands(); }

    public boolean hasUnsavedChanges() {
        return file == null && isFileSystemReady();
    }
}
