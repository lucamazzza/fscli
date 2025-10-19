package ch.supsi.fscli.backend.data;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class FSNode {
    private static final AtomicInteger ID_GEN = new AtomicInteger(1);

    protected final int id;
    protected int linkCount;
    protected Instant ctime;
    protected Instant mtime;
    protected Instant atime;
    protected DirectoryNode parent;

    protected FSNode() {
        this.id = ID_GEN.getAndIncrement();
        this.linkCount = 1;
        Instant now = Instant.now();
        this.ctime = this.mtime = this.atime = now;
        this.parent = null;
    }
    public int getId() {
        return this.id;
    }
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
    public Instant getCTime() {
        return this.ctime;
    }
    public Instant getMTime() {
        return this.mtime;
    }
    public Instant getATime() {
        return this.atime;
    }
    public void touch() {
        this.ctime = Instant.now();
        this.mtime = Instant.now();
    }
    public DirectoryNode getParent() {
        return this.parent;
    }
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    public abstract boolean isDirectory();
    public abstract boolean isSymlink();
    public abstract String typeName();

    @Override
    public String toString() {
        return String.format("%s(id=%d, links=%d)",  typeName(), id, linkCount);
    }
}
