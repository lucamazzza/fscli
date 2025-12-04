package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.service.FileSystemPersistenceService;
import ch.supsi.fscli.backend.service.FileSystemService;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Controller responsible for filesystem persistence operations.
 * Handles filesystem lifecycle management (creation, loading, saving).
 * 
 * <p>This controller wraps {@link FileSystemService} and {@link FileSystemPersistenceService}:</p>
 * <ul>
 *   <li>Filesystem creation</li>
 *   <li>Filesystem state management</li>
 *   <li>Current directory tracking</li>
 *   <li>Save/load operations through proper layer delegation</li>
 * </ul>
 * 
 * <p>Architecture: Frontend → Controller → Service → FileManager → Serializer</p>
 * 
 * @see FileSystemService
 * @see FileSystemPersistenceService
 */
public class FileSystemPersistenceController {
    /** The service layer that manages filesystem state */
    private final FileSystemService service;
    
    /** The service layer that handles persistence operations */
    private final FileSystemPersistenceService persistenceService;

    /**
     * Constructs a new FileSystemPersistenceService.
     * Initializes the underlying service layers.
     */
    public FileSystemPersistenceController() {
        this.service = new FileSystemService();
        this.persistenceService = new FileSystemPersistenceService();
    }

    /**
     * Creates a new empty in-memory filesystem.
     * Initializes the root directory and sets current directory to root.
     */
    public void createNewFileSystem() {
        service.createNewFileSystem();
    }

    /**
     * Checks if a filesystem instance is currently loaded.
     * 
     * @return true if filesystem is loaded and ready to use, false otherwise
     */
    public boolean isFileSystemLoaded() {
        return service.isFileSystemLoaded();
    }

    /**
     * Gets the current working directory path.
     * 
     * @return Current directory path (e.g., "/home/user"), or "/" if no filesystem loaded
     */
    public String getCurrentDirectory() {
       return service.getCurrentDirectory();
    }

    /**
     * Sets the filesystem instance.
     * Used when loading a filesystem from file.
     * 
     * @param fs The filesystem instance to use
     */
    public void setFileSystem(FileSystem fs) {
        service.setFileSystem(fs);
    }

    /**
     * Gets the current filesystem instance.
     * Used for serialization and persistence operations.
     * 
     * @return The current filesystem instance, or null if none loaded
     */
    public FileSystem getFileSystem() {
        return service.getFileSystem();
    }
    
    /**
     * Saves the current filesystem to the specified path.
     * Delegates to the persistence service layer.
     * 
     * @param path The file path where to save the filesystem
     * @throws IOException If an I/O error occurs during save
     * @throws IllegalStateException If no filesystem is loaded
     */
    public void saveFileSystem(Path path) throws IOException {
        FileSystem fs = service.getFileSystem();
        if (fs == null) {
            throw new IllegalStateException(BackendMessageProvider.get("error.noFilesystemLoaded"));
        }
        DirectoryNode root = fs.getRoot();
        persistenceService.save(root, path);
    }
    
    /**
     * Loads a filesystem from the specified path.
     * Delegates to the persistence service layer and updates the filesystem state.
     * 
     * @param path The file path from where to load the filesystem
     * @return true if filesystem was loaded successfully, false otherwise
     */
    public boolean loadFileSystem(Path path) {
        Optional<FileSystemNode> rootOpt = persistenceService.load(path);
        
        if (rootOpt.isEmpty() || !(rootOpt.get() instanceof DirectoryNode)) {
            return false;
        }
        
        DirectoryNode root = (DirectoryNode) rootOpt.get();
        InMemoryFileSystem loadedFS = new InMemoryFileSystem(root);
        service.setFileSystem(loadedFS);
        
        return true;
    }
}
