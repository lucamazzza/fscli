package ch.supsi.fscli.backend.business;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FSNode;
import ch.supsi.fscli.backend.data.SymlinkNode;

import java.util.Arrays;
import java.util.LinkedList;

public class PathResolver {
    private static final String SEP = "/";

    public FSNode resolve(DirectoryNode cwd, String path, boolean followSym) throws NotFoundException, InvalidPathException {
        return resolve(cwd, path, followSym, 0);
    }
    private FSNode resolve(DirectoryNode cwd, String path, boolean followSym, int depth) throws NotFoundException, InvalidPathException {
        if (depth > 32) throw new InvalidPathException("too many symlink levels");
        if (path == null || path.isEmpty()) throw new NotFoundException("path is empty");
        if (cwd == null) throw new InvalidPathException("cwd is null");
        DirectoryNode base;
        String rel;
        if (path.startsWith(SEP)) {
            base = getRoot(cwd);
            rel = path.substring(SEP.length());
        } else {
            base = cwd;
            rel = path;
        }
        if (rel.isEmpty()) return base;
        String[] parts = Arrays.stream(rel.split("/")).filter(s -> !s.isEmpty()).toArray(String[]::new);
        FSNode cur = base;
        for (int i = 0; i < parts.length; i++) {
            String comp = parts[i];
            if (comp.equals(".")) continue;
            if (comp.equals("..")) {
                if (cur instanceof DirectoryNode dir) {
                    DirectoryNode par = dir.getParent();
                    cur = (par == null) ? getRoot(dir) : par;
                } else {
                    DirectoryNode par = cwd.getParent();
                    cur = (par == null) ? getRoot(cwd) : par;
                }
                continue;
            }
            if (cur == null) throw new NotFoundException("not a directory: " + comp);
            DirectoryNode dir = (DirectoryNode) cur;
            FSNode next = dir.get(comp);
            if (next == null) throw new NotFoundException("no such file or directory: " + comp);
            if (next.isSymlink() && (followSym || i < parts.length - 1)) {
                SymlinkNode sl = (SymlinkNode) next;
                String target = sl.getTarget();
                String suffix = (i + 1 < parts.length) ? ("/" + String.join("/", Arrays.copyOfRange(parts, i + 1, parts.length))) : "";
                String resolvedTarget;
                if (target.startsWith(SEP)) {
                    resolvedTarget = target + suffix;
                } else {
                    String parentPath = pathOf(dir);
                    if (parentPath.equals(SEP)) parentPath = "";
                    resolvedTarget = parentPath + "/" + target + suffix;
                }
                return resolve(cwd, resolvedTarget, followSym, depth + 1);
            } else {
                cur = next;
            }
        }
        return cur;
    }
    private DirectoryNode getRoot(DirectoryNode cwd) throws NotFoundException {
        DirectoryNode cur = cwd;
        while (cur.getParent() != null && cur.getParent() != cur) cur = cur.getParent();
        return cur;
    }
    private String pathOf(DirectoryNode dir) {
        if (dir == null) return "/";
        if (dir.getParent() == dir || dir.getParent() == null) return SEP;
        LinkedList<String> parts = new LinkedList<>();
        DirectoryNode cur = dir;
        while (cur.getParent() != null && cur.getParent() != cur) {
            DirectoryNode parent = cur.getParent();
            String found = null;
            for (var e : parent.snapshot().entrySet()) {
                if (e.getValue() == cur) { found = e.getKey(); break; }
            }
            if (found == null) break;
            parts.addFirst(found);
            cur = parent;
        }
        return String.join(SEP, parts);
    }
}
