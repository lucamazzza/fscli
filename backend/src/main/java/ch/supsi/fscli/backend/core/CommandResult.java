package ch.supsi.fscli.backend.core;

import java.util.ArrayList;
import java.util.List;

public class CommandResult {
    private final boolean success;
    private final List<String> output;
    private final String errorMessage;
    
    private CommandResult(boolean success, List<String> output, String errorMessage) {
        this.success = success;
        this.output = output != null ? new ArrayList<>(output) : new ArrayList<>();
        this.errorMessage = errorMessage;
    }
    
    public static CommandResult success(List<String> output) {
        return new CommandResult(true, output, null);
    }
    
    public static CommandResult success(String output) {
        List<String> lines = new ArrayList<>();
        if (output != null && !output.isEmpty()) {
            lines.add(output);
        }
        return new CommandResult(true, lines, null);
    }
    
    public static CommandResult success() {
        return new CommandResult(true, new ArrayList<>(), null);
    }
    
    public static CommandResult error(String errorMessage) {
        return new CommandResult(false, new ArrayList<>(), errorMessage);
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
