package backend.provider.parser;

import ch.supsi.fscli.backend.provider.parser.CommandLexer;
import ch.supsi.fscli.backend.core.exception.InvalidCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandLexerTest {

    private CommandLexer lexer;

    @BeforeEach
    void setUp() {
        lexer = new CommandLexer();
    }

    @Test
    void testTokenizeSimpleCommand() throws Exception {
        List<String> tokens = lexer.tokenize("ls");
        assertEquals(1, tokens.size());
        assertEquals("ls", tokens.get(0));
    }

    @Test
    void testTokenizeCommandWithArguments() throws Exception {
        List<String> tokens = lexer.tokenize("mkdir dir1 dir2");
        assertEquals(3, tokens.size());
        assertEquals("mkdir", tokens.get(0));
        assertEquals("dir1", tokens.get(1));
        assertEquals("dir2", tokens.get(2));
    }

    @Test
    void testTokenizeWithSingleQuotes() throws Exception {
        List<String> tokens = lexer.tokenize("touch 'file with spaces.txt'");
        assertEquals(2, tokens.size());
        assertEquals("touch", tokens.get(0));
        assertEquals("file with spaces.txt", tokens.get(1));
    }

    @Test
    void testTokenizeWithDoubleQuotes() throws Exception {
        List<String> tokens = lexer.tokenize("mkdir \"my folder\"");
        assertEquals(2, tokens.size());
        assertEquals("mkdir", tokens.get(0));
        assertEquals("my folder", tokens.get(1));
    }

    @Test
    void testTokenizeWithEscapedCharacters() throws Exception {
        List<String> tokens = lexer.tokenize("touch file\\ name.txt");
        assertEquals(2, tokens.size());
        assertEquals("touch", tokens.get(0));
        assertEquals("file name.txt", tokens.get(1));
    }

    @Test
    void testTokenizeWithMultipleSpaces() throws Exception {
        List<String> tokens = lexer.tokenize("ls    -l     /home");
        assertEquals(3, tokens.size());
        assertEquals("ls", tokens.get(0));
        assertEquals("-l", tokens.get(1));
        assertEquals("/home", tokens.get(2));
    }

    @Test
    void testTokenizeWithTabs() throws Exception {
        List<String> tokens = lexer.tokenize("ls\t-i\t/tmp");
        assertEquals(3, tokens.size());
        assertEquals("ls", tokens.get(0));
        assertEquals("-i", tokens.get(1));
        assertEquals("/tmp", tokens.get(2));
    }

    @Test
    void testTokenizeMixedQuotes() throws Exception {
        List<String> tokens = lexer.tokenize("echo \"It's working\" 'He said \"hello\"'");
        assertEquals(3, tokens.size());
        assertEquals("echo", tokens.get(0));
        assertEquals("It's working", tokens.get(1));
        assertEquals("He said \"hello\"", tokens.get(2));
    }

    @Test
    void testTokenizeEmptyQuotes() throws Exception {
        List<String> tokens = lexer.tokenize("touch ''");
        assertEquals(1, tokens.size());
        assertEquals("touch", tokens.get(0));
    }

    @Test
    void testTokenizeEscapedBackslash() throws Exception {
        List<String> tokens = lexer.tokenize("echo test\\\\file");
        assertEquals(2, tokens.size());
        assertEquals("echo", tokens.get(0));
        assertEquals("test\\file", tokens.get(1));
    }

    @Test
    void testTokenizeEscapedQuote() throws Exception {
        List<String> tokens = lexer.tokenize("echo \\\"quoted\\\"");
        assertEquals(2, tokens.size());
        assertEquals("echo", tokens.get(0));
        assertEquals("\"quoted\"", tokens.get(1));
    }

    @Test
    void testTokenizePathWithSlashes() throws Exception {
        List<String> tokens = lexer.tokenize("cd /home/user/documents");
        assertEquals(2, tokens.size());
        assertEquals("cd", tokens.get(0));
        assertEquals("/home/user/documents", tokens.get(1));
    }

    @Test
    void testTokenizeWithOptions() throws Exception {
        List<String> tokens = lexer.tokenize("ls -la /home");
        assertEquals(3, tokens.size());
        assertEquals("ls", tokens.get(0));
        assertEquals("-la", tokens.get(1));
        assertEquals("/home", tokens.get(2));
    }

    @Test
    void testTokenizeNullThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            lexer.tokenize(null);
        });
    }

    @Test
    void testTokenizeEmptyStringThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            lexer.tokenize("");
        });
    }

    @Test
    void testTokenizeWhitespaceOnlyThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            lexer.tokenize("   \t  \n  ");
        });
    }

    @Test
    void testTokenizeUnclosedSingleQuoteThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            lexer.tokenize("echo 'unclosed");
        });
    }

    @Test
    void testTokenizeUnclosedDoubleQuoteThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            lexer.tokenize("echo \"unclosed");
        });
    }

    @Test
    void testTokenizeTrailingEscapeThrowsException() {
        assertThrows(InvalidCommandException.class, () -> {
            lexer.tokenize("echo test\\");
        });
    }

    @Test
    void testValidateCommandName() {
        assertTrue(lexer.validateCommandName("ls"));
        assertTrue(lexer.validateCommandName("mkdir"));
        assertTrue(lexer.validateCommandName("cd"));
        assertTrue(lexer.validateCommandName("test_command"));
        assertTrue(lexer.validateCommandName("my-command"));
        assertTrue(lexer.validateCommandName("command123"));
    }

    @Test
    void testValidateInvalidCommandName() {
        assertFalse(lexer.validateCommandName(null));
        assertFalse(lexer.validateCommandName(""));
        assertFalse(lexer.validateCommandName("123command"));
        assertFalse(lexer.validateCommandName("-command"));
        assertFalse(lexer.validateCommandName("_command"));
        assertFalse(lexer.validateCommandName("cmd/with/slash"));
        assertFalse(lexer.validateCommandName("cmd with space"));
    }
}
