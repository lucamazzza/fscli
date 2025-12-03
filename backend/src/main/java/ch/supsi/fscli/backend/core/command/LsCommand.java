package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LsCommand extends AbstractCommand {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public LsCommand() {
        super("ls",
                MESSAGES.getString("ls.description"),
                MESSAGES.getString("ls.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        boolean showInodes = false;
        String path = ".";

        for (String arg : syntax.getArguments()) {
            if (arg.equals("-i")) {
                showInodes = true;
            } else if (!arg.startsWith("-")) {
                path = arg;
            } else {
                return CommandResult.error(MESSAGES.getString("ls.error.invalidOption") + ": " + arg);
            }
        }

        List<String> entries = fs.ls(path, showInodes);
        return CommandResult.success(entries);
    }
}
