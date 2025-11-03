package ch.supsi.fscli.backend.core;

import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public interface Command {
    CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException;
    String getName();
    String getDescription();
    String getUsage();
}
