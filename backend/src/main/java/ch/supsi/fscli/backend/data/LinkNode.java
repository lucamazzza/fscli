package ch.supsi.fscli.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class LinkNode extends FileSystemNode {
    private String target;

    public LinkNode(String target) {
        super();
        this.target = target;
    }
    @JsonProperty("target")
    public String getTarget() {
        return this.target;
    }
    @JsonProperty("target")
    public void setTarget(String target) {
        this.target = target;
        this.mtime = Instant.now();
        this.ctime = Instant.now();
    }
    @Override
    public boolean isDirectory() {
        return false;
    }
    @Override
    public boolean isLink() {
        return true;
    }
    @Override
    public String typeName() {
        return "symlink";
    }
    @Override
    public String toString() {
        return String.format("%s (id=%d -> \"%s\")", this.typeName(), this.id, this.target);
    }
}
