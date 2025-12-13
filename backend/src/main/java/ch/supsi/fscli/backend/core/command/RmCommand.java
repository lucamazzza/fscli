package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.util.Locale;
import java.util.ResourceBundle;

public class RmCommand extends AbstractCommand {

    public RmCommand() {
        super("rm",
                BackendMessageProvider.get("rm.description"),
                BackendMessageProvider.get("rm.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error(BackendMessageProvider.get("rm.error.missingOperand"));
        }

        for (String path : syntax.getArguments()) {
            fs.rm(path);
        }

        return CommandResult.success();
    }
}
