package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class MvCommand extends AbstractCommand {

    public MvCommand() {
        super("mv", "Move/rename file or directory", "mv <source> <destination>");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() < 2) {
            return CommandResult.error("mv: missing operand");
        }
        if (syntax.getArgumentCount() > 2) {
            return CommandResult.error("mv: too many arguments");
        }

        fs.mv(syntax.getArgument(0), syntax.getArgument(1));
        return CommandResult.success();
    }
}
