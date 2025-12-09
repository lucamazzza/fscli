package backend.provider.parser;

import ch.supsi.fscli.backend.provider.parser.CommandParser;
import ch.supsi.fscli.backend.core.exception.InvalidCommandException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    private CommandParser parser;

    @BeforeEach
    void setUp() {
        parser = new CommandParser();
    }

    @Test
    void testParseSimpleCommand() throws Exception {
        CommandSyntax cmd = parser.parse("pwd");
        assertEquals("pwd", cmd.getCommandName());
        assertEquals(0, cmd.getArgumentCount());
        assertFalse(cmd.hasArguments());
    }

    @Test
    void testParseCommandWithArguments() throws Exception {
        CommandSyntax cmd = parser.parse("cd /home/user");
        assertEquals("cd", cmd.getCommandName());
        assertEquals(1, cmd.getArgumentCount());
        assertEquals("/home/user", cmd.getArgument(0));
        assertTrue(cmd.hasArguments());
    }

    @Test
    void testParseCommandWithMultipleArguments() throws Exception {
        CommandSyntax cmd = parser.parse("mkdir dir1 dir2 dir3");
        assertEquals("mkdir", cmd.getCommandName());
        assertEquals(3, cmd.getArgumentCount());
        assertEquals("dir1", cmd.getArgument(0));
        assertEquals("dir2", cmd.getArgument(1));
        assertEquals("dir3", cmd.getArgument(2));
    }

    @Test
    void testParseCommandWithQuotedArguments() throws Exception {
        CommandSyntax cmd = parser.parse("touch 'file with spaces.txt'");
        assertEquals("touch", cmd.getCommandName());
        assertEquals(1, cmd.getArgumentCount());
        assertEquals("file with spaces.txt", cmd.getArgument(0));
    }

    @Test
    void testParseCommandWithOptions() throws Exception {
        CommandSyntax cmd = parser.parse("ls -i /home");
        assertEquals("ls", cmd.getCommandName());
        assertEquals(2, cmd.getArgumentCount());
        assertEquals("-i", cmd.getArgument(0));
        assertEquals("/home", cmd.getArgument(1));
    }

    @Test
    void testParseCommandGetArgumentOutOfBounds() throws Exception {
        CommandSyntax cmd = parser.parse("ls");
        assertNull(cmd.getArgument(0));
        assertNull(cmd.getArgument(-1));
        assertNull(cmd.getArgument(10));
    }

    @Test
    void testParseEmptyCommandThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse("");
        });
    }

    @Test
    void testParseNullCommandThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse(null);
        });
    }

    @Test
    void testParseWhitespaceOnlyThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse("   \t  ");
        });
    }

    @Test
    void testParseInvalidCommandNameThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            parser.parse("123invalid arg1");
        });
    }

    @Test
    void testParseCommandToString() throws Exception {
        CommandSyntax cmd = parser.parse("mkdir dir1 dir2");
        String str = cmd.toString();
        assertTrue(str.contains("mkdir"));
        assertTrue(str.contains("dir1"));
        assertTrue(str.contains("dir2"));
    }
}
