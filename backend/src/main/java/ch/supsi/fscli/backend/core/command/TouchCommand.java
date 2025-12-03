package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class TouchCommand extends AbstractCommand {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public TouchCommand() {
        super("touch",
                MESSAGES.getString("touch.description"),
                MESSAGES.getString("touch.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error(MESSAGES.getString("touch.error.missingOperand"));
        }

        for (String path : syntax.getArguments()) {
            fs.touch(path);
        }

        return CommandResult.success();
    }
}
