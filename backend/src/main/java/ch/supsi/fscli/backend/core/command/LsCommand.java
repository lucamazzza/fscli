package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.List;

public class LsCommand extends AbstractCommand {

    public LsCommand() {
        super("ls", "List directory contents", "ls [-i] [directory]");
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
                return CommandResult.error("ls: invalid option: " + arg);
            }
        }

        List<String> entries = fs.ls(path, showInodes);
        return CommandResult.success(entries);
    }
}
