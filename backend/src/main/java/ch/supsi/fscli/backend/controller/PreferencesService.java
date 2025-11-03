package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.data.PreferencesFileManager;
import ch.supsi.fscli.backend.service.UserPreferences;
import ch.supsi.fscli.backend.util.PreferencesLogger;

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

    private void save() {
        try {
            fileManager.save(currentPrefs);
            PreferencesLogger.logInfo("Preferences saved successfully");
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
        loaded.ifPresent(p -> {
            this.currentPrefs = p;
            PreferencesLogger.logInfo("Preferences reloaded successfully");
        });
    }
}
