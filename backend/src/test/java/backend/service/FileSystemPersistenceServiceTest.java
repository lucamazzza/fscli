package backend.service;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.service.FileSystemPersistenceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemPersistenceServiceTest {
    
    private FileSystemPersistenceService service;
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        service = new FileSystemPersistenceService();
        tempFile = Files.createTempFile("test-fs", ".json");
    }
    
    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }
    
    @Test
    void testSave_SimpleStructure() throws IOException {
        DirectoryNode root = new DirectoryNode();
        root.setParent(root);
        DirectoryNode subdir = new DirectoryNode();
        root.add("subdir", subdir);
        FileNode file = new FileNode();
        root.add("file.txt", file);
        
        service.save(root, tempFile);
        
        assertTrue(Files.exists(tempFile));
        assertTrue(Files.size(tempFile) > 0);
    }
    
    @Test
    void testLoad_Success() throws IOException {
        DirectoryNode root = new DirectoryNode();
        root.setParent(root);
        FileNode file = new FileNode();
        root.add("testfile.txt", file);
        
        service.save(root, tempFile);
        
        Optional<FileSystemNode> loaded = service.load(tempFile);
        
        assertTrue(loaded.isPresent());
        assertTrue(loaded.get().isDirectory());
        DirectoryNode loadedRoot = (DirectoryNode) loaded.get();
        assertNotNull(loadedRoot.get("testfile.txt"));
    }
    
    @Test
    void testLoad_NonExistentFile() {
        Path nonExistent = Path.of("nonexistent-" + System.currentTimeMillis() + ".json");
        
        Optional<FileSystemNode> loaded = service.load(nonExistent);
        
        assertTrue(loaded.isEmpty());
    }
    
    @Test
    void testLoad_EmptyFile() throws IOException {
        Files.writeString(tempFile, "");
        
        Optional<FileSystemNode> loaded = service.load(tempFile);
        
        assertTrue(loaded.isEmpty());
    }
    
    @Test
    void testLoad_InvalidJson() throws IOException {
        Files.writeString(tempFile, "{ invalid json content }");
        
        Optional<FileSystemNode> loaded = service.load(tempFile);
        
        assertTrue(loaded.isEmpty());
    }
    
    @Test
    void testSaveAndLoad_ComplexStructure() throws IOException {
        DirectoryNode root = new DirectoryNode();
        root.setParent(root);
        
        DirectoryNode dir1 = new DirectoryNode();
        root.add("dir1", dir1);
        
        DirectoryNode dir2 = new DirectoryNode();
        dir1.add("dir2", dir2);
        
        FileNode file1 = new FileNode();
        dir2.add("file1.txt", file1);
        
        FileNode file2 = new FileNode();
        root.add("file2.txt", file2);
        
        service.save(root, tempFile);
        Optional<FileSystemNode> loaded = service.load(tempFile);
        
        assertTrue(loaded.isPresent());
        DirectoryNode loadedRoot = (DirectoryNode) loaded.get();
        assertNotNull(loadedRoot.get("dir1"));
        assertNotNull(loadedRoot.get("file2.txt"));
    }
    
    @Test
    void testSave_OverwritesExistingFile() throws IOException {
        DirectoryNode root1 = new DirectoryNode();
        root1.setParent(root1);
        root1.add("file1.txt", new FileNode());
        
        service.save(root1, tempFile);
        long firstSize = Files.size(tempFile);
        
        DirectoryNode root2 = new DirectoryNode();
        root2.setParent(root2);
        root2.add("file1.txt", new FileNode());
        root2.add("file2.txt", new FileNode());
        
        service.save(root2, tempFile);
        long secondSize = Files.size(tempFile);
        
        assertTrue(secondSize >= firstSize);
    }
}
