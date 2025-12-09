package ch.supsi.fscli.backend.provider.parser;

import java.util.List;

public class CommandSyntax {
    private final String commandName;
    private final List<String> arguments;
    
    public CommandSyntax(String commandName, List<String> arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }
    
    public String getCommandName() {
        return commandName;
    }
    
    public List<String> getArguments() {
        return arguments;
    }
    
    public int getArgumentCount() {
        return arguments.size();
    }
    
    public String getArgument(int index) {
        if (index < 0 || index >= arguments.size()) {
            return null;
        }
        return arguments.get(index);
    }
    
    public boolean hasArguments() {
        return !arguments.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("CommandSyntax{command='%s', args=%s}", commandName, arguments);
    }
}
