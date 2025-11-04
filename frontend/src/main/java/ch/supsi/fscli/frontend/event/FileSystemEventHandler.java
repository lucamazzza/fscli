package ch.supsi.fscli.frontend.event;

public interface FileSystemEventHandler extends EventHandler {
    void newFileSystem();
    void save();
    void saveAs(String path);
    void load(String path);
}
