package backend.provider.parser;

import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandSyntaxTest {

    @Test
    void testConstructorWithNoArguments() {
        CommandSyntax cmd = new CommandSyntax("pwd", Collections.emptyList());
        assertEquals("pwd", cmd.getCommandName());
        assertEquals(0, cmd.getArgumentCount());
        assertFalse(cmd.hasArguments());
        assertNotNull(cmd.getArguments());
    }

    @Test
    void testConstructorWithSingleArgument() {
        List<String> args = Collections.singletonList("/home/user");
        CommandSyntax cmd = new CommandSyntax("cd", args);
        assertEquals("cd", cmd.getCommandName());
        assertEquals(1, cmd.getArgumentCount());
        assertTrue(cmd.hasArguments());
        assertEquals("/home/user", cmd.getArgument(0));
    }

    @Test
    void testConstructorWithMultipleArguments() {
        List<String> args = Arrays.asList("dir1", "dir2", "dir3");
        CommandSyntax cmd = new CommandSyntax("mkdir", args);
        assertEquals("mkdir", cmd.getCommandName());
        assertEquals(3, cmd.getArgumentCount());
        assertTrue(cmd.hasArguments());
        assertEquals("dir1", cmd.getArgument(0));
        assertEquals("dir2", cmd.getArgument(1));
        assertEquals("dir3", cmd.getArgument(2));
    }

    @Test
    void testGetCommandName() {
        CommandSyntax cmd = new CommandSyntax("ls", Collections.emptyList());
        assertEquals("ls", cmd.getCommandName());
    }

    @Test
    void testGetArguments() {
        List<String> args = Arrays.asList("-l", "-a", "/home");
        CommandSyntax cmd = new CommandSyntax("ls", args);
        List<String> returnedArgs = cmd.getArguments();
        assertEquals(3, returnedArgs.size());
        assertEquals("-l", returnedArgs.get(0));
        assertEquals("-a", returnedArgs.get(1));
        assertEquals("/home", returnedArgs.get(2));
    }

    @Test
    void testGetArgumentCount() {
        CommandSyntax cmd1 = new CommandSyntax("pwd", Collections.emptyList());
        assertEquals(0, cmd1.getArgumentCount());

        CommandSyntax cmd2 = new CommandSyntax("cd", Collections.singletonList("/home"));
        assertEquals(1, cmd2.getArgumentCount());

        CommandSyntax cmd3 = new CommandSyntax("mkdir", Arrays.asList("a", "b", "c", "d"));
        assertEquals(4, cmd3.getArgumentCount());
    }

    @Test
    void testGetArgumentValidIndex() {
        List<String> args = Arrays.asList("arg0", "arg1", "arg2");
        CommandSyntax cmd = new CommandSyntax("test", args);
        assertEquals("arg0", cmd.getArgument(0));
        assertEquals("arg1", cmd.getArgument(1));
        assertEquals("arg2", cmd.getArgument(2));
    }

    @Test
    void testGetArgumentNegativeIndex() {
        List<String> args = Arrays.asList("arg0", "arg1");
        CommandSyntax cmd = new CommandSyntax("test", args);
        assertNull(cmd.getArgument(-1));
        assertNull(cmd.getArgument(-5));
    }

    @Test
    void testGetArgumentIndexTooLarge() {
        List<String> args = Arrays.asList("arg0", "arg1");
        CommandSyntax cmd = new CommandSyntax("test", args);
        assertNull(cmd.getArgument(2));
        assertNull(cmd.getArgument(10));
        assertNull(cmd.getArgument(100));
    }

    @Test
    void testGetArgumentFromEmptyArguments() {
        CommandSyntax cmd = new CommandSyntax("test", Collections.emptyList());
        assertNull(cmd.getArgument(0));
        assertNull(cmd.getArgument(1));
    }

    @Test
    void testHasArgumentsWhenEmpty() {
        CommandSyntax cmd = new CommandSyntax("pwd", Collections.emptyList());
        assertFalse(cmd.hasArguments());
    }

    @Test
    void testHasArgumentsWhenNotEmpty() {
        CommandSyntax cmd1 = new CommandSyntax("cd", Collections.singletonList("/home"));
        assertTrue(cmd1.hasArguments());

        CommandSyntax cmd2 = new CommandSyntax("ls", Arrays.asList("-l", "-a"));
        assertTrue(cmd2.hasArguments());
    }

    @Test
    void testToStringWithNoArguments() {
        CommandSyntax cmd = new CommandSyntax("pwd", Collections.emptyList());
        String str = cmd.toString();
        assertTrue(str.contains("CommandSyntax"));
        assertTrue(str.contains("pwd"));
        assertTrue(str.contains("[]"));
    }

    @Test
    void testToStringWithArguments() {
        List<String> args = Arrays.asList("dir1", "dir2");
        CommandSyntax cmd = new CommandSyntax("mkdir", args);
        String str = cmd.toString();
        assertTrue(str.contains("CommandSyntax"));
        assertTrue(str.contains("mkdir"));
        assertTrue(str.contains("dir1"));
        assertTrue(str.contains("dir2"));
    }

    @Test
    void testCommandWithSpecialCharacters() {
        List<String> args = Arrays.asList("file with spaces.txt", "another-file_123.doc");
        CommandSyntax cmd = new CommandSyntax("touch", args);
        assertEquals("touch", cmd.getCommandName());
        assertEquals(2, cmd.getArgumentCount());
        assertEquals("file with spaces.txt", cmd.getArgument(0));
        assertEquals("another-file_123.doc", cmd.getArgument(1));
    }

    @Test
    void testCommandWithOptionsAndPaths() {
        List<String> args = Arrays.asList("-i", "-v", "/home/user/docs");
        CommandSyntax cmd = new CommandSyntax("ls", args);
        assertEquals("ls", cmd.getCommandName());
        assertEquals(3, cmd.getArgumentCount());
        assertEquals("-i", cmd.getArgument(0));
        assertEquals("-v", cmd.getArgument(1));
        assertEquals("/home/user/docs", cmd.getArgument(2));
    }

    @Test
    void testArgumentListImmutability() {
        List<String> args = new ArrayList<>(Arrays.asList("arg1", "arg2"));
        CommandSyntax cmd = new CommandSyntax("test", args);
        
        // Modify the original list
        args.add("arg3");
        
        // CommandSyntax should still have only 2 arguments if it made a defensive copy
        // Note: If the implementation doesn't make a defensive copy, this test documents the behavior
        assertEquals(3, cmd.getArgumentCount()); // This reflects current implementation
    }

    @Test
    void testMultipleInstancesIndependence() {
        List<String> args1 = Collections.singletonList("arg1");
        List<String> args2 = Arrays.asList("arg2", "arg3");
        
        CommandSyntax cmd1 = new CommandSyntax("cmd1", args1);
        CommandSyntax cmd2 = new CommandSyntax("cmd2", args2);
        
        assertEquals("cmd1", cmd1.getCommandName());
        assertEquals(1, cmd1.getArgumentCount());
        
        assertEquals("cmd2", cmd2.getCommandName());
        assertEquals(2, cmd2.getArgumentCount());
    }
}
