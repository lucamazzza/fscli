package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.service.CommandHistoryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller specifically for history management operations.
 * Part of the Facade pattern implementation.
 */
public class HistoryController {
    private final List<CommandHistoryEntry> history;
    private static final int MAX_HISTORY_SIZE = 1000;
    
    public HistoryController() {
        this.history = new ArrayList<>();
    }
    
    /**
     * Add a command to history.
     * 
     * @param command Command string
     * @param success Whether the command was successful
     */
    public void addToHistory(String command, boolean success) {
        CommandHistoryEntry entry = new CommandHistoryEntry(
                command,
                success,
                System.currentTimeMillis()
        );
        history.add(entry);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }
    
    /**
     * Get command history.
     * 
     * @return List of command history DTOs
     */
    public List<CommandHistoryDTO> getHistory() {
        return history.stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get last N commands from history.
     * 
     * @param count Number of commands to retrieve
     * @return List of command history DTOs
     */
    public List<CommandHistoryDTO> getLastCommands(int count) {
        int size = history.size();
        int start = Math.max(0, size - count);
        return history.subList(start, size).stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Search command history.
     * 
     * @param pattern Search pattern
     * @return List of matching command history DTOs
     */
    public List<CommandHistoryDTO> searchHistory(String pattern) {
        String lowerPattern = pattern.toLowerCase();
        return history.stream()
                .filter(entry -> entry.getCommand().toLowerCase().contains(lowerPattern))
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Clear command history.
     */
    public void clearHistory() {
        history.clear();
    }
    
    /**
     * Get command history as simple string list.
     * 
     * @return List of command strings
     */
    public List<String> getHistoryCommands() {
        return history.stream()
                .map(CommandHistoryEntry::getCommand)
                .collect(Collectors.toList());
    }
    
    private CommandHistoryDTO convertHistoryToDTO(CommandHistoryEntry entry) {
        return new CommandHistoryDTO(
                entry.getCommand(),
                entry.isSuccessful(),
                entry.getTimestamp()
        );
    }
}
