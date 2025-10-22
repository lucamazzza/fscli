package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.business.UserPreferences;
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
    private final PreferencesSerializer serializer = new PreferencesSerializer();
    private final PreferencesDeserializer deserializer = new PreferencesDeserializer();

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
        if (!Files.exists(prefPath)) return Optional.empty();
        try {
            String json = Files.readString(prefPath);
            return Optional.of(deserializer.deserialize(json));
        } catch (IOException e) {
            PreferencesLogger.logError("Failed to load preferences", e);
            return Optional.empty();
        }
    }
}