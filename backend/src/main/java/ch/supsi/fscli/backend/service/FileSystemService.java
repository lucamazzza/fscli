package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.command.*;
import ch.supsi.fscli.backend.provider.executor.CommandExecutor;
import ch.supsi.fscli.backend.controller.CommandResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Service layer for filesystem command execution.
 */
public class FileSystemService {
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");

    private CommandExecutor executor;
    private final List<CommandHistoryEntry> history = new ArrayList<>();
    private static final int MAX_HISTORY_SIZE = 1000;

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

    public CommandResponse executeCommand(String commandString) {
        CommandResponse response = executeInternal(commandString);
        addToHistory(commandString, response);
        return response;
    }

    public CommandResponse executeCommandSilent(String commandString) {
        return executeInternal(commandString);
    }

    private CommandResponse executeInternal(String commandString) {
        if (commandString == null || commandString.trim().isEmpty()) {
            return new CommandResponse(false, null, MESSAGES.getString("commandEmpty"));
        }
        CommandResult result = executor.execute(commandString);
        return new CommandResponse(
                result.isSuccess(),
                result.getOutput(),
                result.getErrorMessage()
        );
    }

    public String[] getAvailableCommands() {
        return executor.getAvailableCommands().keySet().toArray(new String[0]);
    }

    public String getCommandHelp(String commandName) {
        Command command = executor.getCommand(commandName);
        if (command == null) return commandName + ": " + MESSAGES.getString("unknownCommand");
        String descKey = commandName + ".description";
        String usageKey = commandName + ".usage";
        String desc = MESSAGES.containsKey(descKey) ? MESSAGES.getString(descKey) : "";
        String usage = MESSAGES.containsKey(usageKey) ? MESSAGES.getString(usageKey) : "";
        return String.format("%s\nUsage: %s", desc, usage);
    }

    public List<String> getAllCommandsHelp() {
        List<String> helpTexts = new ArrayList<>();
        for (String cmd : getAvailableCommands()) {
            helpTexts.add(getCommandHelp(cmd));
        }
        return helpTexts;
    }

    public List<CommandHistoryEntry> getHistory() {
        return new ArrayList<>(history);
    }

    public List<String> getHistoryCommands() {
        return history.stream()
                .map(CommandHistoryEntry::getCommand)
                .toList();
    }

    public void clearHistory() {
        history.clear();
    }

    public List<CommandHistoryEntry> getLastCommands(int count) {
        int size = history.size();
        int start = Math.max(0, size - count);
        return new ArrayList<>(history.subList(start, size));
    }

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
        return MESSAGES.containsKey(key) ? MESSAGES.getString(key) : key;
    }
}
