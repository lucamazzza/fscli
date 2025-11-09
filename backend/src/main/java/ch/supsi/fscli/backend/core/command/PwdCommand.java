package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class PwdCommand extends AbstractCommand {
    
    public PwdCommand() {
        super("pwd", "Print working directory", "pwd");
    }
    
    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) {
        return CommandResult.success(fs.pwd());
    }
}
