package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.core.command.*;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;
import ch.supsi.fscli.backend.provider.executor.CommandExecutor;
import ch.supsi.fscli.backend.controller.CommandResponse;
import ch.supsi.fscli.backend.provider.parser.CommandParser;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages_backend");

    private CommandExecutor executor;
    private final List<CommandHistoryEntry> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 1000;
    private final Set<Command> commands;

    private FileSystem fileSystem;

    @Inject
    public FileSystemService(Set<Command> commands) {
        this.commands = commands;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setFileSystem(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.executor = new CommandExecutor(fileSystem, new CommandParser());
        registerCommands();
    }

    public void createNewFileSystem() {
        this.fileSystem = new InMemoryFileSystem();
        this.executor = new CommandExecutor(fileSystem, new CommandParser());
        registerCommands();
        history.clear();
    }

    private void registerCommands() {
        for (Command command : commands) {
            executor.registerCommand(command);
        }
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
            return new CommandResponse(false, null, BackendMessageProvider.get("commandEmpty"));
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
        if (command == null) return commandName + ": " + BackendMessageProvider.get("unknownCommand");
        String descKey = commandName + ".description";
        String usageKey = commandName + ".usage";
        String desc = MESSAGES.containsKey(descKey) ? BackendMessageProvider.get(descKey) : "";
        String usage = MESSAGES.containsKey(usageKey) ? BackendMessageProvider.get(usageKey) : "";
        return String.format("%s\nUsage: %s", desc, usage);
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

    public String getMessage(String key) {
        return MESSAGES.containsKey(key) ? BackendMessageProvider.get(key) : key;
    }
}
