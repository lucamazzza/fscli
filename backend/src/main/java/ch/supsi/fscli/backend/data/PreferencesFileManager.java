package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.service.UserPreferences;
import ch.supsi.fscli.backend.util.PreferencesLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PreferencesFileManager {

    private static final Path DEFAULT_PREF_PATH =
            Paths.get(System.getProperty("user.home"), ".fs_prefs.json");

    private final Path prefPath;
    private final Serializer<UserPreferences> serializer = new Serializer<>();
    private final Deserializer<UserPreferences> deserializer = new Deserializer<>();

    public PreferencesFileManager() {
        this.prefPath = DEFAULT_PREF_PATH;
    }

    public PreferencesFileManager(Path path) {
        this.prefPath = path;
    }

    public void save(UserPreferences prefs) throws IOException {
        Files.writeString(prefPath, serializer.serialize(prefs));
    }

    public Optional<UserPreferences> load() {
        try {
            if (!Files.exists(prefPath) || Files.size(prefPath) == 0) {
                return Optional.empty();
            }
            String json = Files.readString(prefPath);
            return Optional.of(deserializer.deserialize(json, UserPreferences.class));
        } catch (IOException e) {
            PreferencesLogger.logError("Failed to load preferences", e);
            return Optional.empty();
        }
    }
}