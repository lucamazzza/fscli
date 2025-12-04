package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.data.serde.PreferencesFileManager;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import ch.supsi.fscli.backend.util.BackendGlobalVariables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        boolean fileExists = Files.exists(BackendGlobalVariables.DEFAULT_PREF_PATH);

        Optional<UserPreferences> loaded = fileManager.load();

        if (loaded.isPresent() && fileExists) {
            this.currentPrefs = loaded.get();
            PreferencesLogger.logInfo(BackendMessageProvider.get("preferencesChargedFrom")
                            + BackendGlobalVariables.DEFAULT_PREF_PATH
            );
        } else {
            this.currentPrefs = new UserPreferences();
            try {
                fileManager.save(currentPrefs);
                PreferencesLogger.logInfo(BackendMessageProvider.get("newPreferencesFile")
                        + BackendGlobalVariables.DEFAULT_PREF_PATH
                );
            } catch (IOException e) {
                PreferencesLogger.logInfo(BackendMessageProvider.get("errorNewPreferencesFile")
                        + BackendGlobalVariables.DEFAULT_PREF_PATH
                );
            }
        }
    }

    public UserPreferences getCurrentPrefs() {
        return currentPrefs;
    }

    public void updatePreference(Consumer<UserPreferences> modifier) {
        modifier.accept(currentPrefs);
        save();
    }

    private void save() {
        try {
            fileManager.save(currentPrefs);
        } catch (IOException e) {
            PreferencesLogger.logError(
                    "Errore durante il salvataggio delle preferenze",
                    e
            );
            currentPrefs = new UserPreferences();
        }
    }

    public void reload() {
        Optional<UserPreferences> loaded = fileManager.load();
        loaded.ifPresent(p -> this.currentPrefs = p);
    }
}