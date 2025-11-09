package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.controller.CommandController;
import ch.supsi.fscli.backend.controller.CommandResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for filesystem command execution.
 * This is the main interface between the frontend and backend.
 * <p>
 * The frontend should:
 * 1. Create a FileSystemService instance with a FileSystem implementation
 * 2. Call executeCommand() with command strings from user input
 * 3. Handle CommandResponse to display output or errors
 */
public class FileSystemService {
    private final CommandController controller;
    private final List<CommandHistoryEntry> history;
    private static final int MAX_HISTORY_SIZE = 1000;
    
    public FileSystemService(FileSystem fileSystem) {
        this.controller = new CommandController(fileSystem);
        this.history = new ArrayList<>();
    }
    
    /**
     * Execute a command string and return the response.
     * The command is automatically added to history.
     * 
     * @param commandString The command to execute (e.g., "ls -i /home")
     * @return CommandResponse containing success status, output, or error message
     */
    public CommandResponse executeCommand(String commandString) {
        CommandResponse response = controller.executeCommand(commandString);
        addToHistory(commandString, response);
        return response;
    }
    
    /**
     * Execute a command without adding it to history.
     * Useful for automated/background commands.
     */
    public CommandResponse executeCommandSilent(String commandString) {
        return controller.executeCommand(commandString);
    }
    
    /**
     * Get list of all available commands.
     * 
     * @return Array of command names
     */
    public String[] getAvailableCommands() {
        return controller.getAvailableCommands();
    }
    
    /**
     * Get help text for a specific command.
     * 
     * @param commandName Name of the command
     * @return Help text with description and usage
     */
    public String getCommandHelp(String commandName) {
        return controller.getCommandHelp(commandName);
    }
    
    /**
     * Get all help text for all commands.
     * 
     * @return List of help strings for all commands
     */
    public List<String> getAllCommandsHelp() {
        List<String> helpTexts = new ArrayList<>();
        for (String cmd : getAvailableCommands()) {
            helpTexts.add(getCommandHelp(cmd));
        }
        return helpTexts;
    }
    
    /**
     * Get command history.
     * 
     * @return List of command history entries
     */
    public List<CommandHistoryEntry> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Get command history as strings only.
     * 
     * @return List of command strings from history
     */
    public List<String> getHistoryCommands() {
        return history.stream()
                .map(CommandHistoryEntry::getCommand)
                .toList();
    }
    
    /**
     * Clear command history.
     */
    public void clearHistory() {
        history.clear();
    }
    
    /**
     * Get last N commands from history.
     * 
     * @param count Number of commands to retrieve
     * @return List of last N command history entries
     */
    public List<CommandHistoryEntry> getLastCommands(int count) {
        int size = history.size();
        int start = Math.max(0, size - count);
        return new ArrayList<>(history.subList(start, size));
    }
    
    /**
     * Search history for commands matching a pattern.
     * 
     * @param pattern Pattern to search for (case-insensitive)
     * @return List of matching history entries
     */
    public List<CommandHistoryEntry> searchHistory(String pattern) {
        String lowerPattern = pattern.toLowerCase();
        return history.stream()
                .filter(entry -> entry.getCommand().toLowerCase().contains(lowerPattern))
                .toList();
    }
    
    private void addToHistory(String command, CommandResponse response) {
        CommandHistoryEntry entry = new CommandHistoryEntry(
                command, 
                response.isSuccess(), 
                System.currentTimeMillis()
        );
        history.add(entry);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0);
        }
    }
}
