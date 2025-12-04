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
    
    /**
     * Returns whether this command should expand wildcards in its arguments.
     * By default, returns true. Commands that should not expand wildcards
     * (like cd, mkdir, pwd) should override this to return false.
     */
    default boolean shouldExpandWildcards() {
        return true;
    }
    
    /**
     * Returns whether a specific argument at the given index should be expanded.
     * This allows commands to selectively expand certain arguments (e.g., source but not destination).
     * By default, expands all arguments if shouldExpandWildcards() returns true.
     * 
     * @param index The index of the argument (0-based, excluding flags)
     * @param totalArgs The total number of non-flag arguments
     * @return true if the argument should be expanded, false otherwise
     */
    default boolean shouldExpandArgument(int index, int totalArgs) {
        return shouldExpandWildcards();
    }
}
