package ch.supsi.fscli.backend.provider.resolver;

import ch.supsi.fscli.backend.core.exception.InvalidPathException;
import ch.supsi.fscli.backend.core.exception.NotFoundException;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.LinkNode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

public class PathResolver {
    private static final String SEP = "/";
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    private static PathResolver self;
    public static PathResolver getInstance() {
        if (self == null) {
            self = new PathResolver();
        }
        return self;
    }

    private PathResolver() {}

    public FileSystemNode resolve(DirectoryNode cwd, String path, boolean followSym) throws NotFoundException, InvalidPathException {
        return resolve(cwd, path, followSym, 0);
    }

    private FileSystemNode resolve(DirectoryNode cwd, String path, boolean followSym, int depth) throws NotFoundException, InvalidPathException {
        if (depth > 32) throw new InvalidPathException(MESSAGES.getString("tooManySymlinkLevels"));
        if (path == null || path.isEmpty()) throw new NotFoundException(MESSAGES.getString("EmptyPath"));
        if (cwd == null) throw new InvalidPathException(MESSAGES.getString("cwdNull"));

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

        String[] parts = Arrays.stream(rel.split(SEP)).filter(s -> !s.isEmpty()).toArray(String[]::new);
        FileSystemNode cur = base;

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
            if (cur == null) throw new NotFoundException(MESSAGES.getString("notDirectory") + ": " + comp);

            DirectoryNode dir = (DirectoryNode) cur;
            FileSystemNode next = dir.get(comp);
            if (next == null) throw new NotFoundException(MESSAGES.getString("noSuchFileOrDir") + ": " + comp);

            if (next.isLink() && (followSym || i < parts.length - 1)) {
                LinkNode sl = (LinkNode) next;
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
        if (dir == null) return SEP;
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
