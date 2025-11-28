package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.BackendPreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.frontend.handler.PreferencesHandler;

import java.util.Map;
import java.util.Optional;

public class PreferencesModel implements PreferencesHandler {

    private final BackendPreferencesController controller;

    public PreferencesModel(BackendPreferencesController controller) { this.controller = controller; }

    @Override
    public void edit(Map<String, String> settings) {
        settings.forEach((key, value) -> controller.updateOptionalPreference(key, Optional.of(value)));
    }

    @Override
    public Map<String, String> load() {
        UserPreferences p = controller.getPreferences();
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

    public void reload() { controller.reloadPreferences(); }}
