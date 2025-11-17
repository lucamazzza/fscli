package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class RmCommand extends AbstractCommand {

    public RmCommand() {
        super("rm", "Remove files", "rm <file>...");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error("rm: missing operand");
        }

        for (String path : syntax.getArguments()) {
            fs.rm(path);
        }

        return CommandResult.success();
    }
}
