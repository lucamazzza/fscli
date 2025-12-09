package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.util.Locale;
import java.util.ResourceBundle;

public class MkdirCommand extends AbstractCommand {


    public MkdirCommand() {
        super("mkdir",
                BackendMessageProvider.get("mkdir.description"),
                BackendMessageProvider.get("mkdir.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error(BackendMessageProvider.get("mkdir.error.missingOperand"));
        }

        for (String path : syntax.getArguments()) {
            fs.mkdir(path);
        }

        return CommandResult.success();
    }
    
    @Override
    public boolean shouldExpandWildcards() {
        return false;
    }
}
