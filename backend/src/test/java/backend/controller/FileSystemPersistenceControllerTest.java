package backend.controller;

import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemPersistenceControllerTest {
    
    private FileSystemPersistenceController controller;
    private Path tempFile;
    
    @BeforeEach
    void setUp() throws IOException {
        controller = new FileSystemPersistenceController();
        tempFile = Files.createTempFile("test-filesystem", ".json");
    }
    
    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }
    
    @Test
    void testCreateNewFileSystem() {
        controller.createNewFileSystem();
        
        assertTrue(controller.isFileSystemLoaded());
        assertNotNull(controller.getFileSystem());
    }
    
    @Test
    void testIsFileSystemLoaded_Initially() {
        assertFalse(controller.isFileSystemLoaded());
    }
    
    @Test
    void testIsFileSystemLoaded_AfterCreation() {
        controller.createNewFileSystem();
        assertTrue(controller.isFileSystemLoaded());
    }
    
    @Test
    void testGetCurrentDirectory_NoFileSystem() {
        String dir = controller.getCurrentDirectory();
        assertEquals("/", dir);
    }
    
    @Test
    void testGetCurrentDirectory_AfterCreation() {
        controller.createNewFileSystem();
        String dir = controller.getCurrentDirectory();
        assertEquals("/", dir);
    }
    
    @Test
    void testSetFileSystem() {
        InMemoryFileSystem fs = new InMemoryFileSystem();
        controller.setFileSystem(fs);
        
        assertTrue(controller.isFileSystemLoaded());
        assertSame(fs, controller.getFileSystem());
    }
    
    @Test
    void testGetFileSystem_Initially() {
        FileSystem fs = controller.getFileSystem();
        assertNull(fs);
    }
    
    @Test
    void testGetFileSystem_AfterCreation() {
        controller.createNewFileSystem();
        FileSystem fs = controller.getFileSystem();
        
        assertNotNull(fs);
        assertInstanceOf(InMemoryFileSystem.class, fs);
    }
    
    @Test
    void testSaveFileSystem_Success() throws IOException, FSException {
        controller.createNewFileSystem();
        FileSystem fs = controller.getFileSystem();
        fs.mkdir("/testdir");
        fs.touch("/testfile.txt");
        
        controller.saveFileSystem(tempFile);
        
        assertTrue(Files.exists(tempFile));
        assertTrue(Files.size(tempFile) > 0);
    }
    
    @Test
    void testSaveFileSystem_NoFileSystemLoaded() {
        assertThrows(IllegalStateException.class, () -> {
            controller.saveFileSystem(tempFile);
        });
    }
    
    @Test
    void testLoadFileSystem_Success() throws IOException, FSException {
        controller.createNewFileSystem();
        FileSystem fs = controller.getFileSystem();
        fs.mkdir("/loadtest");
        fs.touch("/loadtest/file.txt");
        
        controller.saveFileSystem(tempFile);
        
        controller.createNewFileSystem();
        
        boolean loaded = controller.loadFileSystem(tempFile);
        
        assertTrue(loaded);
        assertTrue(controller.isFileSystemLoaded());
    }
    
    @Test
    void testLoadFileSystem_NonExistentFile() throws IOException {
        Path nonExistent = Path.of("nonexistent-" + System.currentTimeMillis() + ".json");
        
        boolean loaded = controller.loadFileSystem(nonExistent);
        
        assertFalse(loaded);
    }
    
    @Test
    void testLoadFileSystem_EmptyFile() throws IOException {
        Files.writeString(tempFile, "");
        
        boolean loaded = controller.loadFileSystem(tempFile);
        
        assertFalse(loaded);
    }
    
    @Test
    void testLoadFileSystem_InvalidJson() throws IOException {
        Files.writeString(tempFile, "{ invalid json }");
        
        boolean loaded = controller.loadFileSystem(tempFile);
        
        assertFalse(loaded);
    }
    
    @Test
    void testSaveAndLoad_PreservesStructure() throws IOException, FSException {
        controller.createNewFileSystem();
        FileSystem fs = controller.getFileSystem();
        
        fs.mkdir("/dir1");
        fs.mkdir("/dir1/subdir");
        fs.touch("/dir1/file1.txt");
        fs.touch("/dir1/subdir/file2.txt");
        
        controller.saveFileSystem(tempFile);
        
        controller.createNewFileSystem();
        boolean loaded = controller.loadFileSystem(tempFile);
        
        assertTrue(loaded);
        FileSystem loadedFs = controller.getFileSystem();
        assertNotNull(loadedFs);
    }
    
    @Test
    void testMultipleCreations_ReplacesFilesystem() {
        controller.createNewFileSystem();
        FileSystem first = controller.getFileSystem();
        
        controller.createNewFileSystem();
        FileSystem second = controller.getFileSystem();
        
        assertNotNull(first);
        assertNotNull(second);
        assertNotSame(first, second);
    }
    
    @Test
    void testSetFileSystem_ReplacesExisting() {
        controller.createNewFileSystem();
        FileSystem original = controller.getFileSystem();
        InMemoryFileSystem replacement = new InMemoryFileSystem();
        
        controller.setFileSystem(replacement);
        
        assertNotSame(original, controller.getFileSystem());
        assertSame(replacement, controller.getFileSystem());
    }
}
