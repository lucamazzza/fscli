package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.util.Locale;
import java.util.ResourceBundle;

public class RmdirCommand extends AbstractCommand {


    public RmdirCommand() {
        super("rmdir",
                BackendMessageProvider.get("rmdir.description"),
                BackendMessageProvider.get("rmdir.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error(BackendMessageProvider.get("rmdir.error.missingOperand"));
        }

        for (String path : syntax.getArguments()) {
            fs.rmdir(path);
        }

        return CommandResult.success();
    }
}
