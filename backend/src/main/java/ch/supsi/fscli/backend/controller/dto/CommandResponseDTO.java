package ch.supsi.fscli.backend.controller.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for command execution responses.
 * Used to transfer command execution results between layers.
 * 
 * <p>This DTO contains:</p>
 * <ul>
 *   <li>Success status of the command</li>
 *   <li>Output lines from the command</li>
 *   <li>Error message if command failed</li>
 *   <li>Timestamp of execution</li>
 * </ul>
 * 
 * <p>Immutable copies of output are provided to prevent external modification.</p>
 * 
 * @see CommandRequest
 */
public class CommandResponseDTO {
    /** Whether the command executed successfully */
    private boolean success;
    
    /** List of output lines produced by the command */
    private List<String> output;
    
    /** Error message if command failed, null if successful */
    private String errorMessage;
    
    /** Unix timestamp (milliseconds) when response was created */
    private long timestamp;
    
    /**
     * Constructs an empty response with current timestamp.
     * Used for deserialization.
     */
    public CommandResponseDTO() {
        this.output = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Constructs a command response with all fields.
     * 
     * @param success Whether command succeeded
     * @param output List of output lines (will be copied)
     * @param errorMessage Error message, or null if successful
     */
    public CommandResponseDTO(boolean success, List<String> output, String errorMessage) {
        this.success = success;
        this.output = output != null ? new ArrayList<>(output) : new ArrayList<>();
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * Creates a successful response with multiple output lines.
     * 
     * @param output List of output lines
     * @return Successful response DTO
     */
    public static CommandResponseDTO success(List<String> output) {
        return new CommandResponseDTO(true, output, null);
    }
    
    /**
     * Creates a successful response with single output line.
     * 
     * @param output Single output line
     * @return Successful response DTO
     */
    public static CommandResponseDTO success(String output) {
        List<String> outputList = new ArrayList<>();
        if (output != null && !output.isEmpty()) outputList.add(output);
        return new CommandResponseDTO(true, outputList, null);
    }
    
    /**
     * Creates an error response with error message.
     * 
     * @param errorMessage Description of the error
     * @return Error response DTO
     */
    public static CommandResponseDTO error(String errorMessage) {
        return new CommandResponseDTO(false, new ArrayList<>(), errorMessage);
    }
    
    /**
     * Checks if the command executed successfully.
     * 
     * @return true if successful, false if error occurred
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Sets the success status.
     * 
     * @param success true for success, false for error
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    /**
     * Gets the output lines produced by the command.
     * Returns a defensive copy to prevent external modification.
     * 
     * @return Immutable copy of output lines
     */
    public List<String> getOutput() {
        return new ArrayList<>(output);
    }
    
    /**
     * Sets the output lines.
     * Creates a defensive copy of the provided list.
     * 
     * @param output List of output lines
     */
    public void setOutput(List<String> output) {
        this.output = output != null ? new ArrayList<>(output) : new ArrayList<>();
    }
    
    /**
     * Gets the error message if command failed.
     * 
     * @return Error message, or null if command succeeded
     */
    public String getErrorMessage() {
        return errorMessage;
    }
    
    /**
     * Sets the error message.
     * 
     * @param errorMessage Description of the error
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    /**
     * Gets the timestamp when this response was created.
     * 
     * @return Unix timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Sets the timestamp.
     * 
     * @param timestamp Unix timestamp in milliseconds
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Gets all output lines as a single string.
     * Lines are joined with newline characters.
     * 
     * @return All output as a single string
     */
    public String getOutputAsString() {
        return String.join("\n", output);
    }
}
