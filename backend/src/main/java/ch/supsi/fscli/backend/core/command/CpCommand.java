package ch.supsi.fscli.backend.core.command;

import ch.supsi.fscli.backend.core.CommandResult;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.SymlinkNode;
import ch.supsi.fscli.backend.provider.parser.CommandSyntax;

/**
 * Example of an EXTERNAL command that uses low-level FileSystem API.
 *
 * This demonstrates how external developers can add commands without
 * modifying the FileSystem interface or InMemoryFileSystem class.
 *
 * The 'cp' command copies files and directories.
 */
public class CpCommand extends AbstractCommand {

    public CpCommand() {
        super("cp", "Copy files and directories", "cp [-r] <source> <destination>");
    }

    @Override
    public CommandResult execute(FileSystem fs, CommandSyntax syntax) throws FSException {
        boolean recursive = false;
        int argStart = 0;

        // Parse -r flag
        if (syntax.getArgumentCount() > 0 && syntax.getArgument(0).equals("-r")) {
            recursive = true;
            argStart = 1;
        }

        // Validate arguments
        if (syntax.getArgumentCount() - argStart < 2) {
            return CommandResult.error("cp: missing operand");
        }
        if (syntax.getArgumentCount() - argStart > 2) {
            return CommandResult.error("cp: too many arguments");
        }

        String src = syntax.getArgument(argStart);
        String dest = syntax.getArgument(argStart + 1);

        // Use low-level API to implement copy logic
        try {
            FileSystemNode srcNode = fs.resolveNode(src, true);

            if (srcNode.isDirectory() && !recursive) {
                return CommandResult.error("cp: -r not specified; omitting directory '" + src + "'");
            }

            // Create copy of the node
            FileSystemNode copy = copyNode(fs, srcNode, recursive);

            // Create the copy at destination using low-level API
            fs.createNode(dest, copy);

            return CommandResult.success();

        } catch (FSException e) {
            return CommandResult.error("cp: " + e.getMessage());
        }
    }

    /**
     * Creates a deep copy of a node.
     * This is custom logic that doesn't require FileSystem modification.
     */
    private FileSystemNode copyNode(FileSystem fs, FileSystemNode node, boolean recursive) {
        if (!node.isDirectory() && !node.isSymlink()) {
            // Copy file (in real implementation, would copy content too)
            return new FileNode();

        } else if (node.isSymlink()) {
            // Copy symlink
            SymlinkNode symlink = (SymlinkNode) node;
            return new SymlinkNode(symlink.getTarget());

        } else if (node.isDirectory() && recursive) {
            // Copy directory recursively
            DirectoryNode srcDir = (DirectoryNode) node;
            DirectoryNode destDir = new DirectoryNode();

            // Copy all children using low-level API
            for (FileSystemNode child : fs.listNodes(srcDir)) {
                FileSystemNode childCopy = copyNode(fs, child, true);
                String childName = findChildName(srcDir, child);
                destDir.add(childName, childCopy);
            }

            return destDir;

        } else {
            // Directory without -r flag
            return new DirectoryNode();
        }
    }

    /**
     * Find the name of a child in its parent directory.
     * Helper method for traversing the tree.
     */
    private String findChildName(DirectoryNode parent, FileSystemNode child) {
        for (String name : parent.listNames()) {
            if (parent.get(name) == child) {
                return name;
            }
        }
        return null;
    }
}
