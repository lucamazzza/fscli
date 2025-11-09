package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public class RmdirCommand extends AbstractCommand {
    
    public RmdirCommand() {
        super("rmdir", "Remove empty directories", "rmdir <directory>...");
    }
    
    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        if (syntax.getArgumentCount() == 0) {
            return CommandResult.error("rmdir: missing operand");
        }
        
        for (String path : syntax.getArguments()) {
            fs.rmdir(path);
        }
        
        return CommandResult.success();
    }
}
