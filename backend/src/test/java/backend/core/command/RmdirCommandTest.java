package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.RmdirCommand;
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
class RmdirCommandTest {

    @Mock
    private FileSystem fileSystem;

    private RmdirCommand rmdirCommand;

    @BeforeEach
    void setUp() {
        rmdirCommand = new RmdirCommand();
    }

    @Test
    void testGetName() {
        assertEquals("rmdir", rmdirCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Remove empty directories", rmdirCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("rmdir <directory>...", rmdirCommand.getUsage());
    }

    @Test
    void testExecuteWithSingleDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rmdir", Collections.singletonList("emptydir"));
        
        CommandResult result = rmdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rmdir("emptydir");
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteWithMultipleDirectories() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rmdir", Arrays.asList("dir1", "dir2", "dir3"));
        
        CommandResult result = rmdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rmdir("dir1");
        verify(fileSystem).rmdir("dir2");
        verify(fileSystem).rmdir("dir3");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithAbsolutePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rmdir", Collections.singletonList("/home/user/olddir"));
        
        CommandResult result = rmdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).rmdir("/home/user/olddir");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rmdir", Collections.emptyList());
        
        CommandResult result = rmdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).rmdir(anyString());
        assertFalse(result.isSuccess());
        assertEquals("rmdir: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNonEmptyDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rmdir", Collections.singletonList("nonempty"));
        doThrow(new FSException("Directory not empty")).when(fileSystem).rmdir("nonempty");
        
        assertThrows(FSException.class, () -> rmdirCommand.execute(fileSystem, syntax));
        verify(fileSystem).rmdir("nonempty");
    }

    @Test
    void testExecuteWithNonexistentDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rmdir", Collections.singletonList("nonexistent"));
        doThrow(new FSException("Directory does not exist")).when(fileSystem).rmdir("nonexistent");
        
        assertThrows(FSException.class, () -> rmdirCommand.execute(fileSystem, syntax));
        verify(fileSystem).rmdir("nonexistent");
    }

    @Test
    void testExecutePartialSuccessWithException() throws FSException {
        CommandSyntax syntax = new CommandSyntax("rmdir", Arrays.asList("dir1", "nonempty", "dir3"));
        lenient().doNothing().when(fileSystem).rmdir("dir1");
        doThrow(new FSException("Directory not empty")).when(fileSystem).rmdir("nonempty");
        
        assertThrows(FSException.class, () -> rmdirCommand.execute(fileSystem, syntax));
        verify(fileSystem).rmdir("dir1");
        verify(fileSystem).rmdir("nonempty");
        verify(fileSystem, never()).rmdir("dir3");
    }
}
