package ch.supsi.fscli.frontend.controller;

public interface FileSystemHandler extends EventHandler {
    void newFileSystem();
    void save();
    void saveAs(String name);
    void load();
    void parseCommand(String inputString);
    // void quit();
}
