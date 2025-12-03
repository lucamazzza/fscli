package backend.controller;

import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FileSystemPersistenceService.
 * Tests filesystem lifecycle and persistence operations.
 */
class FileSystemPersistenceServiceTest {
    
    private FileSystemPersistenceController controller;
    
    @BeforeEach
    void setUp() {
        controller = new FileSystemPersistenceController();
    }
    
    @Test
    void testCreateNewFileSystem() {
        // Act
        controller.createNewFileSystem();
        
        // Assert
        assertTrue(controller.isFileSystemLoaded());
        assertNotNull(controller.getFileSystem());
    }
    
    @Test
    void testIsFileSystemLoaded_Initially() {
        // Assert
        assertFalse(controller.isFileSystemLoaded());
    }
    
    @Test
    void testIsFileSystemLoaded_AfterCreation() {
        // Act
        controller.createNewFileSystem();
        
        // Assert
        assertTrue(controller.isFileSystemLoaded());
    }
    
    @Test
    void testGetCurrentDirectory_NoFileSystem() {
        // Act
        String dir = controller.getCurrentDirectory();
        
        // Assert
        assertEquals("/", dir);
    }
    
    @Test
    void testGetCurrentDirectory_AfterCreation() {
        // Act
        controller.createNewFileSystem();
        String dir = controller.getCurrentDirectory();
        
        // Assert
        assertEquals("/", dir);
    }
    
    @Test
    void testSetFileSystem() {
        // Arrange
        InMemoryFileSystem fs = new InMemoryFileSystem();
        
        // Act
        controller.setFileSystem(fs);
        
        // Assert
        assertTrue(controller.isFileSystemLoaded());
        assertSame(fs, controller.getFileSystem());
    }
    
    @Test
    void testGetFileSystem_Initially() {
        // Act
        FileSystem fs = controller.getFileSystem();
        
        // Assert
        assertNull(fs);
    }
    
    @Test
    void testGetFileSystem_AfterCreation() {
        // Act
        controller.createNewFileSystem();
        FileSystem fs = controller.getFileSystem();
        
        // Assert
        assertNotNull(fs);
        assertInstanceOf(InMemoryFileSystem.class, fs);
    }
    
    @Test
    void testMultipleCreations_ReplacesFilesystem() {
        // Act
        controller.createNewFileSystem();
        FileSystem first = controller.getFileSystem();
        
        controller.createNewFileSystem();
        FileSystem second = controller.getFileSystem();
        
        // Assert
        assertNotNull(first);
        assertNotNull(second);
        assertNotSame(first, second);
    }
    
    @Test
    void testSetFileSystem_ReplacesExisting() {
        // Arrange
        controller.createNewFileSystem();
        FileSystem original = controller.getFileSystem();
        InMemoryFileSystem replacement = new InMemoryFileSystem();
        
        // Act
        controller.setFileSystem(replacement);
        
        // Assert
        assertNotSame(original, controller.getFileSystem());
        assertSame(replacement, controller.getFileSystem());
    }
}
