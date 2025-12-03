package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.CommandLineEventHandler;
import ch.supsi.fscli.frontend.handler.FileSystemEventHandler;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import lombok.Setter;

import java.io.File;

public class FileSystemController implements FileSystemEventHandler, CommandLineEventHandler {
    @Setter
    private FileSystemModel fileSystemModel;

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
    public void newFileSystem(boolean force) {
        this.fileSystemModel.createFileSystem(force);
    }

    @Override
    public void save() {
        fileSystemModel.save();
    }

    @Override
    public void saveAs(File file) {
        if (file == null) return;
        fileSystemModel.saveAs(file);
    }

    @Override
    public void load(File file) {
        if (file == null) return;
        fileSystemModel.load(file);
    }

    @Override
    public void executeCommand(String command) {
        if (fileSystemModel == null) return;
        if (command == null || command.isBlank()) return;
        fileSystemModel.executeCommand(command.trim());
    }
}
