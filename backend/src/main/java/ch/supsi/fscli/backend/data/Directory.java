package ch.supsi.fscli.backend.data;

import java.util.Collection;
import java.util.Collections;

public final class Directory implements INode {
    private final Long id;
    private String name;
    private INode parent;
    private final Collection<INode> children;

    public Directory(String name) {
        this.id = IdGenerator.getNextId();
        // Check if parent has dir with same name ?
        this.name = name;
        children = Collections.emptyList();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public INode getParent() {
        return this.parent;
    }

    @Override
    public void setParent(INode parent) {
        this.parent = parent;
    }

    public Collection<INode> getChildren() {
        return this.children;
    }

    public boolean addChild(INode child) {
        return this.children.add(child);
    }

    public boolean removeChildren(INode child) {
        return this.children.remove(child);
    }
}
