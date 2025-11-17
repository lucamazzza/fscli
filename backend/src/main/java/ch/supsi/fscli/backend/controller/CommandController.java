package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.command.*;
import ch.supsi.fscli.backend.provider.executor.CommandExecutor;

public class CommandController {
    private final CommandExecutor executor;
    
    public CommandController(FileSystem fileSystem) {
        this.executor = new CommandExecutor(fileSystem);
        registerCommands();
    }
    
    private void registerCommands() {
        executor.registerCommand(new LsCommand());
        executor.registerCommand(new CdCommand());
        executor.registerCommand(new MkdirCommand());
        executor.registerCommand(new RmdirCommand());
        executor.registerCommand(new PwdCommand());
    }
    
    public CommandResponse executeCommand(String commandString) {
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
    
    public String[] getAvailableCommands() {
        return executor.getAvailableCommands().keySet().toArray(new String[0]);
    }
    
    public String getCommandHelp(String commandName) {
        Command command = executor.getCommand(commandName);
        if (command == null) return commandName + ": command not found";
        return String.format("Usage: %s", command.getUsage());
    }
}
