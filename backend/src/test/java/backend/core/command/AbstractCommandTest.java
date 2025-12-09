package backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.AbstractCommand;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AbstractCommandTest {

    // Concrete implementation for testing
    private static class TestCommand extends AbstractCommand {
        public TestCommand() {
            super("test", "Test command description", "test [args]");
        }

        public TestCommand(String name, String description, String usage) {
            super(name, description, usage);
        }

        @Override
        public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
            return CommandResult.success("executed");
        }
    }

    @Test
    void testGetName() {
        TestCommand command = new TestCommand();
        assertEquals("test", command.getName());
    }

    @Test
    void testGetDescription() {
        TestCommand command = new TestCommand();
        assertEquals("Test command description", command.getDescription());
    }

    @Test
    void testGetUsage() {
        TestCommand command = new TestCommand();
        assertEquals("test [args]", command.getUsage());
    }

    @Test
    void testConstructorWithParameters() {
        TestCommand command = new TestCommand("mycommand", "My description", "mycommand <arg>");
        
        assertEquals("mycommand", command.getName());
        assertEquals("My description", command.getDescription());
        assertEquals("mycommand <arg>", command.getUsage());
    }

    @Test
    void testEmptyName() {
        TestCommand command = new TestCommand("", "", "");
        
        assertEquals("", command.getName());
        assertEquals("", command.getDescription());
        assertEquals("", command.getUsage());
    }

    @Test
    void testNullParameters() {
        TestCommand command = new TestCommand(null, null, null);
        
        assertNull(command.getName());
        assertNull(command.getDescription());
        assertNull(command.getUsage());
    }

    @Test
    void testLongDescription() {
        String longDesc = "This is a very long description that contains multiple words and explains what the command does in great detail";
        TestCommand command = new TestCommand("cmd", longDesc, "usage");
        
        assertEquals(longDesc, command.getDescription());
    }

    @Test
    void testComplexUsage() {
        String complexUsage = "command [-option1] [-option2 <arg>] <required> [optional]";
        TestCommand command = new TestCommand("cmd", "desc", complexUsage);
        
        assertEquals(complexUsage, command.getUsage());
    }

    @Test
    void testMultipleInstancesIndependent() {
        TestCommand cmd1 = new TestCommand("cmd1", "desc1", "usage1");
        TestCommand cmd2 = new TestCommand("cmd2", "desc2", "usage2");
        
        assertEquals("cmd1", cmd1.getName());
        assertEquals("cmd2", cmd2.getName());
        assertNotEquals(cmd1.getName(), cmd2.getName());
    }

    @Test
    void testExecuteReturnsResult() throws FSException {
        TestCommand command = new TestCommand();
        CommandSyntax syntax = new CommandSyntax("test", Collections.emptyList());
        
        CommandResult result = command.execute(null, syntax);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("executed", result.getOutput().get(0));
    }
}
