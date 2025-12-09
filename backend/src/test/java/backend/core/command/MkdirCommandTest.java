package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.MkdirCommand;
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
class MkdirCommandTest {

    @Mock
    private FileSystem fileSystem;

    private MkdirCommand mkdirCommand;

    @BeforeEach
    void setUp() {
        mkdirCommand = new MkdirCommand();
    }

    @Test
    void testGetName() {
        assertEquals("mkdir", mkdirCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Make directories", mkdirCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("mkdir <directory>...", mkdirCommand.getUsage());
    }

    @Test
    void testExecuteWithSingleDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Collections.singletonList("newdir"));
        
        CommandResult result = mkdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mkdir("newdir");
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteWithMultipleDirectories() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Arrays.asList("dir1", "dir2", "dir3"));
        
        CommandResult result = mkdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mkdir("dir1");
        verify(fileSystem).mkdir("dir2");
        verify(fileSystem).mkdir("dir3");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithAbsolutePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Collections.singletonList("/home/user/newdir"));
        
        CommandResult result = mkdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mkdir("/home/user/newdir");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithRelativePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Collections.singletonList("./subdir"));
        
        CommandResult result = mkdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mkdir("./subdir");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Collections.emptyList());
        
        CommandResult result = mkdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).mkdir(anyString());
        assertFalse(result.isSuccess());
        assertEquals("mkdir: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithFSException() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Collections.singletonList("existing"));
        doThrow(new FSException("Directory already exists")).when(fileSystem).mkdir("existing");
        
        assertThrows(FSException.class, () -> mkdirCommand.execute(fileSystem, syntax));
        verify(fileSystem).mkdir("existing");
    }

    @Test
    void testExecuteWithNestedPath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Collections.singletonList("parent/child/grandchild"));
        
        CommandResult result = mkdirCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mkdir("parent/child/grandchild");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecutePartialSuccessWithException() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mkdir", Arrays.asList("dir1", "existing", "dir3"));
        lenient().doNothing().when(fileSystem).mkdir("dir1");
        doThrow(new FSException("Directory already exists")).when(fileSystem).mkdir("existing");
        
        assertThrows(FSException.class, () -> mkdirCommand.execute(fileSystem, syntax));
        verify(fileSystem).mkdir("dir1");
        verify(fileSystem).mkdir("existing");
        verify(fileSystem, never()).mkdir("dir3");
    }
}
