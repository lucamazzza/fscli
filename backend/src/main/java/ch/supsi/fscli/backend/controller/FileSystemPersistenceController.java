package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.service.FileSystemService;

/**
 * Controller responsible for filesystem persistence operations.
 * Handles filesystem lifecycle management (creation, loading, saving).
 * 
 * <p>This controller wraps {@link FileSystemService} and provides:</p>
 * <ul>
 *   <li>Filesystem creation</li>
 *   <li>Filesystem state management</li>
 *   <li>Current directory tracking</li>
 *   <li>Filesystem instance access for serialization</li>
 * </ul>
 * 
 * <p>Used primarily by frontend controllers for save/load operations.</p>
 * 
 * @see FileSystemService
 * @see ch.supsi.fscli.backend.data.serde.FilesystemFileManager
 */
public class FileSystemPersistenceController {
    /** The service layer that manages filesystem state */
    private final FileSystemService service;

    /**
     * Constructs a new FileSystemPersistenceController.
     * Initializes the underlying service layer.
     */
    public FileSystemPersistenceController() {
        this.service = new FileSystemService();
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
    public void setFileSystem(InMemoryFileSystem fs) {
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
}
