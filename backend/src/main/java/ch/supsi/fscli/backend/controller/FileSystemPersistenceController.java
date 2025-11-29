package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.controller.dto.CommandResponseDTO;
import ch.supsi.fscli.backend.service.FileSystemService;

import java.util.List;

public class FileSystemPersistenceController {

    private final FileSystemService service;

    public FileSystemPersistenceController() {
        this.service = new FileSystemService();
    }

    public void createNewFileSystem() {
        service.createNewFileSystem();
    }

    public boolean isFileSystemLoaded() {
        return service.isFileSystemLoaded();
    }

    public String getCurrentDirectory() {
       return service.getCurrentDirectory();
    }

    public void setFileSystem(InMemoryFileSystem fs) {
        service.setFileSystem(fs);
    }

    public FileSystem getFileSystem() {
        return service.getFileSystem();
    }
}
