package ch.supsi.fscli.backend.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class CommandResponseDTO {
    private boolean success;
    private List<String> output;
    private String errorMessage;
    private long timestamp;
    
    public CommandResponseDTO() {
        this.output = new ArrayList<>();
        this.timestamp = System.currentTimeMillis();
    }
    
    public CommandResponseDTO(boolean success, List<String> output, String errorMessage) {
        this.success = success;
        this.output = output != null ? new ArrayList<>(output) : new ArrayList<>();
        this.errorMessage = errorMessage;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static CommandResponseDTO success(List<String> output) {
        return new CommandResponseDTO(true, output, null);
    }
    
    public static CommandResponseDTO success(String output) {
        List<String> outputList = new ArrayList<>();
        if (output != null && !output.isEmpty()) outputList.add(output);
        return new CommandResponseDTO(true, outputList, null);
    }
    
    public static CommandResponseDTO error(String errorMessage) {
        return new CommandResponseDTO(false, new ArrayList<>(), errorMessage);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public List<String> getOutput() {
        return new ArrayList<>(output);
    }
    
    public void setOutput(List<String> output) {
        this.output = output != null ? new ArrayList<>(output) : new ArrayList<>();
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getOutputAsString() {
        return String.join("\n", output);
    }
}
