package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.MvCommand;
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
class MvCommandTest {

    @Mock
    private FileSystem fileSystem;

    private MvCommand mvCommand;

    @BeforeEach
    void setUp() {
        mvCommand = new MvCommand();
    }

    @Test
    void testGetName() {
        assertEquals("mv", mvCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Move/rename file or directory", mvCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("mv <source> <destination>", mvCommand.getUsage());
    }

    @Test
    void testExecuteRenameFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("oldname.txt", "newname.txt"));
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mv("oldname.txt", "newname.txt");
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteMoveFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("file.txt", "/home/user/Documents/file.txt"));
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mv("file.txt", "/home/user/Documents/file.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteMoveDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("/home/user/olddir", "/home/user/newdir"));
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mv("/home/user/olddir", "/home/user/newdir");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithRelativePaths() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("./file.txt", "../backup/file.txt"));
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mv("./file.txt", "../backup/file.txt");
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithMissingOperand() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Collections.singletonList("file.txt"));
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).mv(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("mv: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Collections.emptyList());
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).mv(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("mv: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithTooManyArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).mv(anyString(), anyString());
        assertFalse(result.isSuccess());
        assertEquals("mv: too many arguments", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNonexistentSource() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("nonexistent.txt", "newname.txt"));
        doThrow(new FSException("Source file not found")).when(fileSystem).mv("nonexistent.txt", "newname.txt");
        
        assertThrows(FSException.class, () -> mvCommand.execute(fileSystem, syntax));
        verify(fileSystem).mv("nonexistent.txt", "newname.txt");
    }

    @Test
    void testExecuteWithExistingDestination() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("file.txt", "existing.txt"));
        doThrow(new FSException("Destination already exists")).when(fileSystem).mv("file.txt", "existing.txt");
        
        assertThrows(FSException.class, () -> mvCommand.execute(fileSystem, syntax));
        verify(fileSystem).mv("file.txt", "existing.txt");
    }

    @Test
    void testExecuteRenameWithExtensionChange() throws FSException {
        CommandSyntax syntax = new CommandSyntax("mv", Arrays.asList("document.txt", "document.md"));
        
        CommandResult result = mvCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).mv("document.txt", "document.md");
        assertTrue(result.isSuccess());
    }
}
