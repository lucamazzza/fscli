package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class MvCommand extends AbstractCommand {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public MvCommand() {
        super("mv",
                MESSAGES.getString("mv.description"),
                MESSAGES.getString("mv.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() < 2) {
            return CommandResult.error(MESSAGES.getString("mv.error.missingOperand"));
        }
        if (syntax.getArgumentCount() > 2) {
            return CommandResult.error(MESSAGES.getString("mv.error.tooManyArguments"));
        }

        fs.mv(syntax.getArgument(0), syntax.getArgument(1));
        return CommandResult.success();
    }
}
