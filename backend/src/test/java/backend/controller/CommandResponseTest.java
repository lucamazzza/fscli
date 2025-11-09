package backend.controller;

import ch.supsi.fscli.backend.controller.CommandResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandResponseTest {

    @Test
    void testSuccessfulResponse() {
        List<String> output = Arrays.asList("line1", "line2");
        CommandResponse response = new CommandResponse(true, output, null);
        
        assertTrue(response.isSuccess());
        assertEquals(2, response.getOutput().size());
        assertNull(response.getErrorMessage());
    }

    @Test
    void testFailedResponse() {
        CommandResponse response = new CommandResponse(false, null, "Error message");
        
        assertFalse(response.isSuccess());
        assertTrue(response.getOutput().isEmpty());
        assertEquals("Error message", response.getErrorMessage());
    }

    @Test
    void testEmptyOutput() {
        CommandResponse response = new CommandResponse(true, new ArrayList<>(), null);
        
        assertTrue(response.isSuccess());
        assertTrue(response.getOutput().isEmpty());
    }

    @Test
    void testNullOutput() {
        CommandResponse response = new CommandResponse(true, null, null);
        
        assertNotNull(response.getOutput());
        assertTrue(response.getOutput().isEmpty());
    }

    @Test
    void testGetOutputReturnsDefensiveCopy() {
        List<String> output = new ArrayList<>(Arrays.asList("a", "b"));
        CommandResponse response = new CommandResponse(true, output, null);
        
        List<String> retrieved1 = response.getOutput();
        List<String> retrieved2 = response.getOutput();
        
        assertNotSame(retrieved1, retrieved2);
    }

    @Test
    void testOutputImmutability() {
        List<String> output = new ArrayList<>(Arrays.asList("a", "b"));
        CommandResponse response = new CommandResponse(true, output, null);
        
        output.add("c");
        
        assertEquals(2, response.getOutput().size());
    }

    @Test
    void testGetOutputAsStringMultipleLines() {
        List<String> output = Arrays.asList("line1", "line2", "line3");
        CommandResponse response = new CommandResponse(true, output, null);
        
        assertEquals("line1\nline2\nline3", response.getOutputAsString());
    }

    @Test
    void testGetOutputAsStringSingleLine() {
        List<String> output = Arrays.asList("single");
        CommandResponse response = new CommandResponse(true, output, null);
        
        assertEquals("single", response.getOutputAsString());
    }

    @Test
    void testGetOutputAsStringEmpty() {
        CommandResponse response = new CommandResponse(true, new ArrayList<>(), null);
        
        assertEquals("", response.getOutputAsString());
    }

    @Test
    void testErrorMessageNull() {
        CommandResponse response = new CommandResponse(true, Arrays.asList("output"), null);
        
        assertNull(response.getErrorMessage());
    }

    @Test
    void testErrorMessagePresent() {
        CommandResponse response = new CommandResponse(false, null, "File not found");
        
        assertEquals("File not found", response.getErrorMessage());
    }

    @Test
    void testSuccessWithErrorMessage() {
        CommandResponse response = new CommandResponse(true, Arrays.asList("done"), "warning");
        
        assertTrue(response.isSuccess());
        assertEquals("warning", response.getErrorMessage());
    }

    @Test
    void testFailureWithOutput() {
        List<String> output = Arrays.asList("partial output");
        CommandResponse response = new CommandResponse(false, output, "Error occurred");
        
        assertFalse(response.isSuccess());
        assertEquals(1, response.getOutput().size());
        assertEquals("Error occurred", response.getErrorMessage());
    }

    @Test
    void testConstructorWithAllNull() {
        CommandResponse response = new CommandResponse(false, null, null);
        
        assertFalse(response.isSuccess());
        assertNotNull(response.getOutput());
        assertTrue(response.getOutput().isEmpty());
        assertNull(response.getErrorMessage());
    }

    @Test
    void testOutputWithEmptyStrings() {
        List<String> output = Arrays.asList("", "middle", "");
        CommandResponse response = new CommandResponse(true, output, null);
        
        assertEquals(3, response.getOutput().size());
        assertEquals("\nmiddle\n", response.getOutputAsString());
    }
}
