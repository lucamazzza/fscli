package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LsCommand extends AbstractCommand {


    public LsCommand() {
        super("ls",
                BackendMessageProvider.get("ls.description"),
                BackendMessageProvider.get("ls.usage"));
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        boolean showInodes = false;
        List<String> paths = new ArrayList<>();
        for (String arg : syntax.getArguments()) {
            if (arg.equals("-i")) {
                showInodes = true;
            } else if (!arg.startsWith("-")) {
                paths.add(arg);
            } else {
                return CommandResult.error(BackendMessageProvider.get("ls.error.invalidOption") + ": " + arg);
            }
        }
        if (paths.isEmpty()) {
            paths.add(".");
        }
        List<String> allEntries = new ArrayList<>();
        if (paths.size() > 1) {
            for (int i = 0; i < paths.size(); i++) {
                String path = paths.get(i);
                if (i > 0) {
                    allEntries.add("");
                }
                allEntries.add(path + ":");
                List<String> entries = fs.ls(path, showInodes);
                allEntries.addAll(entries);
            }
        } else {
            List<String> entries = fs.ls(paths.get(0), showInodes);
            allEntries.addAll(entries);
        }
        return CommandResult.success(allEntries);
    }
}
