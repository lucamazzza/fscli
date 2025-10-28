package ch.supsi.fscli.backend.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

public class FileNode extends FSNode {

    public FileNode() {
        super();
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
}
