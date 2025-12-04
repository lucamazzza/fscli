package ch.supsi.fscli.backend.provider.executor;

import ch.supsi.fscli.backend.core.command.Command;
import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.core.exception.InvalidCommandException;
import ch.supsi.fscli.backend.provider.parser.CommandParser;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutor {
    private final Map<String, Command> commands;
    private final CommandParser parser;
    private final FileSystem fileSystem;
    
    public CommandExecutor(FileSystem fileSystem, CommandParser parser) {
        this.fileSystem = fileSystem;
        this.parser = parser;
        this.commands = new HashMap<>();
    }
    
    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }
    
    public CommandResult execute(String commandString) {
        try {
            CommandSyntax parsedCommand = parser.parse(commandString);
            
            Command command = commands.get(parsedCommand.getCommandName());
            if (command == null) {
                return CommandResult.error("Unknown command: " + parsedCommand.getCommandName());
            }
            
            // Expand wildcards in arguments based on command's policy
            CommandSyntax expandedCommand = command.shouldExpandWildcards()
                ? expandWildcards(parsedCommand, command)
                : parsedCommand;
            
            return command.execute(fileSystem, expandedCommand);
            
        } catch (InvalidCommandException e) {
            return CommandResult.error("Invalid command: " + e.getMessage());
        } catch (FSException e) {
            return CommandResult.error(e.getMessage());
        } catch (Exception e) {
            return CommandResult.error("Error executing command: " + e.getMessage());
        }
    }
    
    public Map<String, Command> getAvailableCommands() {
        return new HashMap<>(commands);
    }
    
    public Command getCommand(String name) {
        return commands.get(name);
    }
    
    private CommandSyntax expandWildcards(CommandSyntax syntax, Command command) throws FSException {
        String commandName = syntax.getCommandName();
        List<String> originalArgs = syntax.getArguments();
        List<String> expandedArgs = new ArrayList<>();
        
        // Separate flags from non-flag arguments
        List<String> flags = new ArrayList<>();
        List<String> nonFlagArgs = new ArrayList<>();
        
        for (String arg : originalArgs) {
            if (arg.startsWith("-")) {
                flags.add(arg);
            } else {
                nonFlagArgs.add(arg);
            }
        }
        
        // Expand non-flag arguments based on command's policy
        List<String> expandedNonFlagArgs = new ArrayList<>();
        for (int i = 0; i < nonFlagArgs.size(); i++) {
            String arg = nonFlagArgs.get(i);
            if (command.shouldExpandArgument(i, nonFlagArgs.size())) {
                List<String> expanded = fileSystem.expWildcard(arg, fileSystem.getCwd());
                expandedNonFlagArgs.addAll(expanded);
            } else {
                expandedNonFlagArgs.add(arg);
            }
        }
        
        // Reconstruct arguments with flags first, then expanded args
        expandedArgs.addAll(flags);
        expandedArgs.addAll(expandedNonFlagArgs);
        
        return new CommandSyntax(commandName, expandedArgs);
    }
}
