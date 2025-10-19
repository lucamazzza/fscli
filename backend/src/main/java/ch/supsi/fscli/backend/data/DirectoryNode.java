package ch.supsi.fscli.backend.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DirectoryNode extends FSNode {
    private final Map<String, FSNode> children = new LinkedHashMap<>();

    public DirectoryNode() {
        super();
    }
    public synchronized void add(String name, FSNode node) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException();
        children.put(name, node);
        node.setParent(this);
        this.mtime = Instant.now();
    }
    public synchronized FSNode remove(String name) {
        FSNode node = children.remove(name);
        if  (node != null) {
            node.setParent(null);
            this.mtime = Instant.now();
        }
        return node;
    }
    public synchronized boolean contains(String name) {
        return children.containsKey(name);
    }
    public synchronized FSNode get(String name) {
        return children.get(name);
    }
    public synchronized List<String> listNames() {
        return new ArrayList<>(children.keySet());
    }
    public synchronized Map<String, FSNode> snapshot() {
        return new LinkedHashMap<>(children);
    }
    public synchronized boolean isEmpty() {
        return children.isEmpty();
    }
    @Override
    public boolean isDirectory() {
        return true;
    }
    @Override
    public boolean isSymlink() {
        return false;
    }
    @Override
    public String typeName() {
        return "directory";
    }
}
