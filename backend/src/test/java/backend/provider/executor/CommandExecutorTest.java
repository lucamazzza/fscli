package backend.provider.executor;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.core.command.Command;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.executor.CommandExecutor;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.provider.parser.CommandParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandExecutorTest {

    private CommandExecutor executor;
    private FileSystem fileSystem;

    private static class TestCommand implements Command {
        private final String name;

        public TestCommand(String name) {
            this.name = name;
        }

        @Override
        public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
            return CommandResult.success("Executed " + name);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return "Test command " + name;
        }

        @Override
        public String getUsage() {
            return name + " [args]";
        }
    }

    @BeforeEach
    void setUp() {
        fileSystem = new InMemoryFileSystem();
        executor = new CommandExecutor(fileSystem, new CommandParser());
    }

    @Test
    void testRegisterCommand() {
        TestCommand cmd = new TestCommand("test");
        executor.registerCommand(cmd);
        
        Command registered = executor.getCommand("test");
        assertNotNull(registered);
        assertEquals("test", registered.getName());
    }

    @Test
    void testExecuteRegisteredCommand() {
        TestCommand cmd = new TestCommand("test");
        executor.registerCommand(cmd);
        
        CommandResult result = executor.execute("test");
        
        assertTrue(result.isSuccess());
        assertEquals("Executed test", result.getOutput().get(0));
    }

    @Test
    void testExecuteUnregisteredCommand() {
        CommandResult result = executor.execute("nonexistent");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Unknown command"));
    }

    @Test
    void testExecuteInvalidCommandSyntax() {
        CommandResult result = executor.execute("");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Invalid command"));
    }

    @Test
    void testExecuteNullCommand() {
        CommandResult result = executor.execute(null);
        
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Invalid command"));
    }

    @Test
    void testGetAvailableCommands() {
        TestCommand cmd1 = new TestCommand("cmd1");
        TestCommand cmd2 = new TestCommand("cmd2");
        
        executor.registerCommand(cmd1);
        executor.registerCommand(cmd2);
        
        Map<String, Command> commands = executor.getAvailableCommands();
        
        assertEquals(2, commands.size());
        assertTrue(commands.containsKey("cmd1"));
        assertTrue(commands.containsKey("cmd2"));
    }

    @Test
    void testGetAvailableCommandsReturnsDefensiveCopy() {
        TestCommand cmd = new TestCommand("test");
        executor.registerCommand(cmd);
        
        Map<String, Command> commands1 = executor.getAvailableCommands();
        Map<String, Command> commands2 = executor.getAvailableCommands();
        
        assertNotSame(commands1, commands2);
    }

    @Test
    void testGetCommandNull() {
        Command cmd = executor.getCommand("nonexistent");
        assertNull(cmd);
    }

    @Test
    void testExecuteCommandWithArguments() {
        TestCommand cmd = new TestCommand("test");
        executor.registerCommand(cmd);
        
        CommandResult result = executor.execute("test arg1 arg2");
        
        assertTrue(result.isSuccess());
    }

    @Test
    void testRegisterMultipleCommands() {
        executor.registerCommand(new TestCommand("cmd1"));
        executor.registerCommand(new TestCommand("cmd2"));
        executor.registerCommand(new TestCommand("cmd3"));
        
        assertEquals(3, executor.getAvailableCommands().size());
    }

    @Test
    void testRegisterCommandOverwrite() {
        executor.registerCommand(new TestCommand("test"));
        executor.registerCommand(new TestCommand("test"));
        
        assertEquals(1, executor.getAvailableCommands().size());
    }

    @Test
    void testExecuteCommandThatThrowsFSException() {
        Command failingCommand = new Command() {
            @Override
            public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
                throw new FSException("Test exception");
            }

            @Override
            public String getName() { return "fail"; }

            @Override
            public String getDescription() { return "Failing command"; }

            @Override
            public String getUsage() { return "fail"; }
        };
        
        executor.registerCommand(failingCommand);
        CommandResult result = executor.execute("fail");
        
        assertFalse(result.isSuccess());
        assertEquals("Test exception", result.getErrorMessage());
    }

    @Test
    void testExecuteCommandThatThrowsRuntimeException() {
        Command failingCommand = new Command() {
            @Override
            public CommandResult execute(FileSystem fs, CommandSyntax syntax) {
                throw new RuntimeException("Unexpected error");
            }

            @Override
            public String getName() { return "crash"; }

            @Override
            public String getDescription() { return "Crashing command"; }

            @Override
            public String getUsage() { return "crash"; }
        };
        
        executor.registerCommand(failingCommand);
        CommandResult result = executor.execute("crash");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Error executing command"));
    }
}
