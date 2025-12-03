package ch.supsi.fscli.backend.data.serde;

import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.util.FilesystemLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class FilesystemFileManager {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    private Path path;
    private final Serializer<FileSystemNode> serializer;
    private final Deserializer<FileSystemNode> deserializer;

    public FilesystemFileManager(Path path) {
        this.path = path;
        this.serializer = new Serializer<>();
        this.deserializer = new Deserializer<>();
    }

    public void save(FileSystemNode root) throws IOException {
        Files.writeString(path, serializer.serialize(root));
    }

    public Optional<FileSystemNode> load() {
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return Optional.empty();
            }
            String json = Files.readString(path);
            return Optional.of(deserializer.deserialize(json, FileSystemNode.class));
        } catch (IOException e) {
            FilesystemLogger.logError(MESSAGES.getString("error.noFilesystemLoaded"));
            return Optional.empty();
        }
    }
}
