package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.FileSystemEventHandler;
import ch.supsi.fscli.frontend.model.FileSystem;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.util.FxLogger;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class FileSystemController implements FileSystemEventHandler {
    @Setter
    private FileSystem model;

    private File currentFile;

    private static FileSystemController instance;

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static FileSystemController getInstance() {
        if (instance == null) {
            instance = new FileSystemController();
        }
        return instance;
    }

    private FileSystemController() {
        this.currentFile = null;
    }

    @Override
    public void newFileSystem() {
        this.model.createFileSystem();
        this.currentFile = null;

        CommandLineView commandLine = CommandLineView.getInstance();
        commandLine.clearOutput();
        commandLine.appendOutput(MESSAGES.getString("filesystem.newCreated") + "\n");
        commandLine.appendOutput(MESSAGES.getString("filesystem.currentDir") + model.getCurrentDirectory() + "\n\n");

        FxLogger.getInstance().log(MESSAGES.getString("filesystem.newCreatedLog"));
    }

    @Override
    public void save() {
        if (currentFile == null) {
            FxLogger.getInstance().log(MESSAGES.getString("error.noFileSelectedSave"));
            return;
        }

        saveToFile(currentFile);
    }

    @Override
    public void saveAs(File file) {
        if (file == null) {
            FxLogger.getInstance().log(MESSAGES.getString("error.noFileSelected"));
            return;
        }

        this.currentFile = file;
        saveToFile(file);
    }

    @Override
    public void load(File file) {
        if (file == null) {
            FxLogger.getInstance().log(MESSAGES.getString("error.noFileSelected"));
            return;
        }

        if (!file.exists()) {
            FxLogger.getInstance().log(MESSAGES.getString("error.fileNotExist") + ": " + file.getAbsolutePath());
            return;
        }

        try {
            boolean success = model.loadFileSystem(file);

            if (!success) {
                FxLogger.getInstance().log(MESSAGES.getString("error.loadFailed"));
                return;
            }

            this.currentFile = file;

            CommandLineView commandLine = CommandLineView.getInstance();
            commandLine.clearOutput();
            commandLine.appendOutput(MESSAGES.getString("filesystem.loadedFrom") + ": " + file.getName() + "\n");
            commandLine.appendOutput(MESSAGES.getString("filesystem.currentDir") + model.getCurrentDirectory() + "\n\n");

            FxLogger.getInstance().log(MESSAGES.getString("filesystem.loadedLog") + ": " + file.getAbsolutePath());

        } catch (Exception e) {
            FxLogger.getInstance().log(MESSAGES.getString("error.loadException") + ": " + e.getMessage());
        }
    }

    private void saveToFile(File file) {
        if (!model.isFileSystemReady()) {
            FxLogger.getInstance().log(MESSAGES.getString("error.noFilesystemToSave"));
            return;
        }

        try {
            model.saveFileSystem(file);

            this.currentFile = file;
            FxLogger.getInstance().log(MESSAGES.getString("filesystem.savedTo") + ": " + file.getAbsolutePath());

        } catch (IOException e) {
            FxLogger.getInstance().log(MESSAGES.getString("error.saveFailed") + ": " + e.getMessage());
        } catch (IllegalStateException e) {
            FxLogger.getInstance().log(MESSAGES.getString("error.saveIllegalState") + ": " + e.getMessage());
        }
    }
}
