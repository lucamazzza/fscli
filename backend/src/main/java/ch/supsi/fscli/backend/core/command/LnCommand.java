package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class LnCommand extends AbstractCommand {

    public LnCommand() {
        super("ln", "Create link", "ln [-s] <target> <link>");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        boolean symbolic = false;
        int argIndex = 0;

        if (syntax.getArgumentCount() > 0 && syntax.getArgument(0).equals("-s")) {
            symbolic = true;
            argIndex = 1;
        }

        if (syntax.getArgumentCount() - argIndex < 2) {
            return CommandResult.error("ln: missing operand");
        }
        if (syntax.getArgumentCount() - argIndex > 2) {
            return CommandResult.error("ln: too many arguments");
        }

        String target = syntax.getArgument(argIndex);
        String link = syntax.getArgument(argIndex + 1);

        fs.ln(target, link, symbolic);
        return CommandResult.success();
    }
    
    @Override
    public boolean shouldExpandArgument(int index, int totalArgs) {
        // Only expand the first argument (target), not the last (link name)
        return index < totalArgs - 1;
    }
}
