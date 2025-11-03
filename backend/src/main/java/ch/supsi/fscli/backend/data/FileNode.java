package ch.supsi.fscli.backend.data;

public class FileNode extends FileSystemNode {

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
