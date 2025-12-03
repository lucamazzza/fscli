package ch.supsi.fscli.backend.provider.parser;

import ch.supsi.fscli.backend.core.exception.InvalidCommandException;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CommandParser {
    private final CommandLexer lexer;

    public CommandParser() {
        this.lexer = new CommandLexer();
    }

    public CommandSyntax parse(String commandString) throws InvalidCommandException {
        List<String> tokens = lexer.tokenize(commandString);

        if (tokens.isEmpty()) {
            throw new InvalidCommandException(BackendMessageProvider.get("noCommandSpecified"));
        }

        String commandName = tokens.get(0);

        if (!lexer.validateCommandName(commandName)) {
            throw new InvalidCommandException(
                    BackendMessageProvider.get("invalidCommand") + ": " + commandName
            );
        }

        List<String> arguments = tokens.subList(1, tokens.size());

        return new CommandSyntax(commandName, arguments);
    }
}
