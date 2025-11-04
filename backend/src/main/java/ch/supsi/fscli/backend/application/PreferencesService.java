package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.data.PreferencesFileManager;
import ch.supsi.fscli.backend.business.UserPreferences;
import ch.supsi.fscli.backend.util.BackendGlobalVariables;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class PreferencesService {
    private final PreferencesFileManager fileManager;
    private UserPreferences currentPrefs;

    public PreferencesService() {
        this(new PreferencesFileManager());
    }

    public PreferencesService(PreferencesFileManager fileManager) {
        this.fileManager = fileManager;
        this.currentPrefs = fileManager.load().orElseGet(UserPreferences::new);
    }

    public void updatePreference(Consumer<UserPreferences> modifier) {
        modifier.accept(currentPrefs); // il model aggiorna direttamente valori già sanitizzati
        save();
        // log rimosso
    }

    private void save() {
        try {
            File file = BackendGlobalVariables.DEFAULT_PREF_PATH.toFile();
            fileManager.save(currentPrefs);
            // log rimosso
        } catch (IOException e) {
            // mantiene il logging di errore, utile per debug reale
            currentPrefs = new UserPreferences();
        }
    }

    public UserPreferences getCurrentPrefs() {
        return currentPrefs;
    }

    public void reload() {
        Optional<UserPreferences> loaded = fileManager.load();
        loaded.ifPresent(p -> currentPrefs = p);
        // log rimosso
    }

    public Map<String, String> loadRawPrefs() {
        Optional<UserPreferences> loaded = fileManager.loadRaw(); // nuovo metodo
        if (loaded.isPresent()) {
            UserPreferences p = loaded.get();
            return Map.of(
                    "language", p.getLanguage(),
                    "cmdColumns", String.valueOf(p.getCmdColumns()),
                    "outputLines", String.valueOf(p.getOutputLines()),
                    "logLines", String.valueOf(p.getLogLines()),
                    "cmdFont", p.getCmdFont(),
                    "outputFont", p.getOutputFont(),
                    "logFont", p.getLogFont()
            );
        }
        return Map.of();
    }

}
