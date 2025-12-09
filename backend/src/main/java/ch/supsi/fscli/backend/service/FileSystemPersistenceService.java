package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.serde.FilesystemFileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Service layer for filesystem persistence operations.
 * Handles save/load operations through the FileManager layer.
 * 
 * <p>Architecture: Controller → Service → FileManager → Serializer</p>
 */
public class FileSystemPersistenceService {
    
    /**
     * Saves a filesystem root node to the specified path.
     * 
     * @param root The root directory node to save
     * @param path The file path where to save the filesystem
     * @throws IOException If an I/O error occurs during save
     */
    public void save(DirectoryNode root, Path path) throws IOException {
        FilesystemFileManager fileManager = new FilesystemFileManager(path);
        fileManager.save(root);
    }
    
    /**
     * Loads a filesystem root node from the specified path.
     * 
     * @param path The file path from where to load the filesystem
     * @return Optional containing the loaded FileSystemNode, or empty if load fails
     */
    public Optional<FileSystemNode> load(Path path) {
        FilesystemFileManager fileManager = new FilesystemFileManager(path);
        return fileManager.load();
    }
}
