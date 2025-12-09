package ch.supsi.fscli.backend.controller.dto;

public class CommandRequest {
    private String command;
    private boolean addToHistory;
    
    public CommandRequest() {
        this.addToHistory = true;
    }
    
    public CommandRequest(String command) {
        this.command = command;
        this.addToHistory = true;
    }
    
    public CommandRequest(String command, boolean addToHistory) {
        this.command = command;
        this.addToHistory = addToHistory;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public boolean isAddToHistory() {
        return addToHistory;
    }
    
    public void setAddToHistory(boolean addToHistory) {
        this.addToHistory = addToHistory;
    }
}
