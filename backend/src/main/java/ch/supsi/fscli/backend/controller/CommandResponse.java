package ch.supsi.fscli.backend.controller;

import java.util.ArrayList;
import java.util.List;

public class CommandResponse {
    private final boolean success;
    private final List<String> output;
    private final String errorMessage;
    
    public CommandResponse(boolean success, List<String> output, String errorMessage) {
        this.success = success;
        this.output = output != null ? new ArrayList<>(output) : new ArrayList<>();
        this.errorMessage = errorMessage;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public List<String> getOutput() {
        return new ArrayList<>(output);
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public String getOutputAsString() {
        return String.join("\n", output);
    }
}
