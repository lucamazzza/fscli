package ch.supsi.fscli.backend.data.serde;

import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.util.BackendGlobalVariables;
import ch.supsi.fscli.backend.util.PreferencesLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class PreferencesFileManager {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    private final Path prefPath;
    private final Serializer<UserPreferences> serializer = new Serializer<>();
    private final Deserializer<UserPreferences> deserializer = new Deserializer<>();

    public PreferencesFileManager() {
        this.prefPath = BackendGlobalVariables.DEFAULT_PREF_PATH;
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
            UserPreferences prefs = deserializer.deserialize(json, UserPreferences.class);

            prefs.setCmdColumns(prefs.getCmdColumns());
            prefs.setOutputLines(prefs.getOutputLines());
            prefs.setLogLines(prefs.getLogLines());

            if (!BackendGlobalVariables.SYSTEM_FONTS.contains(prefs.getCmdFont())) {
                prefs.setCmdFont(BackendGlobalVariables.DEFAULT_CMD_FONT);
            }
            if (!BackendGlobalVariables.SYSTEM_FONTS.contains(prefs.getOutputFont())) {
                prefs.setOutputFont(BackendGlobalVariables.DEFAULT_OUTPUT_FONT);
            }
            if (!BackendGlobalVariables.SYSTEM_FONTS.contains(prefs.getLogFont())) {
                prefs.setLogFont(BackendGlobalVariables.DEFAULT_LOG_FONT);
            }

            return Optional.of(prefs);
        } catch (IOException e) {
            PreferencesLogger.logError(MESSAGES.getString("error.prefsLoadFailed"), e);
            return Optional.empty();
        }
    }
}
