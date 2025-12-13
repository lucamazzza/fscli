package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.util.Locale;
import java.util.ResourceBundle;

public class MvCommand extends AbstractCommand {

    public MvCommand() {
        super("mv",
                BackendMessageProvider.get("mv.description"),
                BackendMessageProvider.get("mv.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() < 2) {
            return CommandResult.error(BackendMessageProvider.get("mv.error.missingOperand"));
        }
        if (syntax.getArgumentCount() > 2) {
            return CommandResult.error(BackendMessageProvider.get("mv.error.tooManyArguments"));
        }

        fs.mv(syntax.getArgument(0), syntax.getArgument(1));
        return CommandResult.success();
    }
    
    @Override
    public boolean shouldExpandArgument(int index, int totalArgs) {
        // Only expand the first argument (source), not the last (destination)
        return index < totalArgs - 1;
    }
}
