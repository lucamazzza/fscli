package ch.supsi.fscli.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

public class FileNode extends FSNode {
    @JsonIgnore
    private byte[] data;

    public FileNode() {
        super();
        this.data = new byte[0];
    }
    public synchronized byte[] readAll() {
        this.atime = Instant.now();
        return Arrays.copyOf(this.data, this.data.length);
    }
    public synchronized void writeAll(byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
        this.mtime = Instant.now();
        this.ctime = Instant.now();
    }
    public synchronized void append(byte[] more) {
        byte[] out = new byte[data.length + more.length];
        System.arraycopy(data, 0, out, 0, data.length);
        System.arraycopy(more, 0, out, data.length, more.length);
        this.data = out;
        this.mtime = Instant.now();
        this.ctime = Instant.now();
    }
    public int size() {
        return data.length;
    }
    @Override
    public boolean isDirectory() {
        return false;
    }
    @Override
    public boolean isSymlink() {
        return false;
    }
    @Override
    public String typeName() {
        return "file";
    }
    public String readAsString() {
        return new String(readAll(), StandardCharsets.UTF_8);
    }
}
