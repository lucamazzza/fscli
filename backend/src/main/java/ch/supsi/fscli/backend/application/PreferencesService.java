package ch.supsi.fscli.backend.application;

import ch.supsi.fscli.backend.data.PreferencesFileManager;
import ch.supsi.fscli.backend.business.UserPreferences;
import ch.supsi.fscli.backend.util.BackendGlobalVariables;
import ch.supsi.fscli.backend.util.PreferencesLogger;

import java.io.File;
import java.io.IOException;
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

    private UserPreferences lastSavedPrefs = null;

    private void save() {
        try {
            File file = BackendGlobalVariables.DEFAULT_PREF_PATH.toFile();

            boolean existedBefore = file.exists();
            fileManager.save(currentPrefs);

            if (!existedBefore) {
                PreferencesLogger.logInfo("Preferences file created: " + file.getAbsolutePath());
            } else {
                PreferencesLogger.logInfo("Preferences file updated: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            PreferencesLogger.logError("Could not save preferences", e);
            currentPrefs = new UserPreferences();
        }
    }


    public UserPreferences getCurrentPrefs() {
        return currentPrefs;
    }

    public void reload() {
        Optional<UserPreferences> loaded = fileManager.load();
        loaded.ifPresentOrElse(
                p -> {
                    this.currentPrefs = p;
                    PreferencesLogger.logInfo("Preferences reloaded successfully from: " + BackendGlobalVariables.DEFAULT_PREF_PATH);
                },
                () -> PreferencesLogger.logInfo("No preferences file found at: " + BackendGlobalVariables.DEFAULT_PREF_PATH)
        );
    }
}
