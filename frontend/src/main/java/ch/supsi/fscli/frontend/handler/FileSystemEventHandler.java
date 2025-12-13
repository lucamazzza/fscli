package ch.supsi.fscli.frontend.handler;

import java.io.File;

public interface FileSystemEventHandler extends EventHandler {
    void newFileSystem(boolean force);
    void save();
    void saveAs(File file);
    void load(File file);
    boolean hasUnsavedChanges();
}
