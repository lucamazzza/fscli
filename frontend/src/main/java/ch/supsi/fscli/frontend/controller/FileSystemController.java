package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
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
        commandLine.appendOutput(FrontendMessageProvider.get("filesystem.newCreated") + "\n");
        commandLine.appendOutput(FrontendMessageProvider.get("filesystem.currentDir") + model.getCurrentDirectory() + "\n\n");

        FxLogger.getInstance().log(FrontendMessageProvider.get("filesystem.newCreatedLog"));
    }

    @Override
    public void save() {
        if (currentFile == null) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.noFileSelectedSave"));
            return;
        }

        saveToFile(currentFile);
    }

    @Override
    public void saveAs(File file) {
        if (file == null) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.noFileSelected"));
            return;
        }

        this.currentFile = file;
        saveToFile(file);
    }

    @Override
    public void load(File file) {
        if (file == null) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.noFileSelected"));
            return;
        }

        if (!file.exists()) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.fileNotExist") + ": " + file.getAbsolutePath());
            return;
        }

        try {
            boolean success = model.loadFileSystem(file);

            if (!success) {
                FxLogger.getInstance().log(FrontendMessageProvider.get("error.loadFailed"));
                return;
            }

            this.currentFile = file;

            CommandLineView commandLine = CommandLineView.getInstance();
            commandLine.clearOutput();
            commandLine.appendOutput(FrontendMessageProvider.get("filesystem.loadedFrom") + ": " + file.getName() + "\n");
            commandLine.appendOutput(FrontendMessageProvider.get("filesystem.currentDir") + model.getCurrentDirectory() + "\n\n");

            FxLogger.getInstance().log(FrontendMessageProvider.get("filesystem.loadedLog") + ": " + file.getAbsolutePath());

        } catch (Exception e) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.loadException") + ": " + e.getMessage());
        }
    }

    private void saveToFile(File file) {
        if (!model.isFileSystemReady()) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.noFilesystemToSave"));
            return;
        }

        try {
            model.saveFileSystem(file);

            this.currentFile = file;
            FxLogger.getInstance().log(FrontendMessageProvider.get("filesystem.savedTo") + ": " + file.getAbsolutePath());

        } catch (IOException e) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.saveFailed") + ": " + e.getMessage());
        } catch (IllegalStateException e) {
            FxLogger.getInstance().log(FrontendMessageProvider.get("error.saveIllegalState") + ": " + e.getMessage());
        }
    }
}
