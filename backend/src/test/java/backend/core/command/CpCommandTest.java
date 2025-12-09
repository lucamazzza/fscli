package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.CpCommand;
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
class CpCommandTest {

    @Mock
    private FileSystem fileSystem;

    private CpCommand cpCommand;

    @BeforeEach
    void setUp() {
        cpCommand = new CpCommand();
    }

    @Test
    void testGetName() {
        assertEquals("cp", cpCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Copy files and directories", cpCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("cp [-r] <source> <destination>", cpCommand.getUsage());
    }

    @Test
    void testExecuteCopyFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("source.txt", "dest.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cp("source.txt", "dest.txt");
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteCopyWithRecursiveFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("-r", "sourcedir", "destdir"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cp("sourcedir", "destdir");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteCopyWithAbsolutePaths() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("/home/user/file.txt", "/tmp/file.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cp("/home/user/file.txt", "/tmp/file.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithMissingOperand() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Collections.singletonList("source.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).cp(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("cp: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Collections.emptyList());
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).cp(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("cp: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithTooManyArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).cp(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("cp: too many arguments", result.getErrorMessage());
    }

    @Test
    void testExecuteWithTooManyArgumentsWithRecursiveFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("-r", "file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).cp(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("cp: too many arguments", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNonexistentSource() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("nonexistent.txt", "dest.txt"));
        doThrow(new FSException("File not found")).when(fileSystem).cp("nonexistent.txt", "dest.txt");
        
        assertThrows(FSException.class, () -> cpCommand.execute(fileSystem, syntax));
        verify(fileSystem).cp("nonexistent.txt", "dest.txt");
    }

    @Test
    void testExecuteWithRelativePaths() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("./file.txt", "../backup/file.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cp("./file.txt", "../backup/file.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteRecursiveFlagWithSingleFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("-r", "file.txt", "copy.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).cp("file.txt", "copy.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithMissingOperandAfterRecursiveFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Collections.singletonList("-r"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).cp(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("cp: missing operand", result.getErrorMessage());
    }
}
