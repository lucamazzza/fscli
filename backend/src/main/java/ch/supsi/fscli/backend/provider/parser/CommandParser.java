package ch.supsi.fscli.backend.provider.parser;

import ch.supsi.fscli.backend.core.exception.InvalidCommandException;

import java.util.List;

public class CommandParser {
    private final CommandLexer lexer;
    
    public CommandParser() {
        this.lexer = new CommandLexer();
    }
    
    public CommandSyntax parse(String commandString) throws InvalidCommandException {
        List<String> tokens = lexer.tokenize(commandString);
        
        if (tokens.isEmpty()) {
            throw new InvalidCommandException("No command specified");
        }
        
        String commandName = tokens.get(0);
        
        if (!lexer.validateCommandName(commandName)) {
            throw new InvalidCommandException("Invalid command name: " + commandName);
        }
        
        List<String> arguments = tokens.subList(1, tokens.size());
        
        return new CommandSyntax(commandName, arguments);
    }
}
