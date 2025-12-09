package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.TouchCommand;
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
class TouchCommandTest {

    @Mock
    private FileSystem fileSystem;

    private TouchCommand touchCommand;

    @BeforeEach
    void setUp() {
        touchCommand = new TouchCommand();
    }

    @Test
    void testGetName() {
        assertEquals("touch", touchCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Create empty file or update timestamp", touchCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("touch <file>...", touchCommand.getUsage());
    }

    @Test
    void testExecuteWithSingleFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Collections.singletonList("newfile.txt"));
        
        CommandResult result = touchCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).touch("newfile.txt");
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteWithMultipleFiles() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Arrays.asList("file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = touchCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).touch("file1.txt");
        verify(fileSystem).touch("file2.txt");
        verify(fileSystem).touch("file3.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithAbsolutePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Collections.singletonList("/home/user/document.txt"));
        
        CommandResult result = touchCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).touch("/home/user/document.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithRelativePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Collections.singletonList("./subdir/file.txt"));
        
        CommandResult result = touchCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).touch("./subdir/file.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Collections.emptyList());
        
        CommandResult result = touchCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).touch(anyString());
        assertFalse(result.isSuccess());
        assertEquals("touch: missing file operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithFSException() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Collections.singletonList("readonly/file.txt"));
        doThrow(new FSException("Permission denied")).when(fileSystem).touch("readonly/file.txt");
        
        assertThrows(FSException.class, () -> touchCommand.execute(fileSystem, syntax));
        verify(fileSystem).touch("readonly/file.txt");
    }

    @Test
    void testExecuteWithFileExtensions() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Arrays.asList("file.txt", "data.json", "script.sh"));
        
        CommandResult result = touchCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).touch("file.txt");
        verify(fileSystem).touch("data.json");
        verify(fileSystem).touch("script.sh");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecutePartialSuccessWithException() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Arrays.asList("file1.txt", "invalid", "file3.txt"));
        lenient().doNothing().when(fileSystem).touch("file1.txt");
        doThrow(new FSException("Cannot create file")).when(fileSystem).touch("invalid");
        
        assertThrows(FSException.class, () -> touchCommand.execute(fileSystem, syntax));
        verify(fileSystem).touch("file1.txt");
        verify(fileSystem).touch("invalid");
        verify(fileSystem, never()).touch("file3.txt");
    }

    @Test
    void testExecuteWithHiddenFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("touch", Collections.singletonList(".hidden"));
        
        CommandResult result = touchCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).touch(".hidden");
        assertTrue(result.isSuccess());
    }
}
