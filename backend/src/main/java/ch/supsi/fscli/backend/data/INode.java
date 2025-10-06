package ch.supsi.fscli.backend.data;

public interface INode {
    Long getId();
    String getName();
    void setName(String name);
    INode getParent();
    void setParent(INode parent);
}
