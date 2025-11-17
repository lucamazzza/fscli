package ch.supsi.fscli.frontend.event;

public class FileEventManager extends EventManager<FileEvent>{
    @Override
    public void notify(FileEvent event) {
        getListeners().forEach(listener -> listener.update(event));
    }
}
