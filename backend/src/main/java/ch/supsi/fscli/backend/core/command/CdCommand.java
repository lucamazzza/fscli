package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class CdCommand extends AbstractCommand {

    public CdCommand() {
        super("cd", "Change directory", "cd <directory>");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error("cd: missing operand");
        }
        if (syntax.getArgumentCount() > 1) {
            return CommandResult.error("cd: too many arguments");
        }

        fs.cd(syntax.getArgument(0));
        return CommandResult.success();
    }
}
