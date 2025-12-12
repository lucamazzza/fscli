package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.CommandLineEventHandler;
import ch.supsi.fscli.frontend.handler.FileSystemEventHandler;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import lombok.Setter;

import java.io.File;

public class FileSystemController implements FileSystemEventHandler, CommandLineEventHandler {
    @Setter
    private FileSystemModel model;

    private static FileSystemController instance;

    public static FileSystemController getInstance() {
        if (instance == null) {
            instance = new FileSystemController();
        }
        return instance;
    }

    FileSystemController() {
    }

    @Override
    public void newFileSystem(boolean force) {
        this.model.createFileSystem(force);
    }

    @Override
    public void save() {
        model.save();
    }

    @Override
    public void saveAs(File file) {
        if (file == null) return;
        model.saveAs(file);
    }

    @Override
    public void load(File file) {
        if (file == null) return;
        model.load(file);
    }

    @Override
    public void executeCommand(String command) {
        if (model == null) return;
        if (command == null || command.isBlank()) return;
        model.executeCommand(command.trim());
    }
}
