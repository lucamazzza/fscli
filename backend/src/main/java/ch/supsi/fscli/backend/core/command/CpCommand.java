package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class CpCommand extends AbstractCommand {

    public CpCommand() {
        super("cp",
                BackendMessageProvider.get("cp.description"),
                BackendMessageProvider.get("cp.usage"));
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
            return CommandResult.error(BackendMessageProvider.get("cp.error.missingOperand"));
        }
        if (syntax.getArgumentCount() - argStart > 2) {
            return CommandResult.error(BackendMessageProvider.get("cp.error.tooManyArguments"));
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
