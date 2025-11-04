package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

public interface Command {
    CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException;
    String getName();
    String getDescription();
    String getUsage();
}
