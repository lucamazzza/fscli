package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class LnCommand extends AbstractCommand {


    public LnCommand() {
        super("ln",
                BackendMessageProvider.get("ln.description"),
                BackendMessageProvider.get("ln.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        boolean symbolic = false;
        int argIndex = 0;

        if (syntax.getArgumentCount() > 0 && syntax.getArgument(0).equals("-s")) {
            symbolic = true;
            argIndex = 1;
        }

        if (syntax.getArgumentCount() - argIndex < 2) {
            return CommandResult.error(BackendMessageProvider.get("ln.error.missingOperand"));
        }
        if (syntax.getArgumentCount() - argIndex > 2) {
            return CommandResult.error(BackendMessageProvider.get("ln.error.tooManyArguments"));
        }

        String target = syntax.getArgument(argIndex);
        String link = syntax.getArgument(argIndex + 1);

        fs.ln(target, link, symbolic);
        return CommandResult.success();
    }
}
