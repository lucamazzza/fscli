package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.PwdCommand;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PwdCommandTest {

    @Mock
    private FileSystem fileSystem;

    private PwdCommand pwdCommand;

    @BeforeEach
    void setUp() {
        pwdCommand = new PwdCommand();
    }

    @Test
    void testGetName() {
        assertEquals("pwd", pwdCommand.getName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Print working directory", pwdCommand.getDescription());
    }

    @Test
    void testGetUsage() {
        assertEquals("pwd", pwdCommand.getUsage());
    }

    @Test
    void testExecuteReturnsCurrentDirectory() {
        CommandSyntax syntax = new CommandSyntax("pwd", Collections.emptyList());
        when(fileSystem.pwd()).thenReturn("/home/user");
        
        CommandResult result = pwdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).pwd();
        assertTrue(result.isSuccess());
        assertEquals(1, result.getOutput().size());
        assertEquals("/home/user", result.getOutput().get(0));
    }

    @Test
    void testExecuteReturnsRootDirectory() {
        CommandSyntax syntax = new CommandSyntax("pwd", Collections.emptyList());
        when(fileSystem.pwd()).thenReturn("/");
        
        CommandResult result = pwdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).pwd();
        assertTrue(result.isSuccess());
        assertEquals("/", result.getOutput().get(0));
    }

    @Test
    void testExecuteWithArguments() {
        CommandSyntax syntax = new CommandSyntax("pwd", Collections.singletonList("ignored"));
        when(fileSystem.pwd()).thenReturn("/var/log");
        
        CommandResult result = pwdCommand.execute(fileSystem, syntax);
        
        verify(fileSystem).pwd();
        assertTrue(result.isSuccess());
        assertEquals("/var/log", result.getOutput().get(0));
    }

    @Test
    void testExecuteReturnsDeepPath() {
        CommandSyntax syntax = new CommandSyntax("pwd", Collections.emptyList());
        when(fileSystem.pwd()).thenReturn("/home/user/Documents/Projects/fscli");
        
        CommandResult result = pwdCommand.execute(fileSystem, syntax);
        
        assertTrue(result.isSuccess());
        assertEquals("/home/user/Documents/Projects/fscli", result.getOutput().get(0));
    }
}
