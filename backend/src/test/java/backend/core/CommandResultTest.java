package backend.core;

import ch.supsi.fscli.backend.core.CommandResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandResultTest {

    @Test
    void testSuccessWithList() {
        List<String> output = Arrays.asList("line1", "line2");
        CommandResult result = CommandResult.success(output);
        
        assertTrue(result.isSuccess());
        assertEquals(2, result.getOutput().size());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testSuccessWithString() {
        CommandResult result = CommandResult.success("success message");
        
        assertTrue(result.isSuccess());
        assertEquals(1, result.getOutput().size());
        assertEquals("success message", result.getOutput().get(0));
        assertNull(result.getErrorMessage());
    }

    @Test
    void testSuccessNoArgs() {
        CommandResult result = CommandResult.success();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testSuccessWithNullString() {
        CommandResult result = CommandResult.success((String) null);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testSuccessWithEmptyString() {
        CommandResult result = CommandResult.success("");
        
        assertTrue(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testSuccessWithNullList() {
        CommandResult result = CommandResult.success((List<String>) null);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getOutput());
        assertTrue(result.getOutput().isEmpty());
    }

    @Test
    void testError() {
        CommandResult result = CommandResult.error("Error message");
        
        assertFalse(result.isSuccess());
        assertTrue(result.getOutput().isEmpty());
        assertEquals("Error message", result.getErrorMessage());
    }

    @Test
    void testErrorWithNullMessage() {
        CommandResult result = CommandResult.error(null);
        
        assertFalse(result.isSuccess());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testGetOutputReturnsDefensiveCopy() {
        List<String> original = Arrays.asList("a", "b");
        CommandResult result = CommandResult.success(original);
        
        List<String> retrieved = result.getOutput();
        assertNotSame(original, retrieved);
    }

    @Test
    void testOutputImmutability() {
        List<String> original = new ArrayList<>(Arrays.asList("a", "b"));
        CommandResult result = CommandResult.success(original);
        
        original.add("c");
        
        assertEquals(2, result.getOutput().size());
    }

    @Test
    void testGetOutputAsStringMultipleLines() {
        List<String> output = Arrays.asList("line1", "line2", "line3");
        CommandResult result = CommandResult.success(output);
        
        assertEquals("line1\nline2\nline3", result.getOutputAsString());
    }

    @Test
    void testGetOutputAsStringSingleLine() {
        CommandResult result = CommandResult.success("single");
        
        assertEquals("single", result.getOutputAsString());
    }

    @Test
    void testGetOutputAsStringEmpty() {
        CommandResult result = CommandResult.success();
        
        assertEquals("", result.getOutputAsString());
    }

    @Test
    void testGetOutputAsStringWithEmptyStrings() {
        List<String> output = Arrays.asList("", "middle", "");
        CommandResult result = CommandResult.success(output);
        
        assertEquals("\nmiddle\n", result.getOutputAsString());
    }

    @Test
    void testMultipleSuccessCalls() {
        CommandResult result1 = CommandResult.success("first");
        CommandResult result2 = CommandResult.success("second");
        
        assertEquals("first", result1.getOutput().get(0));
        assertEquals("second", result2.getOutput().get(0));
    }

    @Test
    void testMultipleErrorCalls() {
        CommandResult result1 = CommandResult.error("error1");
        CommandResult result2 = CommandResult.error("error2");
        
        assertEquals("error1", result1.getErrorMessage());
        assertEquals("error2", result2.getErrorMessage());
    }

    @Test
    void testGetOutputMultipleCallsReturnDifferentInstances() {
        CommandResult result = CommandResult.success(Arrays.asList("a", "b"));
        
        List<String> output1 = result.getOutput();
        List<String> output2 = result.getOutput();
        
        assertNotSame(output1, output2);
    }
}
