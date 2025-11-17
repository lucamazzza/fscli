package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.service.PreferencesService;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.frontend.handler.PreferencesHandler;

import java.util.Map;

public class PreferencesModel implements PreferencesHandler {

    private final PreferencesService service;

    public PreferencesModel(PreferencesService service) { this.service = service; }

    @Override
    public void edit(Map<String, String> settings) {
        service.updatePreference(p -> {
            if (settings.containsKey("language")) p.setLanguage(settings.get("language"));
            if (settings.containsKey("cmdColumns")) p.setCmdColumns(Integer.parseInt(settings.get("cmdColumns")));
            if (settings.containsKey("outputLines")) p.setOutputLines(Integer.parseInt(settings.get("outputLines")));
            if (settings.containsKey("logLines")) p.setLogLines(Integer.parseInt(settings.get("logLines")));
            if (settings.containsKey("cmdFont")) p.setCmdFont(settings.get("cmdFont"));
            if (settings.containsKey("outputFont")) p.setOutputFont(settings.get("outputFont"));
            if (settings.containsKey("logFont")) p.setLogFont(settings.get("logFont"));
        });
    }

    @Override
    public Map<String, String> load() {
        UserPreferences p = service.getCurrentPrefs();
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

    public void reload() { service.reload(); }
}
