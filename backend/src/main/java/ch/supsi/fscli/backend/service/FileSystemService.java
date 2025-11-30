package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.command.*;
import ch.supsi.fscli.backend.provider.executor.CommandExecutor;
import ch.supsi.fscli.backend.controller.CommandResponse;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for filesystem command execution.
 * This is the main interface between the frontend and backend.
 * <p>
 * Architecture: Frontend → Controller → Service → Provider → Core → Data
 * <p>
 * The frontend should:
 * 1. Create a FileSystemService instance
 * 2. Call executeCommand() with command strings from user input
 * 3. Handle CommandResponse to display output or errors
 */
public class FileSystemService {
    private CommandExecutor executor;
    private List<CommandHistoryEntry> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 1000;

    @Getter
    private FileSystem fileSystem;

    public FileSystemService() {
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.executor = new CommandExecutor(fileSystem);
        registerCommands();
    }

    public void createNewFileSystem() {
        this.fileSystem = new ch.supsi.fscli.backend.core.InMemoryFileSystem();
        this.executor = new CommandExecutor(fileSystem);
        registerCommands();
        history.clear();
    }

    private void registerCommands() {
        executor.registerCommand(new CpCommand());
        executor.registerCommand(new MvCommand());
        executor.registerCommand(new RmCommand());
        executor.registerCommand(new TouchCommand());
        executor.registerCommand(new LsCommand());
        executor.registerCommand(new CdCommand());
        executor.registerCommand(new MkdirCommand());
        executor.registerCommand(new RmdirCommand());
        executor.registerCommand(new PwdCommand());
        executor.registerCommand(new LnCommand());
    }
    
    /**
     * Execute a command string and return the response.
     * The command is automatically added to history.
     * 
     * @param commandString The command to execute (e.g., "ls -i /home")
     * @return CommandResponse containing success status, output, or error message
     */
    public CommandResponse executeCommand(String commandString) {
        CommandResponse response = executeInternal(commandString);
        addToHistory(commandString, response);
        return response;
    }
    
    /**
     * Execute a command without adding it to history.
     * Useful for automated/background commands.
     */
    public CommandResponse executeCommandSilent(String commandString) {
        return executeInternal(commandString);
    }

    private CommandResponse executeInternal(String commandString) {
        if (commandString == null || commandString.trim().isEmpty()) {
            return new CommandResponse(false, null, "Empty command");
        }
        CommandResult result = executor.execute(commandString);
        return new CommandResponse(
            result.isSuccess(),
            result.getOutput(),
            result.getErrorMessage()
        );
    }
    
    /**
     * Get list of all available commands.
     * 
     * @return Array of command names
     */
    public String[] getAvailableCommands() {
        return executor.getAvailableCommands().keySet().toArray(new String[0]);
    }
    
    /**
     * Get help text for a specific command.
     * 
     * @param commandName Name of the command
     * @return Help text with description and usage
     */
    public String getCommandHelp(String commandName) {
        Command command = executor.getCommand(commandName);
        if (command == null) return commandName + ": command not found";
        return String.format("Usage: %s", command.getUsage());
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

    public boolean isFileSystemLoaded() {
        return fileSystem != null && executor != null;
    }

    public String getCurrentDirectory() {
        if (!isFileSystemLoaded()) {
            return "/";
        }
        return fileSystem.pwd();
    }
}
