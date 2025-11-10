package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.FileSystemEventHandler;
import ch.supsi.fscli.frontend.model.FileSystem;
import lombok.Setter;

import java.io.File;

public class FileSystemController implements FileSystemEventHandler {
    @Setter
    private FileSystem model;

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
        this.model.createFileSystem();
    }

    @Override
    public void save() {

    }

    @Override
    public void saveAs(File file) {

    }

    @Override
    public void load(File file) {

    }
}
