package ch.supsi.fscli.backend.controller.dto;

public class CommandHistoryDTO {
    private String command;
    private boolean successful;
    private long timestamp;
    
    public CommandHistoryDTO() {}
    
    public CommandHistoryDTO(String command, boolean successful, long timestamp) {
        this.command = command;
        this.successful = successful;
        this.timestamp = timestamp;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
