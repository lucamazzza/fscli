package ch.supsi.fscli.backend.provider.parser;

import ch.supsi.fscli.backend.core.exception.InvalidCommandException;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CommandParser {
    private final CommandLexer lexer;
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public CommandParser() {
        this.lexer = new CommandLexer();
    }

    public CommandSyntax parse(String commandString) throws InvalidCommandException {
        List<String> tokens = lexer.tokenize(commandString);

        if (tokens.isEmpty()) {
            throw new InvalidCommandException(MESSAGES.getString("noCommandSpecified"));
        }

        String commandName = tokens.get(0);

        if (!lexer.validateCommandName(commandName)) {
            throw new InvalidCommandException(
                    MESSAGES.getString("invalidCommand") + ": " + commandName
            );
        }

        List<String> arguments = tokens.subList(1, tokens.size());

        return new CommandSyntax(commandName, arguments);
    }
}
