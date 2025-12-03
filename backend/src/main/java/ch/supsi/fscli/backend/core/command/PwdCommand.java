package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.Locale;
import java.util.ResourceBundle;

public class PwdCommand extends AbstractCommand {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public PwdCommand() {
        super("pwd",
                MESSAGES.getString("pwd.description"),
                MESSAGES.getString("pwd.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) {
        return CommandResult.success(fs.pwd());
    }
}
