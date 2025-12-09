package backend.controller.dto;

import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandResponseDTOTest {

    @Test
    void testDefaultConstructor() {
        CommandResponseDTO dto = new CommandResponseDTO();
        assertFalse(dto.isSuccess());
        assertNotNull(dto.getOutput());
        assertTrue(dto.getOutput().isEmpty());
        assertNull(dto.getErrorMessage());
        assertTrue(dto.getTimestamp() > 0);
    }

    @Test
    void testParameterizedConstructor() {
        List<String> output = Arrays.asList("line1", "line2");
        CommandResponseDTO dto = new CommandResponseDTO(true, output, null);
        
        assertTrue(dto.isSuccess());
        assertEquals(2, dto.getOutput().size());
        assertEquals("line1", dto.getOutput().get(0));
        assertEquals("line2", dto.getOutput().get(1));
        assertNull(dto.getErrorMessage());
    }

    @Test
    void testParameterizedConstructorWithError() {
        CommandResponseDTO dto = new CommandResponseDTO(false, null, "Error occurred");
        
        assertFalse(dto.isSuccess());
        assertTrue(dto.getOutput().isEmpty());
        assertEquals("Error occurred", dto.getErrorMessage());
    }

    @Test
    void testSuccessFactoryMethod() {
        List<String> output = Arrays.asList("result1", "result2");
        CommandResponseDTO dto = CommandResponseDTO.success(output);
        
        assertTrue(dto.isSuccess());
        assertEquals(2, dto.getOutput().size());
        assertNull(dto.getErrorMessage());
    }

    @Test
    void testSuccessFactoryMethodWithString() {
        CommandResponseDTO dto = CommandResponseDTO.success("Success message");
        
        assertTrue(dto.isSuccess());
        assertEquals(1, dto.getOutput().size());
        assertEquals("Success message", dto.getOutput().get(0));
        assertNull(dto.getErrorMessage());
    }

    @Test
    void testSuccessFactoryMethodWithNullString() {
        CommandResponseDTO dto = CommandResponseDTO.success((String) null);
        
        assertTrue(dto.isSuccess());
        assertTrue(dto.getOutput().isEmpty());
        assertNull(dto.getErrorMessage());
    }

    @Test
    void testSuccessFactoryMethodWithEmptyString() {
        CommandResponseDTO dto = CommandResponseDTO.success("");
        
        assertTrue(dto.isSuccess());
        assertTrue(dto.getOutput().isEmpty());
    }

    @Test
    void testErrorFactoryMethod() {
        CommandResponseDTO dto = CommandResponseDTO.error("Something went wrong");
        
        assertFalse(dto.isSuccess());
        assertTrue(dto.getOutput().isEmpty());
        assertEquals("Something went wrong", dto.getErrorMessage());
    }

    @Test
    void testSetSuccess() {
        CommandResponseDTO dto = new CommandResponseDTO();
        dto.setSuccess(true);
        assertTrue(dto.isSuccess());
        
        dto.setSuccess(false);
        assertFalse(dto.isSuccess());
    }

    @Test
    void testSetOutput() {
        CommandResponseDTO dto = new CommandResponseDTO();
        List<String> output = Arrays.asList("a", "b", "c");
        dto.setOutput(output);
        
        assertEquals(3, dto.getOutput().size());
        assertEquals("a", dto.getOutput().get(0));
    }

    @Test
    void testSetOutputNull() {
        CommandResponseDTO dto = new CommandResponseDTO();
        dto.setOutput(null);
        
        assertNotNull(dto.getOutput());
        assertTrue(dto.getOutput().isEmpty());
    }

    @Test
    void testSetErrorMessage() {
        CommandResponseDTO dto = new CommandResponseDTO();
        dto.setErrorMessage("New error");
        assertEquals("New error", dto.getErrorMessage());
    }

    @Test
    void testSetTimestamp() {
        CommandResponseDTO dto = new CommandResponseDTO();
        long timestamp = 9999999999L;
        dto.setTimestamp(timestamp);
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testGetOutputAsString() {
        List<String> output = Arrays.asList("line1", "line2", "line3");
        CommandResponseDTO dto = new CommandResponseDTO(true, output, null);
        
        String result = dto.getOutputAsString();
        assertEquals("line1\nline2\nline3", result);
    }

    @Test
    void testGetOutputAsStringEmpty() {
        CommandResponseDTO dto = new CommandResponseDTO();
        assertEquals("", dto.getOutputAsString());
    }

    @Test
    void testGetOutputAsStringSingleLine() {
        CommandResponseDTO dto = CommandResponseDTO.success("Single line");
        assertEquals("Single line", dto.getOutputAsString());
    }

    @Test
    void testOutputImmutability() {
        List<String> originalOutput = new ArrayList<>(Arrays.asList("a", "b"));
        CommandResponseDTO dto = new CommandResponseDTO(true, originalOutput, null);
        
        originalOutput.add("c");
        
        assertEquals(2, dto.getOutput().size());
    }

    @Test
    void testGetOutputReturnsCopy() {
        List<String> output = Arrays.asList("a", "b");
        CommandResponseDTO dto = new CommandResponseDTO(true, output, null);
        
        List<String> retrieved = dto.getOutput();
        assertNotSame(output, retrieved);
    }

    @Test
    void testTimestampGenerated() {
        long before = System.currentTimeMillis();
        CommandResponseDTO dto = new CommandResponseDTO();
        long after = System.currentTimeMillis();
        
        assertTrue(dto.getTimestamp() >= before);
        assertTrue(dto.getTimestamp() <= after);
    }

    @Test
    void testSuccessWithNullList() {
        CommandResponseDTO dto = CommandResponseDTO.success((List<String>) null);
        assertTrue(dto.isSuccess());
        assertNotNull(dto.getOutput());
        assertTrue(dto.getOutput().isEmpty());
    }
}
