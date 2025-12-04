package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class CpCommand extends AbstractCommand {

    public CpCommand() {
        super("cp", "Copy files and directories", "cp [-r] <source> <destination>");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        boolean recursive = false;
        int argStart = 0;
        if (syntax.getArgumentCount() > 0 && syntax.getArgument(0).equals("-r")) {
            recursive = true;
            argStart = 1;
        }
        if (syntax.getArgumentCount() - argStart < 2) {
            return CommandResult.error("cp: missing operand");
        }
        if (syntax.getArgumentCount() - argStart > 2) {
            return CommandResult.error("cp: too many arguments");
        }
        String src = syntax.getArgument(argStart);
        String dest = syntax.getArgument(argStart + 1);
        fs.cp(src, dest);
        return CommandResult.success();
    }
    
    @Override
    public boolean shouldExpandArgument(int index, int totalArgs) {
        // Only expand source arguments, not the last (destination)
        return index < totalArgs - 1;
    }
}
