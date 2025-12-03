package ch.supsi.fscli.backend.core;

import ch.supsi.fscli.backend.data.LinkNode;
import ch.supsi.fscli.backend.provider.resolver.PathResolver;
import ch.supsi.fscli.backend.core.exception.*;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.FileNode;

import java.util.ArrayList;
import java.util.List;

public class InMemoryFileSystem implements FileSystem {
    private final DirectoryNode root;
    private DirectoryNode cwd;
    private final PathResolver pathResolver;
    
    public InMemoryFileSystem() {
        this.root = new DirectoryNode();
        this.root.setParent(root);
        this.cwd = root;
        this.pathResolver = PathResolver.getInstance();
    }
    
    public InMemoryFileSystem(DirectoryNode root) {
        this.root = root;
        this.root.setParent(root);
        this.cwd = root;
        this.pathResolver = PathResolver.getInstance();
    }
    
    @Override
    public void mkdir(String path) throws FSException {
        if (path == null || path.isEmpty()) {
            throw new InvalidPathException("Path cannot be empty");
        }
        try {
            FileSystemNode existing = pathResolver.resolve(cwd, path, false);
            throw new AlreadyExistsException("File or directory already exists: " + path);
        } catch (NotFoundException e) {
            // It doesn't exist, we can create it
        }
        String parentPath = getParentPath(path);
        String dirName = getFileName(path);
        DirectoryNode parent;
        if (parentPath.isEmpty()) {
            parent = cwd;
        } else {
            try {
                FileSystemNode parentNode = pathResolver.resolve(cwd, parentPath, true);
                if (!parentNode.isDirectory()) {
                    throw new NotADirectoryException("Parent is not a directory: " + parentPath);
                }
                parent = (DirectoryNode) parentNode;
            } catch (NotFoundException e) {
                throw new NotFoundException("Parent directory not found: " + parentPath);
            }
        }
        DirectoryNode newDir = new DirectoryNode();
        parent.add(dirName, newDir);
    }
    
    @Override
    public void rmdir(String path) throws FSException {
        FileSystemNode node = pathResolver.resolve(cwd, path, false);
        if (!node.isDirectory()) {
            throw new NotADirectoryException("Not a directory: " + path);
        }
        DirectoryNode dir = (DirectoryNode) node;
        if (!dir.isEmpty()) {
            throw new FSException("Directory not empty: " + path);
        }
        DirectoryNode parent = dir.getParent();
        if (parent == null || parent == dir) {
            throw new FSException("Cannot remove root directory");
        }
        String name = findNameInParent(parent, dir);
        if (name != null) {
            parent.remove(name);
        }
    }
    
    @Override
    public void touch(String path) throws FSException {
        try {
            FileSystemNode existing = pathResolver.resolve(cwd, path, false);
            existing.touch();
        } catch (NotFoundException e) {
            String parentPath = getParentPath(path);
            String fileName = getFileName(path);
            DirectoryNode parent;
            if (parentPath.isEmpty()) {
                parent = cwd;
            } else {
                FileSystemNode parentNode = pathResolver.resolve(cwd, parentPath, true);
                if (!parentNode.isDirectory()) {
                    throw new NotADirectoryException("Parent is not a directory: " + parentPath);
                }
                parent = (DirectoryNode) parentNode;
            }
            FileNode newFile = new FileNode();
            parent.add(fileName, newFile);
        }
    }
    
    @Override
    public void rm(String path) throws FSException {
        FileSystemNode node = pathResolver.resolve(cwd, path, false);
        if (node.isDirectory()) {
            throw new FSException("Cannot remove directory with rm, use rmdir: " + path);
        }
        DirectoryNode parent = node.getParent();
        if (parent == null) {
            throw new FSException("Cannot remove root");
        }
        String name = findNameInParent(parent, node);
        if (name != null) {
            parent.remove(name);
        }
    }
    
    @Override
    public void mv(String src, String dest) throws FSException {
        FileSystemNode srcNode = pathResolver.resolve(cwd, src, false);
        try {
            pathResolver.resolve(cwd, dest, false);
            throw new AlreadyExistsException("Destination already exists: " + dest);
        } catch (NotFoundException e) {
            // Good - destination doesn't exist
        }
        String destParentPath = getParentPath(dest);
        String destName = getFileName(dest);
        DirectoryNode destParent;
        if (destParentPath.isEmpty()) {
            destParent = cwd;
        } else {
            FileSystemNode destParentNode = pathResolver.resolve(cwd, destParentPath, true);
            if (!destParentNode.isDirectory()) {
                throw new NotADirectoryException("Destination parent is not a directory: " + destParentPath);
            }
            destParent = (DirectoryNode) destParentNode;
        }
        DirectoryNode srcParent = srcNode.getParent();
        if (srcParent == null) {
            throw new FSException("Cannot move root");
        }
        String srcName = findNameInParent(srcParent, srcNode);
        if (srcName != null) {
            srcParent.remove(srcName);
        }
        destParent.add(destName, srcNode);
    }
    
    @Override
    public void ln(String target, String link, boolean sym) throws FSException {
        try {
            pathResolver.resolve(cwd, link, false);
            throw new AlreadyExistsException("Link already exists: " + link);
        } catch (NotFoundException e) {
            // Good - link doesn't exist
        }
        if (sym) {
            String linkParentPath = getParentPath(link);
            String linkName = getFileName(link);
            DirectoryNode linkParent;
            if (linkParentPath.isEmpty()) {
                linkParent = cwd;
            } else {
                FileSystemNode linkParentNode = pathResolver.resolve(cwd, linkParentPath, true);
                if (!linkParentNode.isDirectory()) {
                    throw new NotADirectoryException("Link parent is not a directory: " + linkParentPath);
                }
                linkParent = (DirectoryNode) linkParentNode;
            }
            
            LinkNode symlink = new LinkNode(target);
            linkParent.add(linkName, symlink);
        } else {
            FileSystemNode targetNode = pathResolver.resolve(cwd, target, true);
            if (targetNode.isDirectory()) {
                throw new InvalidPathException("Target must not be a directory, for hard links: " + target);
            }
            String linkParentPath = getParentPath(link);
            String linkName = getFileName(link);
            DirectoryNode linkParent;
            if (linkParentPath.isEmpty()) {
                linkParent = cwd;
            } else {
                FileSystemNode linkParentNode = pathResolver.resolve(cwd, linkParentPath, true);
                if (!linkParentNode.isDirectory()) {
                    throw new NotADirectoryException("Link parent is not a directory: " + linkParentPath);
                }
                linkParent = (DirectoryNode) linkParentNode;
            }
            linkParent.add(linkName, targetNode);
            targetNode.incrementLinkCount();
        }
    }
    
    @Override
    public List<String> ls(String path, boolean showI) throws FSException {
        FileSystemNode node = pathResolver.resolve(cwd, path, true);
        if (!node.isDirectory()) {
            if (showI) {
                return List.of(node.getId() + " " + getFileName(path));
            } else {
                return List.of(getFileName(path));
            }
        }
        DirectoryNode dir = (DirectoryNode) node;
        List<String> result = new ArrayList<>();
        for (String name : dir.listNames()) {
            FileSystemNode child = dir.get(name);
            if (showI) {
                String prefix = child.getId() + " ";
                if (child.isDirectory()) {
                    result.add(prefix + name + "/");
                } else if (child.isLink()) {
                    LinkNode sym = (LinkNode) child;
                    result.add(prefix + name + " -> " + sym.getTarget());
                } else {
                    result.add(prefix + name);
                }
            } else {
                if (child.isDirectory()) {
                    result.add(name + "/");
                } else if (child.isLink()) {
                    LinkNode sym = (LinkNode) child;
                    result.add(name + " -> " + sym.getTarget());
                } else {
                    result.add(name);
                }
            }
        }
        return result;
    }
    
    @Override
    public void cd(String path) throws FSException {
        FileSystemNode node = pathResolver.resolve(cwd, path, true);
        if (!node.isDirectory()) {
            throw new NotADirectoryException("Not a directory: " + path);
        }
        cwd = (DirectoryNode) node;
    }
    
    @Override
    public String pwd() {
        return getAbsolutePath(cwd);
    }

    @Override
    public void cp(String src, String dest) throws FSException {
        FileSystemNode srcNode = pathResolver.resolve(cwd, src, true);
        try {
            pathResolver.resolve(cwd, dest, false);
            throw new AlreadyExistsException("Destination already exists: " + dest);
        } catch (NotFoundException e) {
            // Good - destination doesn't exist
        }
        String destParentPath = getParentPath(dest);
        String destName = getFileName(dest);
        DirectoryNode destParent;
        if (destParentPath.isEmpty()) {
            destParent = cwd;
        } else {
            FileSystemNode destParentNode = pathResolver.resolve(cwd, destParentPath, true);
            if (!destParentNode.isDirectory()) {
                throw new NotADirectoryException("Destination parent is not a directory: " + destParentPath);
            }
            destParent = (DirectoryNode) destParentNode;
        }
        FileSystemNode copy = copyNode(this, srcNode, srcNode.isDirectory());
        destParent.add(destName, copy);
    }
    
    @Override
    public List<String> expWildcard(String path, DirectoryNode curDir) throws FSException {
        List<String> result = new ArrayList<>();
        if (!path.contains("*") && !path.contains("?")) {
            result.add(path);
            return result;
        }
        // For now, just return the pattern itself
        // TODO: Implement proper wildcard matching
        result.add(path);
        return result;
    }
    
    @Override
    public DirectoryNode getRoot() {
        return root;
    }
    
    @Override
    public DirectoryNode getCwd() {
        return cwd;
    }

    private String getParentPath(String path) {
        if (path.equals("/")) {
            return "";
        }
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash <= 0) {
            return "";
        }
        return path.substring(0, lastSlash);
    }
    
    private String getFileName(String path) {
        if (path.equals("/")) {
            return "/";
        }
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash < 0) {
            return path;
        }
        return path.substring(lastSlash + 1);
    }
    
    private String findNameInParent(DirectoryNode parent, FileSystemNode child) {
        for (String name : parent.listNames()) {
            if (parent.get(name) == child) {
                return name;
            }
        }
        return null;
    }
    
    private String getAbsolutePath(DirectoryNode dir) {
        if (dir == null || dir == root) {
            return "/";
        }
        List<String> parts = new ArrayList<>();
        DirectoryNode current = dir;
        while (current != root && current.getParent() != current) {
            DirectoryNode parent = current.getParent();
            if (parent == null) break;
            
            String name = findNameInParent(parent, current);
            if (name != null) {
                parts.add(0, name);
            }
            current = parent;
        }
        if (parts.isEmpty()) {
            return "/";
        }
        return "/" + String.join("/", parts);
    }
    
    @Override
    public FileSystemNode resolveNode(String path, boolean followSymlinks) throws FSException {
        return pathResolver.resolve(cwd, path, followSymlinks);
    }
    
    @Override
    public void createNode(String path, FileSystemNode node) throws FSException {
        if (path == null || path.isEmpty()) {
            throw new InvalidPathException("Path cannot be empty");
        }
        try {
            pathResolver.resolve(cwd, path, false);
            throw new AlreadyExistsException("Node already exists: " + path);
        } catch (NotFoundException e) {
            // Good - doesn't exist yet
        }
        
        String parentPath = getParentPath(path);
        String nodeName = getFileName(path);
        
        DirectoryNode parent;
        if (parentPath.isEmpty()) {
            parent = cwd;
        } else {
            FileSystemNode parentNode = pathResolver.resolve(cwd, parentPath, true);
            if (!parentNode.isDirectory()) {
                throw new NotADirectoryException("Parent is not a directory: " + parentPath);
            }
            parent = (DirectoryNode) parentNode;
        }
        
        parent.add(nodeName, node);
    }
    
    @Override
    public void deleteNode(String path) throws FSException {
        FileSystemNode node = pathResolver.resolve(cwd, path, false);
        DirectoryNode parent = node.getParent();
        
        if (parent == null || parent == node) {
            throw new FSException("Cannot delete root directory");
        }
        
        String name = findNameInParent(parent, node);
        if (name != null) {
            parent.remove(name);
        }
    }

    @Override
    public FileSystemNode copyNode(FileSystem fs, FileSystemNode node, boolean recursive) {
        if (!node.isDirectory() && !node.isLink()) {
            return new FileNode();

        } else if (node.isLink()) {
            LinkNode symlink = (LinkNode) node;
            return new LinkNode(symlink.getTarget());

        } else if (node.isDirectory() && recursive) {
            DirectoryNode srcDir = (DirectoryNode) node;
            DirectoryNode destDir = new DirectoryNode();

            for (FileSystemNode child : fs.listNodes(srcDir)) {
                FileSystemNode childCopy = copyNode(fs, child, true);
                String childName = findChildName(srcDir, child);
                destDir.add(childName, childCopy);
            }

            return destDir;

        } else {
            return new DirectoryNode();
        }
    }
    
    @Override
    public DirectoryNode getParentDirectory(String path) throws FSException {
        String parentPath = getParentPath(path);
        
        if (parentPath.isEmpty()) {
            return cwd;
        }
        
        FileSystemNode parentNode = pathResolver.resolve(cwd, parentPath, true);
        if (!parentNode.isDirectory()) {
            throw new NotADirectoryException("Parent is not a directory: " + parentPath);
        }
        
        return (DirectoryNode) parentNode;
    }
    
    @Override
    public List<FileSystemNode> listNodes(DirectoryNode directory) {
        List<FileSystemNode> nodes = new ArrayList<>();
        for (String name : directory.listNames()) {
            nodes.add(directory.get(name));
        }
        return nodes;
    }
    
    @Override
    public String extractFileName(String path) {
        return getFileName(path);
    }
    
    @Override
    public String extractParentPath(String path) {
        return getParentPath(path);
    }

    private String findChildName(DirectoryNode parent, FileSystemNode child) {
        for (String name : parent.listNames()) {
            if (parent.get(name) == child) {
                return name;
            }
        }
        return null;
    }
}
