package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class CdCommand extends AbstractCommand {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public CdCommand() {
        super("cd",
                MESSAGES.getString("cd.description"),
                MESSAGES.getString("cd.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error(MESSAGES.getString("cd.error.missingOperand"));
        }
        if (syntax.getArgumentCount() > 1) {
            return CommandResult.error(MESSAGES.getString("cd.error.tooManyArguments"));
        }

        fs.cd(syntax.getArgument(0));
        return CommandResult.success();
    }
}
