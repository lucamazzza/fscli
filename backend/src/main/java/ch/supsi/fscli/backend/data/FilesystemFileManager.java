package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.util.FilesystemLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FilesystemFileManager {
    private Path path;
    private Serializer<FSNode> serializer;
    private Deserializer<FSNode> deserializer;

    public FilesystemFileManager(Path path) {
        this.path = path;
        this.serializer = new Serializer<>();
        this.deserializer = new Deserializer<>();
    }

    public void save(FSNode root) throws IOException {
        Files.writeString(path, serializer.serialize(root));
    }

    public Optional<FSNode> load() {
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return Optional.empty();
            }
            String json = Files.readString(path);
            return Optional.of(deserializer.deserialize(json, FSNode.class));
        } catch (IOException e) {
            FilesystemLogger.logError("Failed to load Filesystem");
            return Optional.empty();
        }
    }

}
