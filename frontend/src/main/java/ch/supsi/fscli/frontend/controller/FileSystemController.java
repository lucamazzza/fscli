package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.event.FileSystemEventHandler;

public class FileSystemController implements FileSystemEventHandler {
    private static FileSystemController instance;

    public static FileSystemController getInstance() {
        if (instance == null) {
            instance = new FileSystemController();
        }
        return instance;
    }

    private FileSystemController() {

    }

    @Override
    public void newFileSystem() {

    }

    @Override
    public void save() {

    }

    @Override
    public void saveAs(String path) {

    }

    @Override
    public void load(String path) {

    }
}
