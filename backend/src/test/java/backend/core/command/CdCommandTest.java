package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.CdCommand;
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
class CdCommandTest {

    @Mock
    private FileSystem fileSystem;

    private CdCommand cdCommand;

    @BeforeEach
    void setUp() {
        cdCommand = new CdCommand();
    }

    @Test
    void testGetName() {
        assertEquals("cd", cdCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Change directory", cdCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("cd <directory>", cdCommand.getUsage());
    }

    @Test
    void testExecuteWithValidDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cd", Collections.singletonList("/home/user"));
        
        CommandResult result = cdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cd("/home/user");
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteWithRelativePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cd", Collections.singletonList("Documents"));
        
        CommandResult result = cdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cd("Documents");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithParentDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cd", Collections.singletonList(".."));
        
        CommandResult result = cdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cd("..");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cd", Collections.emptyList());
        
        CommandResult result = cdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).cd(anyString());
        assertFalse(result.isSuccess());
        assertEquals("cd: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithTooManyArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cd", Arrays.asList("/home", "/var"));
        
        CommandResult result = cdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).cd(anyString());
        assertFalse(result.isSuccess());
        assertEquals("cd: too many arguments", result.getErrorMessage());
    }

    @Test
    void testExecuteWithFSException() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cd", Collections.singletonList("/nonexistent"));
        doThrow(new FSException("Directory not found")).when(fileSystem).cd("/nonexistent");
        
        assertThrows(FSException.class, () -> cdCommand.execute(fileSystem, syntax));
        verify(fileSystem).cd("/nonexistent");
    }

    @Test
    void testExecuteWithRoot() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cd", Collections.singletonList("/"));
        
        CommandResult result = cdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cd("/");
        assertTrue(result.isSuccess());
    }
}
