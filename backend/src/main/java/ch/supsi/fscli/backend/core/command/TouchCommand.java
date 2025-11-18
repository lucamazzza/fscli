package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class TouchCommand extends AbstractCommand {

    public TouchCommand() {
        super("touch", "Create empty file or update timestamp", "touch <file>...");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error("touch: missing file operand");
        }

        for (String path : syntax.getArguments()) {
            fs.touch(path);
        }

        return CommandResult.success();
    }
}
