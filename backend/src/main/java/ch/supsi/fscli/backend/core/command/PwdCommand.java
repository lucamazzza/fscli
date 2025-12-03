package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.util.Locale;
import java.util.ResourceBundle;

public class PwdCommand extends AbstractCommand {


    public PwdCommand() {
        super("pwd",
                BackendMessageProvider.get("pwd.description"),
                BackendMessageProvider.get("pwd.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) {
        return CommandResult.success(fs.pwd());
    }
}
