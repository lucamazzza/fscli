package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class RmdirCommand extends AbstractCommand {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public RmdirCommand() {
        super("rmdir",
                MESSAGES.getString("rmdir.description"),
                MESSAGES.getString("rmdir.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error(MESSAGES.getString("rmdir.error.missingOperand"));
        }

        for (String path : syntax.getArguments()) {
            fs.rmdir(path);
        }

        return CommandResult.success();
    }
}
