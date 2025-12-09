package ch.supsi.fscli.backend.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CommandHistoryEntry {
    private final String command;
    private final boolean successful;
    private final long timestamp;
    
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
    
    public CommandHistoryEntry(String command, boolean successful, long timestamp) {
        this.command = command;
        this.successful = successful;
        this.timestamp = timestamp;
    }
    
    public String getCommand() {
        return command;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getFormattedTimestamp() {
        return FORMATTER.format(Instant.ofEpochMilli(timestamp));
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s %s", 
                getFormattedTimestamp(), 
                successful ? "✓" : "✗", 
                command);
    }
}
