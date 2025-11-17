package ch.supsi.fscli.backend.core;

import ch.supsi.fscli.backend.core.exception.FSException;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;

import java.util.List;

public interface FileSystem {

    /**
     * Create a directory at the specified path.
     */
    void mkdir(String path) throws FSException;

    /**
     * Remove an empty directory.
     */
    void rmdir(String path) throws FSException;

    /**
     * Create or update a file.
     */
    void touch(String path) throws FSException;

    /**
     * Remove a file.
     */
    void rm(String path) throws FSException;

    /**
     * Move or rename a file/directory.
     */
    void mv(String src, String dest) throws FSException;

    /**
     * Create a link (hard or symbolic).
     */
    void ln(String target, String link, boolean sym) throws FSException;

    /**
     * List directory contents.
     */
    List<String> ls(String path, boolean showI) throws FSException;

    /**
     * Change current working directory.
     */
    void cd(String path) throws FSException;

    /**
     * Print working directory.
     */
    String pwd();

    /**
     * Copy a file or directory.
     */
    void cp(String src, String dest) throws FSException;
    
    /**
     * Expand wildcards in path.
     */
    List<String> expWildcard(String path, DirectoryNode curDir) throws FSException;

    /**
     * Resolve a path to a node.
     * External commands can use this for custom operations.
     *
     * @param path The path to resolve (relative or absolute)
     * @param followSymlinks Whether to follow symbolic links
     * @return The resolved node
     * @throws FSException if path doesn't exist or other error
     */
    FileSystemNode resolveNode(String path, boolean followSymlinks) throws FSException;
    
    /**
     * Create a node at the specified path.
     * External commands can use this to add custom node types.
     * 
     * @param path The path where to create the node
     * @param node The node to create
     * @throws FSException if parent doesn't exist or path already exists
     */
    void createNode(String path, FileSystemNode node) throws FSException;
    
    /**
     * Delete a node at the specified path.
     * External commands can use this for custom deletion logic.
     * 
     * @param path The path of the node to delete
     * @throws FSException if path doesn't exist or other error
     */
    void deleteNode(String path) throws FSException;
    
    /**
     * Copy a node (file, directory, symlink).
     * Used internally by cp command, can be used by external commands.
     * 
     * @param fs The filesystem
     * @param node The node to copy
     * @param recursive Whether to copy directories recursively
     * @return The copied node
     */
    FileSystemNode copyNode(FileSystem fs, FileSystemNode node, boolean recursive);
    
    /**
     * Get the parent directory of a path.
     * 
     * @param path The path to get parent for
     * @return The parent directory node
     * @throws FSException if parent doesn't exist
     */
    DirectoryNode getParentDirectory(String path) throws FSException;
    
    /**
     * Get current working directory.
     * 
     * @return The current working directory node
     */
    DirectoryNode getCwd();
    
    /**
     * Get root directory.
     * 
     * @return The root directory node
     */
    DirectoryNode getRoot();
    
    /**
     * List all child nodes of a directory.
     * 
     * @param directory The directory to list
     * @return List of child nodes
     */
    List<FileSystemNode> listNodes(DirectoryNode directory);
    
    /**
     * Extract filename from a path.
     * Utility method for external commands.
     * 
     * @param path The path
     * @return The filename
     */
    String extractFileName(String path);
    
    /**
     * Extract parent path from a path.
     * Utility method for external commands.
     * 
     * @param path The path
     * @return The parent path
     */
    String extractParentPath(String path);
}
