package ch.supsi.fscli.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DirectoryNode extends FileSystemNode {
    @JsonIgnore
    private final Map<String, FileSystemNode> children = new LinkedHashMap<>();

    public DirectoryNode() {
        super();
    }
    public synchronized void add(String name, FileSystemNode node) {
        if (name == null || name.isEmpty()) throw new IllegalArgumentException();
        children.put(name, node);
        node.setParent(this);
        this.mtime = Instant.now();
        node.atime = Instant.now();
    }
    public synchronized FileSystemNode remove(String name) {
        FileSystemNode node = children.remove(name);
        if  (node != null) {
            node.setParent(null);
            this.mtime = Instant.now();
        }
        return node;
    }
    public synchronized boolean contains(String name) {
        return children.containsKey(name);
    }
    public synchronized FileSystemNode get(String name) {
        return children.get(name);
    }
    public synchronized List<String> listNames() {
        return new ArrayList<>(children.keySet());
    }
    @JsonProperty("children")
    public synchronized Map<String, FileSystemNode> snapshot() {
        return new LinkedHashMap<>(children);
    }
    @JsonProperty("children")
    @SuppressWarnings("unused")
    public synchronized void setChildren(Map<String, FileSystemNode> children) {
        this.children.clear();
        if (children != null) {
            for (Map.Entry<String, FileSystemNode> entry : children.entrySet()) {
                this.children.put(entry.getKey(), entry.getValue());
                entry.getValue().setParent(this);
            }
        }
    }
    @JsonIgnore
    public synchronized boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }
    @Override
    public boolean isLink() {
        return false;
    }
    @Override
    public String typeName() {
        return "directory";
    }
}
