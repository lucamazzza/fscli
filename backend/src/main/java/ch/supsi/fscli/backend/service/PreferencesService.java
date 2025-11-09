package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.data.serde.PreferencesFileManager;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.util.PreferencesLogger;
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
        modifier.accept(currentPrefs);
        save();
    }

    private void save() {
        try {
            File file = BackendGlobalVariables.DEFAULT_PREF_PATH.toFile();
            fileManager.save(currentPrefs);
        } catch (IOException e) {
            currentPrefs = new UserPreferences();
        }
    }

    public UserPreferences getCurrentPrefs() {
        return currentPrefs;
    }

    public void reload() {
        Optional<UserPreferences> loaded = fileManager.load();
        loaded.ifPresent(p -> currentPrefs = p);
    }
}
