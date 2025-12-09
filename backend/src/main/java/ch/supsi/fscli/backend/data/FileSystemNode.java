package ch.supsi.fscli.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all filesystem nodes (files, directories, links).
 * Tracks metadata like creation time, modification time, and link count.
 * Supports JSON serialization with Jackson.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "nodeType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FileNode.class, name = "file"),
        @JsonSubTypes.Type(value = DirectoryNode.class, name = "directory"),
        @JsonSubTypes.Type(value = LinkNode.class, name = "link")
})
public abstract class FileSystemNode {
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);

    protected final int id;
    protected int linkCount;
    protected Instant ctime;
    protected Instant mtime;
    protected Instant atime;
    @JsonIgnore
    protected DirectoryNode parent;

    protected FileSystemNode() {
        this.id = ID_GEN.getAndIncrement();
        this.linkCount = 1;
        Instant now = Instant.now();
        this.ctime = this.mtime = this.atime = now;
        this.parent = null;
    }
    @JsonProperty("id")
    public int getId() {
        return this.id;
    }
    @JsonProperty("linkCount")
    public int getLinkCount () {
        return this.linkCount;
    }
    public void incrementLinkCount() {
        linkCount++;
        this.ctime = Instant.now();
    }
    public void decrementLinkCount() {
        linkCount = Math.max(0, linkCount - 1);
        this.ctime = Instant.now();
    }
    @JsonProperty("ctime")
    public Instant getCTime() {
        return this.ctime;
    }
    @JsonProperty("mtime")
    public Instant getMTime() {
        return this.mtime;
    }
    @JsonProperty("atime")
    public Instant getATime() {
        return this.atime;
    }
    public void touch() {
        this.ctime = Instant.now();
        this.mtime = Instant.now();
    }
    @JsonIgnore
    public DirectoryNode getParent() {
        return this.parent;
    }
    @JsonIgnore
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    @JsonProperty(value = "isDirectory", access = JsonProperty.Access.READ_ONLY)
    public abstract boolean isDirectory();
    @JsonProperty(value = "isLink", access = JsonProperty.Access.READ_ONLY)
    public abstract boolean isLink();
    @JsonProperty(value = "typeName", access = JsonProperty.Access.READ_ONLY)
    public abstract String typeName();

    @Override
    public String toString() {
        return String.format("%s(id=%d, links=%d)",  typeName(), id, linkCount);
    }
}
