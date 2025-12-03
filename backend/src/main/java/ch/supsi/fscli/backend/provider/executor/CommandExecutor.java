package ch.supsi.fscli.backend.provider.executor;

import ch.supsi.fscli.backend.core.command.Command;
import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.core.exception.InvalidCommandException;
import ch.supsi.fscli.backend.provider.parser.CommandParser;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class CommandExecutor {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());


    private final Map<String, Command> commands;
    private final CommandParser parser;
    private final FileSystem fileSystem;

    public CommandExecutor(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        this.parser = new CommandParser();
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
                return CommandResult.error(
                        MESSAGES.getString("unknownCommand") + ": " + parsedCommand.getCommandName());
            }

            return command.execute(fileSystem, parsedCommand);

        } catch (InvalidCommandException e) {
            return CommandResult.error(
                    MESSAGES.getString("invalidCommand") + ": " + e.getMessage());
        } catch (FSException e) {
            return CommandResult.error(e.getMessage());
        } catch (Exception e) {
            return CommandResult.error(
                    MESSAGES.getString("executionError") + ": " + e.getMessage());
        }
    }

    public Map<String, Command> getAvailableCommands() {
        return new HashMap<>(commands);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }
}
