package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.CpCommand;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.SymlinkNode;
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
        FileNode fileNode = new FileNode();
        
        when(fileSystem.resolveNode("source.txt", true)).thenReturn(fileNode);
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).resolveNode("source.txt", true);
        verify(fileSystem).createNode(eq("dest.txt"), any(FileSystemNode.class));
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteCopyDirectory() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("-r", "sourcedir", "destdir"));
        DirectoryNode dirNode = new DirectoryNode();
        
        when(fileSystem.resolveNode("sourcedir", true)).thenReturn(dirNode);
        when(fileSystem.listNodes(any(DirectoryNode.class))).thenReturn(Collections.emptyList());
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).resolveNode("sourcedir", true);
        verify(fileSystem).createNode(eq("destdir"), any(FileSystemNode.class));
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteCopyDirectoryWithoutRecursiveFlag() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("sourcedir", "destdir"));
        DirectoryNode dirNode = new DirectoryNode();
        
        when(fileSystem.resolveNode("sourcedir", true)).thenReturn(dirNode);
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).resolveNode("sourcedir", true);
        verify(fileSystem, never()).createNode(anyString(), any(FileSystemNode.class));
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("-r not specified"));
    }

    @Test
    void testExecuteWithMissingOperand() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Collections.singletonList("source.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).resolveNode(anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("cp: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNoArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Collections.emptyList());
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).resolveNode(anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("cp: missing operand", result.getErrorMessage());
    }

    @Test
    void testExecuteWithTooManyArguments() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("file1.txt", "file2.txt", "file3.txt"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem, never()).resolveNode(anyString(), anyBoolean());
        assertFalse(result.isSuccess());
        assertEquals("cp: too many arguments", result.getErrorMessage());
    }

    @Test
    void testExecuteWithNonexistentSource() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("nonexistent.txt", "dest.txt"));
        
        when(fileSystem.resolveNode("nonexistent.txt", true)).thenThrow(new FSException("File not found"));
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).resolveNode("nonexistent.txt", true);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("File not found"));
    }

    @Test
    void testExecuteRecursiveFlagWithFile() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("-r", "file.txt", "dest.txt"));
        FileNode fileNode = new FileNode();
        
        when(fileSystem.resolveNode("file.txt", true)).thenReturn(fileNode);
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).resolveNode("file.txt", true);
        verify(fileSystem).createNode(eq("dest.txt"), any(FileSystemNode.class));
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteCopySymlink() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("symlink", "dest"));
        SymlinkNode symlinkNode = new SymlinkNode("target");
        
        when(fileSystem.resolveNode("symlink", true)).thenReturn(symlinkNode);
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).resolveNode("symlink", true);
        verify(fileSystem).createNode(eq("dest"), any(FileSystemNode.class));
        assertTrue(result.isSuccess());
    }

    @Test
    void testExecuteWithAbsolutePaths() throws FSException {
        CommandSyntax syntax = new CommandSyntax("cp", Arrays.asList("/home/user/file.txt", "/tmp/file.txt"));
        FileNode fileNode = new FileNode();
        
        when(fileSystem.resolveNode("/home/user/file.txt", true)).thenReturn(fileNode);
        
        CommandResult result = cpCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).resolveNode("/home/user/file.txt", true);
        verify(fileSystem).createNode(eq("/tmp/file.txt"), any(FileSystemNode.class));
        assertTrue(result.isSuccess());
    }
}
