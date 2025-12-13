package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.event.EventPublisher;
import ch.supsi.fscli.frontend.event.PreferencesEvent;
import ch.supsi.fscli.frontend.util.AppError;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PreferencesModel {
    private final PreferencesController backendController;
    
    @Setter
    private EventPublisher<PreferencesEvent> preferencesEventPublisher;
    
    private Map<String, String> currentPrefs;
    
    private static PreferencesModel instance;

    public static PreferencesModel getInstance() {
        if (instance == null) {
            instance = new PreferencesModel(
                BackendInjector.getInstance(PreferencesController.class)
            );
        }
        return instance;
    }

    PreferencesModel(PreferencesController backendController) {
        this.backendController = backendController;
        this.currentPrefs = loadFromBackend();
    }

    public Map<String, String> loadPreferences() {
        currentPrefs = loadFromBackend();
        notifyEvent(AppError.PREFERENCES_LOADED);
        return new HashMap<>(currentPrefs);
    }

    public void savePreferences(Map<String, String> newPrefs) {
        if (newPrefs == null || newPrefs.isEmpty()) {
            notifyEvent(AppError.PREFERENCES_SAVE_FAILED);
            return;
        }

        try {
            newPrefs.forEach((key, value) ->
                backendController.updateOptionalPreference(key, Optional.of(value))
            );
            currentPrefs = new HashMap<>(newPrefs);
            notifyEvent(AppError.PREFERENCES_SAVED);
        } catch (Exception e) {
            notifyEvent(AppError.PREFERENCES_SAVE_FAILED);
        }
    }

    public Map<String, String> getCurrentPreferences() {
        return new HashMap<>(currentPrefs);
    }

    private Map<String, String> loadFromBackend() {
        UserPreferences prefs = backendController.getPreferences();
        return Map.of(
            "language", prefs.getLanguage(),
            "cmdColumns", String.valueOf(prefs.getCmdColumns()),
            "outputLines", String.valueOf(prefs.getOutputLines()),
            "logLines", String.valueOf(prefs.getLogLines()),
            "cmdFont", prefs.getCmdFont(),
            "outputFont", prefs.getOutputFont(),
            "logFont", prefs.getLogFont()
        );
    }

    private void notifyEvent(AppError error) {
        if (preferencesEventPublisher != null) {
            preferencesEventPublisher.notify(new PreferencesEvent(error));
        }
    }
}
