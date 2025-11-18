package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.LnCommand;
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
class LnCommandTest {

    @Mock
    private FileSystem fileSystem;

    private LnCommand lnCommand;

    @BeforeEach
    void setUp() {
        lnCommand = new LnCommand();
    }

    @Test
    void testGetName() {
        assertEquals("ln", lnCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Create link", lnCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("ln [-s] <target> <link>", lnCommand.getUsage());
    }

    @Test
    void testExecuteHardLink() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("file.txt", "link.txt"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ln("file.txt", "link.txt", false);
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteSymbolicLink() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("-s", "file.txt", "symlink.txt"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ln("file.txt", "symlink.txt", true);
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteSymbolicLinkAbsolutePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("-s", "/home/user/file.txt", "/tmp/link.txt"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ln("/home/user/file.txt", "/tmp/link.txt", true);
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteHardLinkAbsolutePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("/var/log/app.log", "/backup/app.log"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ln("/var/log/app.log", "/backup/app.log", false);
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithMissingOperand() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Collections.singletonList("file.txt"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).ln(anyString(), anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("ln: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithMissingOperandSymbolicFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("-s", "file.txt"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).ln(anyString(), anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("ln: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Collections.emptyList());
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).ln(anyString(), anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("ln: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithTooManyArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).ln(anyString(), anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("ln: too many arguments", result.getErrorMessage());
    }

    @Test
    void testExecuteWithTooManyArgumentsSymbolicFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("-s", "file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).ln(anyString(), anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("ln: too many arguments", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNonexistentTarget() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("nonexistent.txt", "link.txt"));
        doThrow(new FSException("Target does not exist")).when(fileSystem).ln("nonexistent.txt", "link.txt", false);
        
        assertThrows(FSException.class, () -> lnCommand.execute(fileSystem, syntax));
        verify(fileSystem).ln("nonexistent.txt", "link.txt", false);
    }

    @Test
    void testExecuteSymbolicLinkToDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ln", Arrays.asList("-s", "/home/user/docs", "/tmp/docs-link"));
        
        CommandResult result = lnCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ln("/home/user/docs", "/tmp/docs-link", true);
        assertTrue(result.isSuccess());
    }
}
