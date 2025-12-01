package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.FileSystemEventHandler;
import ch.supsi.fscli.frontend.model.FileSystem;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.util.FxLogger;
import lombok.Setter;

import java.io.File;
import java.io.IOException;

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
        commandLine.appendOutput("New filesystem created successfully!\n");
        commandLine.appendOutput("Current directory: " + model.getCurrentDirectory() + "\n\n");
        
        FxLogger.getInstance().log("New filesystem created");
    }

    @Override
    public void save() {
        if (currentFile == null) {
            FxLogger.getInstance().log("Error: No file selected for save. Use 'Save As' first.");
            return;
        }
        
        saveToFile(currentFile);
    }

    @Override
    public void saveAs(File file) {
        if (file == null) {
            FxLogger.getInstance().log("Error: No file selected");
            return;
        }
        
        this.currentFile = file;
        saveToFile(file);
    }

    @Override
    public void load(File file) {
        if (file == null) {
            FxLogger.getInstance().log("Error: No file selected");
            return;
        }
        
        if (!file.exists()) {
            FxLogger.getInstance().log("Error: File does not exist: " + file.getAbsolutePath());
            return;
        }
        
        try {
            boolean success = model.getBackendPersistenceController().loadFileSystem(file.toPath());
            
            if (!success) {
                FxLogger.getInstance().log("Error: Failed to load filesystem from file");
                return;
            }
            
            this.currentFile = file;
            
            CommandLineView commandLine = CommandLineView.getInstance();
            commandLine.clearOutput();
            commandLine.appendOutput("Filesystem loaded successfully from: " + file.getName() + "\n");
            commandLine.appendOutput("Current directory: " + model.getCurrentDirectory() + "\n\n");
            
            FxLogger.getInstance().log("Filesystem loaded from: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            FxLogger.getInstance().log("Error loading filesystem: " + e.getMessage());
        }
    }
    
    private void saveToFile(File file) {
        if (!model.isFileSystemReady()) {
            FxLogger.getInstance().log("Error: No filesystem to save");
            return;
        }
        
        try {
            model.getBackendPersistenceController().saveFileSystem(file.toPath());
            
            this.currentFile = file;
            FxLogger.getInstance().log("Filesystem saved to: " + file.getAbsolutePath());
            
        } catch (IOException e) {
            FxLogger.getInstance().log("Error saving filesystem: " + e.getMessage());
        } catch (IllegalStateException e) {
            FxLogger.getInstance().log("Error: " + e.getMessage());
        }
    }
}
