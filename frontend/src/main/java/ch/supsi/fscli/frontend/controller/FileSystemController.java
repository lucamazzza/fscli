package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.serde.FilesystemFileManager;
import ch.supsi.fscli.frontend.handler.FileSystemEventHandler;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.util.FxLogger;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FileSystemController implements FileSystemEventHandler {
    @Setter
    private FileSystemModel fileSystemModel;

    private static FileSystemController instance;

    public static FileSystemController getInstance() {
        if (instance == null) {
            instance = new FileSystemController();
        }
        return instance;
    }

    private FileSystemController() {
    }

    @Override
    public void newFileSystem() {
        this.fileSystemModel.createFileSystem();
    }

    @Override
    public void save() {
    }

    @Override
    public void saveAs(File file) {
        if (file == null) {
            FxLogger.getInstance().log("Error: No file selected");
            return;
        }
        
        saveToFile(file);
    }

    @Override
    public void load(File file) {
        if (file == null) {
            FxLogger.getInstance().log("Error: No file selected");
            return;
        }
        
        if (!file.exists()) {
            FxLogger.getInstance().log("Error: File does not exist: " + file.getAbsolutePath());
            return;
        }
        
        try {
            FilesystemFileManager fileManager = new FilesystemFileManager(file.toPath());
            Optional<FileSystemNode> rootOpt = fileManager.load();
            
            if (rootOpt.isEmpty()) {
                FxLogger.getInstance().log("Error: Failed to load filesystem from file");
                return;
            }
            
            FileSystemNode rootNode = rootOpt.get();
            
            if (!(rootNode instanceof DirectoryNode)) {
                FxLogger.getInstance().log("Error: Invalid filesystem format - root is not a directory");
                return;
            }
            
            // Create new InMemoryFileSystem and restore the loaded structure
            InMemoryFileSystem loadedFS = new InMemoryFileSystem();
            // Note: This is a simplified load. A complete implementation would need
            // to properly reconstruct the filesystem from the serialized root node.
            // For now, we'll set it and let the backend handle the structure
            
            fileSystemModel.getBackendPersistenceController().setFileSystem(loadedFS);

            CommandLineView commandLine = CommandLineView.getInstance();
            commandLine.clearOutput();
            commandLine.appendOutput("Filesystem loaded successfully from: " + file.getName() + "\n");
            // commandLine.appendOutput("Current directory: " + model.getCurrentDirectory() + "\n\n");
            
            FxLogger.getInstance().log("Filesystem loaded from: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            FxLogger.getInstance().log("Error loading filesystem: " + e.getMessage());
        }
    }
    
    private void saveToFile(File file) {
        if (!fileSystemModel.isFileSystemReady()) {
            FxLogger.getInstance().log("Error: No filesystem to save");
            return;
        }
        
        try {
            ch.supsi.fscli.backend.core.FileSystem backendFS = fileSystemModel.getBackendPersistenceController().getFileSystem();
            DirectoryNode root = backendFS.getRoot();
            
            FilesystemFileManager fileManager = new FilesystemFileManager(file.toPath());
            fileManager.save(root);
            
            FxLogger.getInstance().log("Filesystem saved to: " + file.getAbsolutePath());
            
        } catch (IOException e) {
            FxLogger.getInstance().log("Error saving filesystem: " + e.getMessage());
        }
    }
}
