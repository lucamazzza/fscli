package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

import java.util.ArrayList;
import java.util.List;

public class LsCommand extends AbstractCommand {

    public LsCommand() {
        super("ls", "List directory contents", "ls [-i] [directory]");
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
                return CommandResult.error("ls: invalid option: " + arg);
            }
        }
        
        // If no paths specified, list current directory
        if (paths.isEmpty()) {
            paths.add(".");
        }
        
        List<String> allEntries = new ArrayList<>();
        
        // If multiple paths, show each directory with a header
        if (paths.size() > 1) {
            for (int i = 0; i < paths.size(); i++) {
                String path = paths.get(i);
                if (i > 0) {
                    allEntries.add("");  // Blank line between directories
                }
                allEntries.add(path + ":");
                List<String> entries = fs.ls(path, showInodes);
                allEntries.addAll(entries);
            }
        } else {
            // Single path, just list the contents
            List<String> entries = fs.ls(paths.get(0), showInodes);
            allEntries.addAll(entries);
        }

        return CommandResult.success(allEntries);
    }
}
