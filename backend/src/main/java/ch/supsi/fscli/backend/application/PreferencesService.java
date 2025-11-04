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
        UserPreferences oldPrefs = new  UserPreferences(currentPrefs);

        modifier.accept(currentPrefs);

        save();

        if (!oldPrefs.getLanguage().equals(currentPrefs.getLanguage())) {
            PreferencesLogger.logInfo(
                    "The language will be changed to " + currentPrefs.getLanguage() + " when the app is restarted."
            );
        }

        if (oldPrefs.getCmdColumns() != currentPrefs.getCmdColumns()) {
            PreferencesLogger.logInfo(
                    "The command columns will be changed to " + currentPrefs.getCmdColumns() + " when the app is restarted."
            );
        }

        if (oldPrefs.getOutputLines() != currentPrefs.getOutputLines()) {
            PreferencesLogger.logInfo(
                    "The output lines will be changed to " + currentPrefs.getOutputLines() + " when the app is restarted."
            );
        }

        if (oldPrefs.getLogLines() != currentPrefs.getLogLines()) {
            PreferencesLogger.logInfo(
                    "The log lines will be changed to " + currentPrefs.getLogLines() + " when the app is restarted."
            );
        }

        if (!oldPrefs.getCmdFont().equals(currentPrefs.getCmdFont())) {
            PreferencesLogger.logInfo(
                    "The command font will be changed to " + currentPrefs.getCmdFont() + " when the app is restarted."
            );
        }

        if (!oldPrefs.getOutputFont().equals(currentPrefs.getOutputFont())) {
            PreferencesLogger.logInfo(
                    "The output font will be changed to " + currentPrefs.getOutputFont() + " when the app is restarted."
            );
        }

        if (!oldPrefs.getLogFont().equals(currentPrefs.getLogFont())) {
            PreferencesLogger.logInfo(
                    "The log font will be changed to " + currentPrefs.getLogFont() + " when the app is restarted."
            );
        }
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
