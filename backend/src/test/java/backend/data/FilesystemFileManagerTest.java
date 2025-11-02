package backend.data;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FSNode;
import ch.supsi.fscli.backend.data.FileNode;
import ch.supsi.fscli.backend.data.FilesystemFileManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FilesystemFileManagerTest {

    @Test
    void testSaveAndLoadFileNode() throws IOException {
        Path tempFile = Files.createTempFile("filesystem", ".json");
        FilesystemFileManager manager = new FilesystemFileManager(tempFile);

        FileNode file = new FileNode();
        
        manager.save(file);
        Optional<FSNode> loaded = manager.load();

        assertTrue(loaded.isPresent());
        assertInstanceOf(FileNode.class, loaded.get());
        assertFalse(loaded.get().isDirectory());

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testSaveAndLoadDirectoryNode() throws IOException {
        Path tempFile = Files.createTempFile("filesystem", ".json");
        FilesystemFileManager manager = new FilesystemFileManager(tempFile);

        DirectoryNode root = new DirectoryNode();
        root.add("file1.txt", new FileNode());
        root.add("file2.txt", new FileNode());
        
        DirectoryNode subdir = new DirectoryNode();
        subdir.add("file3.txt", new FileNode());
        root.add("subdir", subdir);

        manager.save(root);
        Optional<FSNode> loaded = manager.load();

        assertTrue(loaded.isPresent());
        assertInstanceOf(DirectoryNode.class, loaded.get());
        
        DirectoryNode loadedDir = (DirectoryNode) loaded.get();
        assertTrue(loadedDir.contains("file1.txt"));
        assertTrue(loadedDir.contains("file2.txt"));
        assertTrue(loadedDir.contains("subdir"));
        
        FSNode loadedSubdir = loadedDir.get("subdir");
        assertInstanceOf(DirectoryNode.class, loadedSubdir);
        assertTrue(((DirectoryNode) loadedSubdir).contains("file3.txt"));

        Files.deleteIfExists(tempFile);
    }

    @Test
    void testLoadNonExistentFile() {
        Path tempFile = Paths.get("nonexistent-file-" + System.currentTimeMillis() + ".json");
        FilesystemFileManager manager = new FilesystemFileManager(tempFile);

        Optional<FSNode> loaded = manager.load();

        assertFalse(loaded.isPresent());
    }

    @Test
    void testLoadEmptyFile() throws IOException {
        Path tempFile = Files.createTempFile("empty-filesystem", ".json");
        FilesystemFileManager manager = new FilesystemFileManager(tempFile);

        Optional<FSNode> loaded = manager.load();

        assertFalse(loaded.isPresent());

        Files.deleteIfExists(tempFile);
    }
}
