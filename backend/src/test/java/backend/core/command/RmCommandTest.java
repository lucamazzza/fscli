package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.RmCommand;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RmCommandTest {

    @Mock
    private FileSystem fileSystem;

    private RmCommand rmCommand;

    @BeforeEach
    void setUp() {
        rmCommand = new RmCommand();
    }

    @Test
    void testGetName() {
        assertEquals("rm", rmCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Remove files", rmCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("rm <file>...", rmCommand.getUsage());
    }

    @Test
    void testExecuteWithSingleFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Collections.singletonList("oldfile.txt"));
        
        CommandResult result = rmCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rm("oldfile.txt");
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteWithMultipleFiles() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Arrays.asList("file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = rmCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rm("file1.txt");
        verify(fileSystem).rm("file2.txt");
        verify(fileSystem).rm("file3.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithAbsolutePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Collections.singletonList("/tmp/tempfile.txt"));
        
        CommandResult result = rmCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rm("/tmp/tempfile.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithRelativePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Collections.singletonList("./subdir/file.txt"));
        
        CommandResult result = rmCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rm("./subdir/file.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Collections.emptyList());
        
        CommandResult result = rmCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).rm(anyString());
        assertFalse(result.isSuccess());
        assertEquals("rm: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNonexistentFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Collections.singletonList("nonexistent.txt"));
        doThrow(new FSException("File not found")).when(fileSystem).rm("nonexistent.txt");
        
        assertThrows(FSException.class, () -> rmCommand.execute(fileSystem, syntax));
        verify(fileSystem).rm("nonexistent.txt");
    }

    @Test
    void testExecutePartialSuccessWithException() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Arrays.asList("file1.txt", "protected.txt", "file3.txt"));
        lenient().doNothing().when(fileSystem).rm("file1.txt");
        doThrow(new FSException("Permission denied")).when(fileSystem).rm("protected.txt");
        
        assertThrows(FSException.class, () -> rmCommand.execute(fileSystem, syntax));
        verify(fileSystem).rm("file1.txt");
        verify(fileSystem).rm("protected.txt");
        verify(fileSystem, never()).rm("file3.txt");
    }

    @Test
    void testExecuteWithHiddenFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Collections.singletonList(".hidden"));
        
        CommandResult result = rmCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rm(".hidden");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithDifferentExtensions() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rm", Arrays.asList("file.txt", "data.json", "script.sh", "image.png"));
        
        CommandResult result = rmCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rm("file.txt");
        verify(fileSystem).rm("data.json");
        verify(fileSystem).rm("script.sh");
        verify(fileSystem).rm("image.png");
        assertTrue(result.isSuccess());
    }
}
