package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.LsCommand;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LsCommandTest {

    @Mock
    private FileSystem fileSystem;

    private LsCommand lsCommand;

    @BeforeEach
    void setUp() {
        lsCommand = new LsCommand();
    }

    @Test
    void testGetName() {
        assertEquals("ls", lsCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("List directory contents", lsCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("ls [-i] [directory]", lsCommand.getUsage());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Collections.emptyList());
        List<String> entries = Arrays.asList("file1.txt", "file2.txt", "dir1");
        when(fileSystem.ls(".", false)).thenReturn(entries);
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ls(".", false);
        assertTrue(result.isSuccess());
        assertEquals(entries, result.getOutput());
    }

    @Test
    void testExecuteWithDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Collections.singletonList("/home/user"));
        List<String> entries = Arrays.asList("Documents", "Downloads", "Pictures");
        when(fileSystem.ls("/home/user", false)).thenReturn(entries);
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ls("/home/user", false);
        assertTrue(result.isSuccess());
        assertEquals(entries, result.getOutput());
    }

    @Test
    void testExecuteWithInodeFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Collections.singletonList("-i"));
        List<String> entries = Arrays.asList("123 file1.txt", "456 file2.txt");
        when(fileSystem.ls(".", true)).thenReturn(entries);
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ls(".", true);
        assertTrue(result.isSuccess());
        assertEquals(entries, result.getOutput());
    }

    @Test
    void testExecuteWithInodeFlagAndDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Arrays.asList("-i", "/var/log"));
        List<String> entries = Arrays.asList("789 syslog", "012 kern.log");
        when(fileSystem.ls("/var/log", true)).thenReturn(entries);
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ls("/var/log", true);
        assertTrue(result.isSuccess());
        assertEquals(entries, result.getOutput());
    }

    @Test
    void testExecuteWithDirectoryAndInodeFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Arrays.asList("/tmp", "-i"));
        List<String> entries = Arrays.asList("345 temp1", "678 temp2");
        when(fileSystem.ls("/tmp", true)).thenReturn(entries);
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ls("/tmp", true);
        assertTrue(result.isSuccess());
        assertEquals(entries, result.getOutput());
    }

    @Test
    void testExecuteWithInvalidOption() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Collections.singletonList("-l"));
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).ls(anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("ls: invalid option: -l", result.getErrorMessage());
    }

    @Test
    void testExecuteWithEmptyDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Collections.emptyList());
        when(fileSystem.ls(".", false)).thenReturn(Collections.emptyList());
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ls(".", false);
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testExecuteWithNonexistentDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Collections.singletonList("/nonexistent"));
        when(fileSystem.ls("/nonexistent", false)).thenThrow(new FSException("Directory not found"));
        
        assertThrows(FSException.class, () -> lsCommand.execute(fileSystem, syntax));
        verify(fileSystem).ls("/nonexistent", false);
    }

    @Test
    void testExecuteWithRelativePath() throws FSException {
        CommandSyntax syntax = new CommandSyntax("ls", Collections.singletonList("./subdir"));
        List<String> entries = Arrays.asList("file.txt");
        when(fileSystem.ls("./subdir", false)).thenReturn(entries);
        
        CommandResult result = lsCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).ls("./subdir", false);
        assertTrue(result.isSuccess());
        assertEquals(entries, result.getOutput());
    }
}
