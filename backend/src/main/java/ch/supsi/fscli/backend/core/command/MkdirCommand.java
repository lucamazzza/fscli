package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class MkdirCommand extends AbstractCommand {

    public MkdirCommand() {
        super("mkdir", "Make directories", "mkdir <directory>...");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error("mkdir: missing operand");
        }

        for (String path : syntax.getArguments()) {
            fs.mkdir(path);
        }

        return CommandResult.success();
    }
}
