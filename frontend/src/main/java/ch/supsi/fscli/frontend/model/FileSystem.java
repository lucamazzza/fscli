package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.frontend.event.EventError;
import ch.supsi.fscli.frontend.event.EventNotifier;
import ch.supsi.fscli.frontend.event.FileEvent;
import lombok.Setter;

public class FileSystem {
    private boolean isFilePresent;

    @Setter
    private EventNotifier<FileEvent> eventManager;

    private static FileSystem instance;

    public static FileSystem getInstance() {
        if (instance == null) {
            instance = new FileSystem();
        }
        return instance;
    }

    private FileSystem() {
        this.isFilePresent = false;
    }

    public void createFileSystem() {
        this.isFilePresent = true;
        eventManager.notify(new FileEvent(EventError.SUCCESS, "FileSystem was created successfully", true));
    }
}
