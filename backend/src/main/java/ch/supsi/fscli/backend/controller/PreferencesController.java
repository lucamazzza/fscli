package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.service.PreferencesService;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;


import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class PreferencesController {

    private final PreferencesService service;



    public PreferencesController() {
        this.service = new PreferencesService();
    }

    public UserPreferences getPreferences() {
        return service.getCurrentPrefs();
    }

    public void updatePreferences(Consumer<UserPreferences> modifier) {
        service.updatePreference(modifier);
    }

    public void setLanguage(String language) {
        updatePreferences(p -> p.setLanguage(language));

        switch (language.toLowerCase()) {
            case "en" -> BackendMessageProvider.setLocale(Locale.ENGLISH);
            case "it" -> BackendMessageProvider.setLocale(Locale.ITALIAN);
            default -> BackendMessageProvider.setLocale(Locale.getDefault());
        }
    }

    public void setCmdColumns(int columns) {
        updatePreferences(p -> p.setCmdColumns(columns));
    }

    public void setOutputLines(int lines) {
        updatePreferences(p -> p.setOutputLines(lines));
    }

    public void setLogLines(int lines) {
        updatePreferences(p -> p.setLogLines(lines));
    }

    public void setCmdFont(String font) {
        updatePreferences(p -> p.setCmdFont(font));
    }

    public void setOutputFont(String font) {
        updatePreferences(p -> p.setOutputFont(font));
    }

    public void setLogFont(String font) {
        updatePreferences(p -> p.setLogFont(font));
    }

    public void reloadPreferences() {
        service.reload();
    }

    public void updateOptionalPreference(String key, Optional<String> value) {
        value.ifPresent(v -> {
            switch (key) {
                case "language" -> setLanguage(v);
                case "cmdColumns" -> setCmdColumns(Integer.parseInt(v));
                case "outputLines" -> setOutputLines(Integer.parseInt(v));
                case "logLines" -> setLogLines(Integer.parseInt(v));
                case "cmdFont" -> setCmdFont(v);
                case "outputFont" -> setOutputFont(v);
                case "logFont" -> setLogFont(v);
                default -> PreferencesLogger.logError(
                        BackendMessageProvider.get("error.invalidPreferenceKey") + ": " + key,
                        null
                );
            }
        });
    }
}
