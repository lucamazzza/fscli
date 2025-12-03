package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class MkdirCommand extends AbstractCommand {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public MkdirCommand() {
        super("mkdir",
                MESSAGES.getString("mkdir.description"),
                MESSAGES.getString("mkdir.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error(MESSAGES.getString("mkdir.error.missingOperand"));
        }

        for (String path : syntax.getArguments()) {
            fs.mkdir(path);
        }

        return CommandResult.success();
    }
}
