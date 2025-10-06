package ch.supsi.fscli.backend.data;

public class Link implements INode {
    private final Long id;
    private String name;
    private INode parent;

    public Link(String name) {
        this.id = IdGenerator.getNextId();
        // TODO: check if link has unique name?
        this.name = name;
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
}
